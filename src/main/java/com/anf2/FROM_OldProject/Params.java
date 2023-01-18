package com.anf2.FROM_OldProject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by stepan.anfinogenov.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Params {
    private String user;
    private String SFTPhost;
    private Integer SFTPport;
    private String hostPassword;
    private String maskFile; //document_16012023.csv
    private String fileType;
    private String SFTPdirectory;
    private String localDirectory;
}
