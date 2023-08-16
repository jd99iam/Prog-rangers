package com.prograngers.backend.service.auth;

import com.prograngers.backend.dto.response.auth.google.GoogleTokenResponse;
import com.prograngers.backend.dto.response.auth.google.GoogleUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static com.prograngers.backend.service.auth.MultiValueMapConverter.convertToMultiValueMap;
import static com.prograngers.backend.service.auth.OauthConstant.BEARER_FORMAT;

@Component
public class GoogleOauth {

    private final static String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private final static String USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final String grantType;
    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;
    private final WebClient webClient;

    public GoogleOauth(@Value("${google.grant.type}") String grantType,
                      @Value("${google.client.id}") String clientId,
                      @Value("${google.redirect.uri}") String redirectUri,
                      @Value("${google.client.secret}") String clientSecret,
                      WebClient webClient) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.webClient = webClient;
    }

    public GoogleTokenResponse googleGetToken(String code) {
        return webClient.post()
                .uri(TOKEN_URI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(convertToMultiValueMap(grantType, clientId, redirectUri, code, clientSecret))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    public GoogleUserInfoResponse googleGetUserInfo(String accessToken) {
        return webClient.get()
                .uri(USER_INFO_URI)
                .header(HttpHeaders.AUTHORIZATION, String.format(BEARER_FORMAT, accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfoResponse.class)
                .block();
    }
}
