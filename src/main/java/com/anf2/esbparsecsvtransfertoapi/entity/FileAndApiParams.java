package com.anf2.esbparsecsvtransfertoapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by stepan.anfinogenov.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileAndApiParams extends FileParams{
    private String apiPath;
}
