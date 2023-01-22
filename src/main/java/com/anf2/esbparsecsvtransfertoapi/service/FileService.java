package com.anf2.esbparsecsvtransfertoapi.service;

import com.anf2.esbparsecsvtransfertoapi.entity.Employee;
import com.anf2.esbparsecsvtransfertoapi.entity.FileAndApiParams;
import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;

import java.io.IOException;
import java.util.List;

public interface FileService {
    String downloadCsvDocument(FileParams fileParams);
    List<Employee> transferDataCsvDocumentToApi(FileAndApiParams fileAndApiParams) throws IOException;
}
