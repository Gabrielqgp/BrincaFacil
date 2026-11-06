package br.pucpr.brincafacil

import br.pucpr.brincafacil.roles.Role
import br.pucpr.brincafacil.roles.RoleRepository
import br.pucpr.brincafacil.users.User
import br.pucpr.brincafacil.users.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class BootStrapper(
    val roleRepository: RoleRepository,
    val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${APP_ADMIN_PASSWORD:}") private val adminPassword: String
) : ApplicationListener<ContextRefreshedEvent> {

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN") ?: roleRepository
            .save(Role(name = "ADMIN", description = "System Administrator"))
        roleRepository.findByName("PREMIUM") ?: roleRepository.save(
            Role(
                name = "PREMIUM",
                description = "Premium User"
            )
        )
        if (adminPassword.isNotBlank() && userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                name = "Administrator",
                password = passwordEncoder.encode(adminPassword)!!,
                email = "admin@authserver.com"
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }
    }
}