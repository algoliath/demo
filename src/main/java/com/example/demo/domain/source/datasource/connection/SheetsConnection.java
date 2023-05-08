package com.example.demo.domain.source.datasource.connection;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.example.demo.web.auth.Credentials.getCredentials;

@Slf4j
public class SheetsConnection {

    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static Sheets getConnection(String credentials) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }
}
