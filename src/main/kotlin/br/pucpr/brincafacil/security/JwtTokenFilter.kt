package br.pucpr.brincafacil.security

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class JwtTokenFilter(
    private val jwt: JWT,
): GenericFilterBean() {
    override fun doFilter(
        req: ServletRequest?,
        res: ServletResponse?,
        chain: FilterChain?
    ) {
        try {
            jwt.extract(req as HttpServletRequest)?.also {
                SecurityContextHolder.getContext().authentication = it
            }
        } catch (ex: JwtException) {
            log.debug("JWT inválido", ex)
        } catch (ex: IllegalArgumentException) {
            log.debug("JWT inválido", ex)
        }

        chain?.doFilter(req, res)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtTokenFilter::class.java)
    }
}