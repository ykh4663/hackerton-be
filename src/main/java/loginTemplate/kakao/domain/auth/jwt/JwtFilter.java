package loginTemplate.kakao.domain.auth.jwt;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loginTemplate.kakao.global.error.AuthErrorCode;
import loginTemplate.kakao.global.error.ErrorCode;
import loginTemplate.kakao.global.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        accessToken = accessToken.substring(7);

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter writer = response.getWriter();
            String errorMessage = createErrorMessage(AuthErrorCode.EXPIRED_TOKEN);
            writer.print(errorMessage);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access_token")) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter writer = response.getWriter();
            String errorMessage = createErrorMessage(AuthErrorCode.INVALID_TOKEN_CATEGORY);
            writer.print(errorMessage);
            return;
        }

        Long userId = jwtUtil.getUserId(accessToken);


        Authentication authToken =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String createErrorMessage(ErrorCode errorCode) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode.name(), errorCode.getMessage(), null);
        String errorMessage;
        try {
            errorMessage = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return errorMessage;
    }
}