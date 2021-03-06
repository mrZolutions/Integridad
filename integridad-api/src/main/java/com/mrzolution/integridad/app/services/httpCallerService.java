package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.UserClient;
import com.mrzolution.integridad.app.domain.UserIntegridad;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class httpCallerService {
    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String post(String url, String json, UserClient userClient) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, json);
        Request request;
        if(userClient != null){
            request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Key", userClient.getApiKey())
                    .addHeader("X-Password", userClient.getEPassword())
                    .url(url)
                    .post(body)
                    .build();
        } else {
            request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .url(url)
                    .post(body)
                    .build();
        }

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String postAPIMrz(String url, String json, UserIntegridad user) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, json);
        Request request;
        request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + user.getToken())
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


}
