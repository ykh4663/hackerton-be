package com.hackerton.cf.global.config;

import com.hackerton.cf.domain.auth.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;



import org.springframework.http.HttpMethod;

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        // 화이트리스트를 MvcRequestMatcher 배열로 변환
        MvcRequestMatcher[] permitAll = SecurityUrls.AUTH_WHITELIST.stream()
                .map(mvc::pattern)
                .toArray(MvcRequestMatcher[]::new);

        http
                // CORS
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                // CSRF/Form/Logout 비활성
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // 세션 미사용
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAll).permitAll()                                   // 화이트리스트
                        .requestMatchers(mvc.pattern(HttpMethod.OPTIONS, "/**")).permitAll()      // 프리플라이트
                        .anyRequest().authenticated()                                             // 나머지는 인증 필요
                )
                // JWT 필터(UsernamePasswordAuthenticationFilter 앞에 둠)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }

    /** CORS 설정 (패턴 기반 허용 오리진) */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOriginPatterns(SecurityUrls.ALLOWED_ORIGINS);
        conf.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        conf.setAllowedHeaders(List.of("*"));
        conf.setAllowCredentials(true);
        conf.setMaxAge(3600L);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}