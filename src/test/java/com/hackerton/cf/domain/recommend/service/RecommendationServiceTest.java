package com.hackerton.cf.domain.recommend.service;

import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.recommend.dto.AiCoverLetterResponse;
import com.hackerton.cf.global.error.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.*;

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

    // -------------------- getRecommendation (8) --------------------

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
        verify(restTemplate).exchange(eq("http://test/recommend"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void getRecommendation_success_202Accepted_returnsBody() {
        ProfileResponse profile = new ProfileResponse();
        Map<String, Object> expected = Map.of("accepted", true);
        ResponseEntity<Map> resp = new ResponseEntity<>(expected, HttpStatus.ACCEPTED);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class))).thenReturn(resp);

        Map<String, Object> result = service.getRecommendation(profile);
        assertEquals(expected, result);
    }

    @Test
    void getRecommendation_sendsApplicationJsonHeader() {
        ProfileResponse profile = new ProfileResponse();
        Map<String, Object> expected = Map.of("k", "v");
        ResponseEntity<Map> resp = new ResponseEntity<>(expected, HttpStatus.OK);
        ArgumentCaptor<HttpEntity<ProfileResponse>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(resp);

        service.getRecommendation(profile);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), captor.capture(), eq(Map.class));
        HttpHeaders headers = captor.getValue().getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void getRecommendation_requestBody_isSameProfileObject() {
        ProfileResponse profile = new ProfileResponse();
        Map<String, Object> expected = Map.of("k", "v");
        ResponseEntity<Map> resp = new ResponseEntity<>(expected, HttpStatus.OK);
        ArgumentCaptor<HttpEntity<ProfileResponse>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(resp);

        service.getRecommendation(profile);

        verify(restTemplate).exchange(anyString(), any(), captor.capture(), eq(Map.class));
        assertSame(profile, captor.getValue().getBody());
    }

    @Test
    void getRecommendation_http5xx_throwsApplicationException() {
        ProfileResponse profile = new ProfileResponse();
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> service.getRecommendation(profile));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getRecommendation_http4xx_throwsApplicationException() {
        ProfileResponse profile = new ProfileResponse();
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> service.getRecommendation(profile));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getRecommendation_timeout_throwsApplicationException() {
        ProfileResponse profile = new ProfileResponse();
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> service.getRecommendation(profile));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getRecommendation_conversionError_throwsApplicationException() {
        ProfileResponse profile = new ProfileResponse();
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new HttpMessageConversionException("parse"));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> service.getRecommendation(profile));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    // -------------------- getModifiedCoverLetter (7) --------------------

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
    void getModifiedCoverLetter_sendsApplicationJsonHeader_andPayload() {
        AiCoverLetterResponse body = new AiCoverLetterResponse("ok");
        ResponseEntity<AiCoverLetterResponse> resp = new ResponseEntity<>(body, HttpStatus.OK);
        ArgumentCaptor<HttpEntity<Map<String, String>>> captor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(AiCoverLetterResponse.class)))
                .thenReturn(resp);

        service.getModifiedCoverLetter("ACME", "BODY");

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), captor.capture(), eq(AiCoverLetterResponse.class));
        HttpEntity<Map<String, String>> entity = captor.getValue();
        assertEquals(MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());
        assertEquals("ACME", entity.getBody().get("company"));
        assertEquals("BODY", entity.getBody().get("content"));
    }

    @Test
    void getModifiedCoverLetter_nullBody_throwsApplicationException() {
        ResponseEntity<AiCoverLetterResponse> resp = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenReturn(resp);

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont"));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_nullContent_throwsApplicationException() {
        AiCoverLetterResponse body = new AiCoverLetterResponse(null);
        ResponseEntity<AiCoverLetterResponse> resp = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenReturn(resp);

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont"));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_http5xx_throwsApplicationException() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont"));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_http4xx_throwsApplicationException() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont"));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_timeout_throwsApplicationException() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont"));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }

    @Test
    void getModifiedCoverLetter_conversionError_throwsApplicationException() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(AiCoverLetterResponse.class)))
                .thenThrow(new HttpMessageConversionException("parse"));

        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> service.getModifiedCoverLetter("comp", "cont"));
        assertEquals(AI_CALL_FAILURE, ex.getErrorCode());
    }
}
