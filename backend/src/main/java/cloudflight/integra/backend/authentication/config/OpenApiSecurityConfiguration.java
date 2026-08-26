package cloudflight.integra.backend.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security wiring for the spec-export run. {@link SecurityConfiguration} locks down {@code /v3/api-docs}
 * too, which leaves {@code generateAngularClient} with no spec to read.
 */
@Configuration
@Profile("openapi")
public class OpenApiSecurityConfiguration {

    /**
     * Builds a permissive filter chain for the spec-export run.
     *
     * @param http the security builder provided by Spring
     * @return a filter chain that allows every request
     * @throws Exception if the chain cannot be built
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain openApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
