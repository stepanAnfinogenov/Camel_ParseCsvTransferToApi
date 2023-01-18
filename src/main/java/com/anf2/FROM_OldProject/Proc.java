package com.anf2.FROM_OldProject;

import com.jcraft.jsch.*;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.anf2.FROM_OldProject.Const.*;

/**
 * Created by stepan.anfinogenov on 2022.
 */

public class Proc implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(Proc.class);

    private String oldestFileName = "";
    private Connection connection = null;

    @Override
    public void process(Exchange exchange) throws Exception {
        LOG.info("\nSTART PROCESS IN Proc");
        Params params = (Params) exchange.getIn().getBody();
        LOG.info("\n params: " + params);

        File file = copyFile(params);
        LOG.info("\n file: " + file);

        int inserted = call(params);
//        updateChecker(insertedRows, params);

        LOG.info("\nEND process in Proc, inserted: " + inserted);
    }

    void start(String url, String usr, String pass) {
        LOG.info("\n IN Proc -> start start");
        Locale.setDefault(Locale.ENGLISH);
        LOG.info("\nGetting connection to: " + url + "\nuser: " + usr + "\npassword: " + pass);
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        } catch (SQLException e) {
            LOG.info("\nIN Proc -> start/SQLException_1" + e);
            throw new RuntimeException(e);
        }
        try {
            connection = DriverManager.getConnection(url, usr, pass);
            connection.setAutoCommit(AUTO_COMMIT_MODE);
        } catch (SQLException e) {
            LOG.info("IN Proc -> start/SQLException_2" + e);
            throw new RuntimeException(e);
        }
    }

    void stop() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


    private int call(Params params) throws Exception {
        LOG.info("\n IN Proc -> call starts");
        try {
            try {
                LOG.info("\n IN Proc -> call/try starts");
                start(params.getTableUrl(), params.getTableUser(), params.getTablePassword());
//                createIfNotExists(params);
                truncateTable(params);
                return insertRows(params);
            } finally {
                LOG.info("\n IN Proc -> call/finally");
                stop();
            }
        } catch (Exception e) {
            LOG.info("\n IN Proc -> call/Exception: " + e);
            e.printStackTrace();

        }
        return 0;
    }

    /**
     * get the latest file from remote directory by sftp and put it to local directory
     * it's necessary to create folder "/esb/exchange/esb_utils_files/"
     */
    public String transferFileBySftp(String username, String host, int port, String password, String maskFileName, String fileType, String SftpDirectory, String localDirectory) {
        LOG.info("\n IN Proc -> transferFileBySftp starts");

        String curren_dir = "empty";

        JSch jsch = new JSch();
        Session session = null;
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session = jsch.getSession(username, host, port);
            session.setConfig(config);
            session.setPassword(password);
            LOG.info("\n----------------\nusername: " + username + "\nString host: " + host + "\nint port: " + port + "\nString password: " + password + "\nmaskFileName: " + maskFileName + "\nString fileType: " + fileType + "\nString SftpDirectory: " + SftpDirectory + "\nString localDirectory: " + localDirectory + "\n-------------------");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.cd(SftpDirectory);
            curren_dir = sftpChannel.pwd();
            LOG.info("\ncurren_dir: " + curren_dir);
            List<ChannelSftp.LsEntry> allFilesInDirectory = sftpChannel.ls("*");

            int oldestFileDate = 0;

            for (ChannelSftp.LsEntry lsEntry : allFilesInDirectory) {
                if (lsEntry.getFilename().startsWith(maskFileName)) {
                    String currentFileName = lsEntry.getFilename();
                    int currentFileDate = Integer.parseInt(currentFileName.substring(currentFileName.length() - fileType.length() - 8, currentFileName.length() - fileType.length()));
                    if (currentFileDate > oldestFileDate) {
                        oldestFileDate = currentFileDate;
                        oldestFileName = currentFileName;
                    }
                }
            }
            LOG.info("\noldestFileName: " + oldestFileName);

            sftpChannel.get(oldestFileName, (localDirectory + oldestFileName));
            LOG.info("\nFile {} copied ", oldestFileName);

            sftpChannel.exit();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error(e.getMessage());
        } catch (SftpException e) {
            LOG.error(e.getMessage());
        }
        return localDirectory + oldestFileName;
    }

    private File copyFile(Params params) throws IOException {
        LOG.info("\n IN Proc -> getFile");

        if (params.getSFTP()) {
            LOG.info("\n IN Proc -> getFile(params.isSFTP() - true)");

            return getFileFromDirectoryBySFTP(params);
        } else {
            LOG.info("\n IN Proc -> getFile(params.isSFTP() - false)");
            return getFileFromDirectory(params);
        }
    }

    private File getFileFromDirectoryBySFTP(Params params) throws IOException {
        LOG.info("\n IN Proc -> getFileFromDirectoryBySFTP");
        String filePath = transferFileBySftp(params.getSFTPuser(),
                params.getSFTPhost(),
                params.getSFTPport(),
                params.getSFTPpassword(),
                params.getMaskFile(),
                params.getFileType(),
                params.getSFTPpath(),
                params.getLocalDirectory()
        );

        return getFileFromDirectory(filePath);
    }

    private File getFileFromDirectory(String filePath) {
        LOG.info("\n IN Proc -> getFileFromDirectory with Params");

        File resultFile = new File(filePath);

        LOG.info("\n IN Proc -> getFileFromDirectory FILE NAME: " + resultFile.getName());

        return resultFile;
    }

    private File getFileFromDirectory(Params params) {
        LOG.info("\n IN Proc -> getFileFromDirectory with Params");

        StringBuilder path = new StringBuilder();

        path.append("/esb/exchange/tmp/")
                .append(params.getMaskFile());

        File resultFile = new File(path.toString());

        LOG.info("\n IN Proc -> getFileFromDirectory FILE NAME: " + resultFile.getName());

        return resultFile;
    }

