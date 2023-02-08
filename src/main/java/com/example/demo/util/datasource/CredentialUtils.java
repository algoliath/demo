package com.example.demo.util.datasource;

public class CredentialUtils {

    public static String getCredentialPath(String path){
        return "/auth/" + path;
    }
}
