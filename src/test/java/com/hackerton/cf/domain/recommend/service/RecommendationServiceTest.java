package com.hackerton.cf.domain.recommend.service;

import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.recommend.dto.AiCoverLetterResponse;
import com.hackerton.cf.global.error.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.hackerton.cf.global.error.AiException.AI_CALL_FAILURE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecommendationService service;

    @BeforeEach
    void setUp() {
        // @Value 필드 주입
        ReflectionTestUtils.setField(service, "recommendUrl", "http://test/recommend");
        ReflectionTestUtils.setField(service, "coverLetterUrl", "http://test/cover-letter");
    }

    @Test
    void getRecommendation_success_returnsMap() {
        ProfileResponse profile = new ProfileResponse();
        Map<String, Object> expected = Map.of("rec", "ok");
        ResponseEntity<Map> resp = new ResponseEntity<>(expected, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://test/recommend"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(resp);

        Map<String, Object> result = service.getRecommendation(profile);

        assertEquals(expected, result);
        verify(restTemplate).exchange(
                eq("http://test/recommend"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    void getRecommendation_restClientException_throwsApplicationException() {
        ProfileResponse profile = new ProfileResponse();
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new RestClientException("fail"));

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getRecommendation(profile)
        );
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_success_returnsContent() {
        AiCoverLetterResponse body = new AiCoverLetterResponse("modified");
        ResponseEntity<AiCoverLetterResponse> resp = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://test/cover-letter"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(AiCoverLetterResponse.class)
        )).thenReturn(resp);

        String result = service.getModifiedCoverLetter("comp", "cont");

        assertEquals("modified", result);
    }

    @Test
    void getModifiedCoverLetter_nullBody_throwsApplicationException() {
        ResponseEntity<AiCoverLetterResponse> resp = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenReturn(resp);

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont")
        );
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_nullContent_throwsApplicationException() {
        AiCoverLetterResponse body = new AiCoverLetterResponse(null);
        ResponseEntity<AiCoverLetterResponse> resp = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenReturn(resp);

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont")
        );
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_restClientException_throwsApplicationException() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenThrow(new RestClientException("fail"));

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont")
        );
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }
}
