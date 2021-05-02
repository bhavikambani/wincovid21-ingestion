package com.wincovid21.ingestion.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wincovid21.ingestion.domain.IngestionResponse;
import lombok.AllArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component
public class SearchClientHelper {

    private ObjectMapper objectMapper;
    private static final String searchUrl = "localhost:8081/upsert";

    public IngestionResponse makeHttpPostRequest(Object payload) throws IOException {
        HttpPost httpPost = new HttpPost(searchUrl);
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();

            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(payload));
            httpPost.setEntity(entity);
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response = client.execute(httpPost);
            IngestionResponse ingestionResponse = IngestionResponse.<HttpEntity>builder().httpStatus(HttpStatus.OK).result(response.getEntity()).build();
            return ingestionResponse;
        }
        finally {
            httpPost.releaseConnection();
            response.close();
        }
    }
}
