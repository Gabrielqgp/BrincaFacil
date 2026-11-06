package br.pucpr.brincafacil.security

import br.pucpr.brincafacil.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

@Component
class JWT(private val properties: SecurityProperties) {
    fun createToken(user: User): String =
        UserToken(user).let {
            Jwts.builder().json(JacksonSerializer())
                .signWith(Keys.hmacShaKeyFor(properties.secret.toByteArray()))
                .issuedAt(utcNow().toDate())
                .expiration(utcNow().plusHours(
                    if (it.isAdmin) properties.adminExpireHours else properties.expireHours
                ).toDate())
                .issuer(properties.issuer)
                .subject(it.id.toString())
                .claim(USER_FIELD,it)
                .compact()
        }

    fun extract(req: HttpServletRequest): Authentication? {
            val header = req.getHeader(AUTHORIZATION)
            if (header == null || !header.startsWith("Bearer ")) return null
            val token = header.replace("Bearer ", "")

            val claims = Jwts.parser().json(JacksonDeserializer(mapOf(USER_FIELD to UserToken::class.java)))
                .verifyWith(Keys.hmacShaKeyFor(properties.secret.toByteArray()))
                .build()
                .parseSignedClaims(token)
                .payload

        if(claims.issuer != properties.issuer) return null
        val user = claims.get(USER_FIELD, UserToken::class.java)
        val authotities = user.roles.map {  SimpleGrantedAuthority("ROLE_$it")}
        return UsernamePasswordAuthenticationToken
            .authenticated(user, user.id, authotities)

    }
    companion object {
        val USER_FIELD = "user"

        private fun utcNow() = ZonedDateTime.now(ZoneOffset.UTC)
        private fun ZonedDateTime.toDate() = Date.from(this.toInstant())
    }
}