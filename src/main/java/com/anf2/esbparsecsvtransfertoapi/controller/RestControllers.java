package com.anf2.esbparsecsvtransfertoapi.controller;

import com.anf2.esbparsecsvtransfertoapi.entity.FileAndApiParams;
import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;
import com.anf2.esbparsecsvtransfertoapi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestControllers {

    @Autowired
    private FileService fileService;

    @PostMapping("/download/document")
    public String DownloadDocumentViaSftp (@RequestBody FileParams fileParams) {
        String fileName = fileService.downloadCsvDocument(fileParams);

        return "document" + fileName + "downloaded from" + fileParams.getSftpHost() + fileParams.getSftpDirectory() + "to " + fileParams.getLocalDirectory();
    }
    @PostMapping("/parsAndTransfer/document")
    public String transferDocumentDataToApi (@RequestBody FileAndApiParams fileAndApiParams) {
        String fileName = fileService.transferDataCsvDocumentToApi(fileAndApiParams);

        return "document" + fileName + "read from" + fileAndApiParams.getSftpHost() + fileAndApiParams.getSftpDirectory() +
                "parsed and transfer to " + fileAndApiParams.getApiPath();
    }
}
