package br.pucpr.brincafacil.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@SecurityScheme(
    name = "jwt", //Nome que quiser
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",

)

    @EnableMethodSecurity
class SecurityConfig(
    private val jwtFilter: JwtTokenFilter
){
    @Bean
    @Profile("dev")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun h2ConsoleSecurity(http: HttpSecurity): SecurityFilterChain {
        return http
            .securityMatcher(PathRequest.toH2Console())
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }
            .build()
    }

    @Bean
    fun filterChain(security: HttpSecurity): SecurityFilterChain = security
        .sessionManagement { it.sessionCreationPolicy(STATELESS)}
        .cors (Customizer.withDefaults())
        .csrf { it.disable() }
        .headers { it.frameOptions { fo -> fo.disable() } }
        .authorizeHttpRequests { requests ->
            requests
                .requestMatchers(HttpMethod.GET, "/health", "/brinquedos/**", "/decoracoes/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                .requestMatchers("/h2/console/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        }
        .addFilterBefore(jwtFilter, BasicAuthenticationFilter::class.java)
        .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource() =
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", CorsConfiguration().apply {
                addAllowedMethod("*")
                addAllowedOrigin("*")
                addAllowedHeader("*")
            })
        }
}