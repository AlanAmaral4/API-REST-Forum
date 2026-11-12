package br.com.forum.forumapi.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class TokenService(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private val expirationMillis = 3600000L // 1 hora

    private fun getSigningKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    fun gerarToken(username: String): String {
        val agora = Date()
        val expiracao = Date(agora.time + expirationMillis)
        return Jwts.builder()
            .subject(username)
            .issuedAt(agora)
            .expiration(expiracao)
            .signWith(getSigningKey())
            .compact()
    }

    fun validarTokenEObterUsuario(token: String): String? {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: Exception) {
            null
        }
    }
}