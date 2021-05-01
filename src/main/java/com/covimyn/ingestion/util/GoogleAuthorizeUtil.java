package com.covimyn.ingestion.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class GoogleAuthorizeUtil {
    public static Credential authorize() throws IOException {
        InputStream in = GoogleAuthorizeUtil.class.getResourceAsStream("/google-sheets-client-secret.json");

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS,SheetsScopes.DRIVE);

        Credential credential = GoogleCredential.fromStream(in)
                .createScoped(scopes);
        return credential;
    }

}
