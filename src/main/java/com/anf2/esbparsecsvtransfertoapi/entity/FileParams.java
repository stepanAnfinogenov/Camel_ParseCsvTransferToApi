package com.anf2.esbparsecsvtransfertoapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by stepan.anfinogenov.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileParams {
    private String user;
    private String sftpHost;
    private Integer sftpPort;
    private String hostPassword;
    private String maskFile; //name of doc: document_16012023.csv
    private String fileType;
    private String sftpDirectory;
    private String localDirectory;
}
