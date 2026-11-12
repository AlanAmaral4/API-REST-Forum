package br.com.forum.forumapi.controller

import br.com.forum.forumapi.security.TokenService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class LoginRequest(
    @NotBlank
    val email: String,
    @NotBlank
    val senha: String
)

data class TokenResponse(
    val token: String
)

@RestController
@RequestMapping("/login")
class AuthController(
    private val authManager: AuthenticationManager,
    private val tokenService: TokenService
) {
    @PostMapping
    fun autenticar(
        @RequestBody @Valid request: LoginRequest
    ): ResponseEntity<TokenResponse> {
        val authToken = UsernamePasswordAuthenticationToken(
            request.email,
            request.senha
        )
        val authentication = authManager.authenticate(authToken)
        val token = tokenService.gerarToken(authentication.name)
        return ResponseEntity.ok(TokenResponse(token))
    }
}