package com.example.demo.util.connection;

import com.example.demo.domain.datasource.Source;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.Docs;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.example.demo.web.auth.Credentials.getCredentials;

public class DocsConnection {

    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static Docs getConnection(Source dataSource) throws GeneralSecurityException, IOException {
        String credentials = (String) dataSource.getData("credentials").orElse("");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Docs service = new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
        return service;
    }




}
