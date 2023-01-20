package com.anf2.esbparsecsvtransfertoapi.service;

import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileParamsServiceImpl implements FileParamsService {
    private static final Logger LOG = LoggerFactory.getLogger(FileParamsServiceImpl.class);

    @Override
    public String downloadCsvDocument(FileParams fileParams) {

        return getFile(fileParams);
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

    /**
     * get the latest file from remote directory by sftp and put it to local directory
     * it's necessary to create folder "/esb/exchange/esb_utils_files/"
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

