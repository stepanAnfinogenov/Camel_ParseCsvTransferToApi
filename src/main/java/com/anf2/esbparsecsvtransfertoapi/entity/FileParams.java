package com.anf2.esbparsecsvtransfertoapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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
    private String maskFile; //document_16012023.csv
    private String fileType;
    private String sftpDirectory;
    private String localDirectory;
}
