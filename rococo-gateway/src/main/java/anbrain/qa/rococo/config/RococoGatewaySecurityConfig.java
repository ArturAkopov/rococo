package anbrain.qa.rococo.config;

import anbrain.qa.rococo.cors.CorsCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@Profile({"local", "docker"})
public class RococoGatewaySecurityConfig {

    private final CorsCustomizer corsCustomizer;

    @Autowired
    public RococoGatewaySecurityConfig(CorsCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);

        http.authorizeHttpRequests(customizer ->
                        customizer.requestMatchers(
                                        antMatcher(HttpMethod.GET, "/api/session"),
                                        antMatcher(HttpMethod.GET, "/api/artist/**"),
                                        antMatcher(HttpMethod.GET, "/api/museum/**"),
                                        antMatcher(HttpMethod.GET, "/api/painting/**"),
                                        antMatcher("/actuator/health"),
                                        antMatcher("/v3/api-docs/**"),
                                        antMatcher("/swagger-ui/**"),
                                        antMatcher("/swagger-ui.html"),
                                        antMatcher("/webjars/**")
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint((request, response, authException) -> {
                                    throw authException;
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    throw accessDeniedException;
                                })
                );
        return http.build();
    }
}
