package com.anf2.esbparsecsvtransfertoapi.controller;

import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;
import com.anf2.esbparsecsvtransfertoapi.service.FileParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestControllers {

    @Autowired
    private FileParamsService fileParamsService;

    @PostMapping("/download/document")
    public String askExecuteDownloadingDocument (@RequestBody FileParams fileParams) {
        String fileName = fileParamsService.downloadCsvDocument(fileParams);

        return "document downloadet from .... to ....";
    }
}
