package com.anf2.esbparsecsvtransfertoapi.service;

import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;

public interface FileService {
    String downloadCsvDocument(FileParams fileParams);
    String transferDataCsvDocumentToApi(FileParams fileParams);
}
