package com.anf2.esbparsecsvtransfertoapi.service;

import com.anf2.esbparsecsvtransfertoapi.entity.Employee;
import com.anf2.esbparsecsvtransfertoapi.entity.FileAndApiParams;
import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String downloadCsvDocument(FileParams fileParams) {
        return getFile(fileParams);
    }

    @Override
    public List<Employee> transferDataCsvDocumentToApi(FileAndApiParams fileAndApiParams) throws IOException {
        String fileName = getFile(fileAndApiParams);
        List<Employee> employees = parsFile(fileName, fileAndApiParams);

        return employees;
    }


    private List<Employee> parsFile(String fileName, FileAndApiParams fileAndApiParams) throws IOException {
        LOG.info("\n parsFile starts");
        List<Employee> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        reader.readLine(); // skip firs line
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.equals("")) {
                result.add(lineToEmployee(line));
            }
        }

        reader.close();

        return result;
    }

    private Employee lineToEmployee(String line) {
        String[] fields = line.split(";");

        return new Employee(fields[0], fields[1], fields[2], Integer.parseInt(fields[3]));
    }


    private String getFile(FileParams fileParams) {
        LOG.info("\n IN Proc -> call starts");

        return copyLatestFileBySftp(
                fileParams.getUser(),
                fileParams.getSftpHost(),
                fileParams.getSftpPort(),
                fileParams.getHostPassword(),
                fileParams.getMaskFile(),
                fileParams.getFileType(),
                fileParams.getSftpDirectory(),
                fileParams.getLocalDirectory());
    }

    private String getFile(FileAndApiParams fileAndApiParams) {
        LOG.info("\n IN Proc -> call starts");

        return copyLatestFileBySftp(
                fileAndApiParams.getUser(),
                fileAndApiParams.getSftpHost(),
                fileAndApiParams.getSftpPort(),
                fileAndApiParams.getHostPassword(),
                fileAndApiParams.getMaskFile(),
                fileAndApiParams.getFileType(),
                fileAndApiParams.getSftpDirectory(),
                fileAndApiParams.getLocalDirectory());
    }

    /**
     * get the latest file from remote directory by sftp and put it to local directory
     */
    private String copyLatestFileBySftp(String username, String host, int port, String password, String maskFileName, String fileType, String SftpDirectory, String localDirectory) {
        LOG.info("\ncopyLatestFileBySftp starts");
        LOG.info("\n-------arguments into copyLatestFileBySftp--------\nusername: " + username + "\nString host: " + host + "\nint port: " + port + "\nString password: " + password + "\nmaskFileName: " + maskFileName + "\nString fileType: " + fileType + "\nString SftpDirectory: " + SftpDirectory + "\nString localDirectory: " + localDirectory + "\n-------------------");

        String curren_dir;
        JSch jsch = new JSch();
        Session session;
        String latestFileName = "";
        int latestFileDate = 0;

        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session = jsch.getSession(username, host, port);
            session.setConfig(config);
            session.setPassword(password);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.cd(SftpDirectory);
            curren_dir = sftpChannel.pwd();
            LOG.info("\ncurren_dir: " + curren_dir);
            List<ChannelSftp.LsEntry> allFilesInDirectory = sftpChannel.ls("*");

            for (ChannelSftp.LsEntry lsEntry : allFilesInDirectory) {
                if (lsEntry.getFilename().startsWith(maskFileName)) {
                    String currentFileName = lsEntry.getFilename();
                    int currentFileDate = Integer.parseInt(currentFileName.substring(currentFileName.length() - fileType.length() - 8, currentFileName.length() - fileType.length()));
                    if (currentFileDate > latestFileDate) {
                        latestFileDate = currentFileDate;
                        latestFileName = currentFileName;
                    }
                }
            }

            LOG.info("\nlatestFileName: " + latestFileName);
            sftpChannel.get(latestFileName, (localDirectory + latestFileName));
            LOG.info("\nFile {} copied ", latestFileName);

            sftpChannel.exit();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error(e.getMessage());
        } catch (SftpException e) {
            LOG.error(e.getMessage());
        }

        return localDirectory + latestFileName;
    }
}

