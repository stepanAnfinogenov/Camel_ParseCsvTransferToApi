package com.anf2.esbparsecsvtransfertoapi.service;

import com.anf2.esbparsecsvtransfertoapi.entity.FileParams;

import java.io.File;

public interface FileParamsService {
    public String downloadCsvDocument(FileParams fileParams);
}
