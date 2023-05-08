package com.example.demo.domain.source.datasource.connection;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.example.demo.web.auth.Credentials.getCredentials;

@Slf4j
public class DriveConnection {

    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static Drive getConnection(String credentials)  {
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Drive service;
        try {
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return service;
    }

}