//    /**
//     * to work method properly it's necessary to create next procedure into DB:
//     *
//     * CREATE OR REPLACE PROCEDURE create_table_if_doesnt_exist(
//     *   p_table_name VARCHAR2,
//     *   create_table_query VARCHAR2
//     * ) AUTHID CURRENT_USER IS
//     *   n NUMBER;
//     * BEGIN
//     *   SELECT COUNT(*) INTO n FROM user_tables WHERE table_name = UPPER(p_table_name);
//     *   IF (n = 0) THEN
//     *     EXECUTE IMMEDIATE create_table_query;
//     *   END IF;
//     * END;
//     *
//     */
//    private void createIfNotExists(Params params){
//        LOG.info("\n IN Proc->createIfNotExists");
//
//            StringBuilder accumulatorSQL = new StringBuilder("call create_table_if_doesnt_exist('");
//            accumulatorSQL
//                    .append(params.getTableName())
//                    .append("', 'CREATE TABLE ")
//                    .append(params.getTableName())
//                    .append()
//                    //TODO
//            ;
//
//            try (Statement stmt = connection.createStatement()) {
//                stmt.setEscapeProcessing(false);
//                stmt.addBatch(accumulatorSQL.toString());
//                stmt.executeBatch();
//            } catch (SQLException e) {
//                LOG.error("\n SQLException IN Proc->truncateTable failed to truncate table: " + params.getTableName() + e);
//            }
//    }

    private void truncateTable(Params params){
        LOG.info("\n IN Proc->truncateTable");
        if (params.getTruncateDestTable()) {
            LOG.info("\n IN Proc->truncateTable getTruncateDestTable - TRUE");

            StringBuilder accumulatorSQL = new StringBuilder("TRUNCATE TABLE ");
            accumulatorSQL.append(params.getTableName());

            try (Statement stmt = connection.createStatement()) {
                stmt.setEscapeProcessing(false);
                stmt.addBatch(accumulatorSQL.toString());
                stmt.executeBatch();
            } catch (SQLException e) {
                LOG.error("\n SQLException IN Proc->truncateTable failed to truncate table: " + params.getTableName() + e);
            }
        } else {
            LOG.info("\n IN Proc->truncateTable getTruncateDestTable - FALSE");
        }
    }

    private int insertRows(Params params) throws IOException {
        LOG.info("\n IN Proc->insertRows starts");

        StringBuilder accumulatorSQL = new StringBuilder("INSERT ALL\n");
        List<String> rows = getRows(params);

        try (Statement stmt = connection.createStatement()) {
            stmt.setEscapeProcessing(false);
            String nameColumns = getColumns(params.getNameColumns());

            for (int i = params.getSkipLine(); i < rows.size(); i++) {
                String row = rows.get(i);

                accumulatorSQL.append("INTO ")
                        .append(params.getTableName())
                        .append(" (")
                        .append(nameColumns)
                        .append(") values ('")
                        .append(getInsertRows(row, params.getSplitter(), getNecessaryColumns(params.getNumberColumns()), params.getReplaceSign()))
                        .append(")\n");
                if (i == 10) { // to show sql insert structure with limit 10 rows
                    LOG.info("\n rows.size() :" + rows.size());
                    LOG.info("\n row :" + row);
                    LOG.info("\n Try to insert " + accumulatorSQL + " ******* + more then " + (rows.size() - 10) + " rows *******");
                }
            }
            accumulatorSQL.append("SELECT * FROM dual");

            stmt.addBatch(accumulatorSQL.toString());
            stmt.executeBatch();
        } catch (SQLException e) {
            LOG.error("\n exception IN Proc->insertRows failed to insert \n" +
                    accumulatorSQL.substring(0, 2000) +
                    "*******************" +
                    "*******************" +
                    "*******************" +
                    accumulatorSQL.substring(accumulatorSQL.length() - 2000, accumulatorSQL.length()) +
                    "\n", e);
            return -1;
        }

        return rows.size();
    }

    private String getInsertRows(String row, Character splitter, List<Integer> necessaryColumns, String replaceSign) {
        StringBuilder accumulator = new StringBuilder();

        String partsRow[] = row.split(String.valueOf(splitter));

        if (replaceSign != null) {
            String replaceSignature[] = replaceSign.split(";");

            for (int i = 0; i < necessaryColumns.size(); i++) {
                if (necessaryColumns.get(i) == Integer.parseInt(replaceSignature[0])) {
                    accumulator
                            .append(replaceSignInRow(partsRow[necessaryColumns.get(i) - 1], replaceSignature[1], replaceSignature[2]))
                            .append("','");
                } else {
                    accumulator
                            .append(partsRow[necessaryColumns.get(i) - 1])
                            .append("','");
                }
            }
        } else {
            for (int i = 0; i < necessaryColumns.size(); i++) {
                accumulator
                        .append(partsRow[necessaryColumns.get(i) - 1])
                        .append("','");
            }
        }

        String result = accumulator.toString();

        return result.substring(0, result.length() - 2);
    }

    private String replaceSignInRow(String row, String signSrs, String signDst) {
        return row.replaceFirst(signSrs, signDst);
    }

    private String getColumns(String columnNames) {
        LOG.info("\n  IN Proc->getColumns starts ");
        StringBuilder accumulator = new StringBuilder();

        String names[] = columnNames.split(";");

        for (String element : names) {
            accumulator.append(element).append(",");
        }

        String result = accumulator.toString();

        LOG.info("\n  IN Proc->getColumns result.substring(0, result.length()-2): " + result.substring(0, result.length() - 3));
        return result.substring(0, result.length() - 1);
    }

    private List<String> getRows(Params params) throws IOException {
        LOG.info("\n IN Proc -> getRows starts");
        List<String> result = new ArrayList<>();

        FileInputStream inputStream = null;
        Scanner sc = null;
        if (oldestFileName.equals("")) {
            findLatestFile(params);
        }
        try {
            LOG.info("\n IN Proc -> getRows/try starts");
            inputStream = new FileInputStream(params.getLocalDirectory() + oldestFileName);
            sc = new Scanner(inputStream, params.getCharset());
            LOG.info("\n IN Proc -> getRows/try next");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                result.add(line);
            }

            if (sc.ioException() != null) {
                LOG.info("\n IN Proc -> getRows/ioException");
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }

        return result;
    }

    /**
     * method looks for 8 signs in the  file name before 4 latest signs
     * necessary format file name *******20220718****
     * for example: m3ua_sctp_path_20220718.csv
     */
    private void findLatestFile(Params params) {
        LOG.info("\n IN Proc -> findLatestFile");
        Set<String> listFilesNames =
                Stream.of(new File(LOCAL_DIRECTORY).listFiles())
                        .filter(file -> !file.isDirectory() && file.getName().startsWith(params.getMaskFile()))
                        .map(File::getName)
                        .collect(Collectors.toSet());

        int oldestFileDate = 0;
        for (String currentFileName : listFilesNames) {
            int currentFileDate = Integer.parseInt(currentFileName.substring(currentFileName.length() - params.getFileType().length() - 8, currentFileName.length() - params.getFileType().length()));
            if (currentFileDate > oldestFileDate) {
                oldestFileDate = currentFileDate;
                oldestFileName = currentFileName;
            }
        }
        LOG.info("\n IN Proc -> findLatestFile, oldestFileName: " + oldestFileName);
    }

    private List<Integer> getNecessaryColumns(String columnNumbers) {
        List<Integer> result = new ArrayList<>();

        String[] numbers = columnNumbers.split(";");

        for (String currentNumber : numbers) {
            result.add(Integer.parseInt(currentNumber));
        }

        return result;
    }

//    /**
//     * update checker only if insertedRows > -1
//     */
//    private void updateChecker(int insertedRows, Params params) throws SQLException, IOException {
//        if (insertedRows > -1 && params.getUpdateCheckerTable()) {
//            //TODO
//        }
//    }
}