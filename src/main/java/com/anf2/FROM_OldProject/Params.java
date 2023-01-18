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
    private String tableUrl;
    private String tableUser;
    private String tablePassword;
    private String fileType;
    private Boolean SFTP;
    private String localDirectory;
    private String SFTPhost;
    private Integer SFTPport;
    private String SFTPpath;
    private String SFTPuser;
    private String SFTPpassword;
    private Boolean deleteSourceFile;
    private String charset;
    private Boolean latestFile;
    private String maskFile; //m3ua_sctp_path_20220731
    private String tableName;
    private Boolean truncateDestTable;
    private Integer skipLine;
    private Character splitter;
    private String numberColumns;
    private String nameColumns;
    private String replaceSign;
    private Boolean updateCheckerTable;


    public Params getBean(String tableUrl,
                          String tableUser,
                          String tablePassword,
                          String fileType,
                          Boolean SFTP,
                          String localDirectory,
                          String SFTPhost,
                          Integer SFTPport,
                          String SFTPpath,
                          String SFTPuser,
                          String SFTPpassword,
                          Boolean deleteSourceFile,
                          String charset,
                          Boolean latestFile,
                          String maskFile,
                          String tableName,
                          Boolean truncateDestTable,
                          Integer skipLine,
                          Character splitter,
                          String numberColumns,
                          String nameColumns,
                          String replaceSign,
                          Boolean updateCheckerTable) {

        return new Params(this.tableUrl,
                this.tableUser,
                this.tablePassword,
                this.fileType,
                this.SFTP,
                this.localDirectory,
                this.SFTPhost,
                this.SFTPport,
                this.SFTPpath,
                this.SFTPuser,
                this.SFTPpassword,
                this.deleteSourceFile,
                this.charset,
                this.latestFile,
                this.maskFile,
                this.tableName,
                this.truncateDestTable,
                this.skipLine,
                this.splitter,
                this.numberColumns,
                this.nameColumns,
                this.replaceSign,
                this.updateCheckerTable);
    }
}