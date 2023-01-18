package com.anf2;

import com.anf2.FROM_OldProject.Proc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws Exception {
        System.out.println("\n !!!!!!!!!!!!!!!!!!!\n");
        LOG.info("\n--- 1\n");


//        MainRoute mainRoute = new MainRoute();
//        mainRoute.configure();

        Proc proc = new Proc();
        LOG.info("\n--- 2\n");

        proc.transferFileBySftp("ubuntu",
                "18.221.159.73",
                22,
                "password",
                "document_",
                ".csv",
                "/home/ubuntu/test1/",
                "/home/ubuntu/app/dirForFiles/"
        );
        LOG.info("\n--- 3\n");
    }
}
