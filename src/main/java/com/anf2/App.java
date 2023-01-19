package com.anf2;

import com.anf2.model.FileParams;
import com.anf2.transporters.FileTransporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Stepan Anfinogenov 2023
 */
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws Exception {
        LOG.info("\n--- 1\n");

        FileParams fileParams = new FileParams(
                "ubuntu",
                "18.221.159.73",
                22,
                "password",
                "document_",
                ".csv",
                "/home/ubuntu/test1/",
                "/home/ubuntu/app/dirForFiles/");  //for server
//                "src/main/resources/downloadedFiles");  //for local testing
        LOG.info("\n--- 2\n");

        new FileTransporter().getFile(fileParams);
        LOG.info("\n--- 3\n");
    }
}
