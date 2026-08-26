package cloudflight.integra.backend.authentication.config;

import cloudflight.integra.backend.authentication.AuthEntryPointJwt;
import cloudflight.integra.backend.authentication.AuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final AuthenticationTokenFilter authenticationTokenFilter;
    private final AuthEntryPointJwt authEntryPointJwt;

    public SecurityConfiguration(
            AuthenticationTokenFilter authenticationTokenFilter, AuthEntryPointJwt authEntryPointJwt) {
        this.authenticationTokenFilter = authenticationTokenFilter;
        this.authEntryPointJwt = authEntryPointJwt;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(
                        AbstractHttpConfigurer
                                ::disable) // todo: this can limit so only our site sends requests to this api
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // openapi & swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()

                        // login/register
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // venues
                        .requestMatchers(HttpMethod.GET, "/api/venues/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN", "PARTICIPANT")
                        .requestMatchers("/api/venues/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN")

                        // users
                        .requestMatchers("/api/users/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN", "PARTICIPANT")

                        // todo:add for notification when is implemented get for user

                        // inventory
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN", "PARTICIPANT")
                        .requestMatchers("/api/inventory/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN")

                        // resources
                        .requestMatchers(HttpMethod.GET, "/api/resources/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN", "PARTICIPANT")
                        .requestMatchers("/api/resources/**")
                        .hasAnyRole("ADMIN", "VENUE_ADMIN")

                        // everything else
                        .anyRequest()
                        .hasRole("ADMIN"))
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
