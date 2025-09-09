package com.hackerton.cf.domain.recommend.service;

import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.recommend.dto.AiCoverLetterResponse;

import com.hackerton.cf.global.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.HttpMessageConversionException;

import java.util.Map;

import static com.hackerton.cf.global.error.AiException.AI_CALL_FAILURE;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RestTemplate restTemplate;
    @Value("${recommend.api.url}")
    private String recommendUrl;

    @Value("${recommend.api.cover-letter}")
    private String coverLetterUrl;

    public Map<String, Object> getRecommendation(ProfileResponse profile) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProfileResponse> request = new HttpEntity<>(profile, headers);
        try {
            ResponseEntity<Map> resp = restTemplate.exchange(
                    recommendUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            // 바디 null 방어(있으면 유지)
            if (resp.getBody() == null) {
                throw new ApplicationException(AI_CALL_FAILURE);
            }
            return resp.getBody();
        } catch (HttpMessageConversionException | RestClientException e) { // ★핵심
            throw new ApplicationException(AI_CALL_FAILURE);
        }
    }

    public String getModifiedCoverLetter(String company, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of("company", company, "content", content);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<AiCoverLetterResponse> resp = restTemplate.exchange(
                    coverLetterUrl,
                    HttpMethod.POST,
                    request,
                    AiCoverLetterResponse.class
            );
            AiCoverLetterResponse body = resp.getBody();
            if (body == null || body.getModifiedContent() == null) {
                throw new ApplicationException(AI_CALL_FAILURE);
            }
            return body.getModifiedContent();
        } catch (HttpMessageConversionException | RestClientException e) { // ★핵심
            throw new ApplicationException(AI_CALL_FAILURE);
        }
    }




}