package com.anf2.esbparsecsvtransfertoapi.controller;

import com.anf2.esbparsecsvtransfertoapi.entity.Employee;
import com.anf2.esbparsecsvtransfertoapi.entity.FileAndApiParams;
import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;
import com.anf2.esbparsecsvtransfertoapi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestControllers {

    @Autowired
    private FileService fileService;

    @PostMapping("/download/document")
    public String DownloadDocumentViaSftp(@RequestBody FileParams fileParams) {
        String fileName = fileService.downloadCsvDocument(fileParams);

        return "document" + fileName + "downloaded from" + fileParams.getSftpHost() + fileParams.getSftpDirectory() + "to " + fileParams.getLocalDirectory();
    }

    @PostMapping("/parsAndTransfer/document")
    public String transferDocumentDataToApi(@RequestBody FileAndApiParams fileAndApiParams) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        List<Employee> employees = fileService.transferDataCsvDocumentToApi(fileAndApiParams);
        List<Employee> createdEmployee = new ArrayList<>();

        for (Employee employee : employees) {
            createdEmployee.add(restTemplate.postForObject(fileAndApiParams.getApiPath(), employee, Employee.class));
        }

        return "createdEmployees :" + createdEmployee;
    }
}
