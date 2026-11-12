package br.com.forum.forumapi.config

import br.com.forum.forumapi.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf {
                it.disable()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->

                auth.requestMatchers(HttpMethod.GET, "/topicos/**").permitAll()

                auth.requestMatchers(HttpMethod.POST, "/login").permitAll()

                auth.requestMatchers("/hello").permitAll()

                auth.requestMatchers(HttpMethod.POST, "/topicos")
                    .hasAnyRole("ALUNO", "ADMIN", "PROFESSOR")

                auth.requestMatchers(HttpMethod.PUT, "/topicos/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")

                auth.requestMatchers(HttpMethod.DELETE, "/topicos/**")
                    .hasAnyRole("ADMIN")

                auth.anyRequest().authenticated()
            }
            //.httpBasic(Customizer.withDefaults())
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        configuration: AuthenticationConfiguration
    ): AuthenticationManager {
        return configuration.authenticationManager
    }
}