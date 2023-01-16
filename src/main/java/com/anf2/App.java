package com.anf2;

import FROM_MEGAFONE.Proc;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        Proc proc = new Proc();

        proc.transferFileBySftp("ubuntu",
                "18.222.254.18",
                22,
                "c3$fZPW8!6agYL!Z$m%3dA!&?dR$Bzzn",
                "document_16.01.2023.csv",
                ".csv",
                "/home/ubuntu/test1",
                "/home/ubuntu/app/dirForFilesJenkinsfile"
        );
    }
}
