package com.anf2.esbparsecsvtransfertoapi.service;

import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;

public interface FileParamsService {
    String downloadCsvDocument(FileParams fileParams);
}
