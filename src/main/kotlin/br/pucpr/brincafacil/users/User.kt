package br.pucpr.brincafacil.users

import br.pucpr.brincafacil.roles.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Email

@Entity
@Table(name="UserTable")
class User(
    @Id
    @SequenceGenerator(
        name = "user_id_generator",
        sequenceName = "USER_TABLE_SEQ",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    var id: Long? = null,

    @Email
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, length = 72)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @ManyToMany
    @JoinTable(
        name = "UserRole",
        joinColumns = [JoinColumn(name="idUser")],
        inverseJoinColumns = [JoinColumn(name="idRole")],
    )
    var roles: MutableSet<Role> = mutableSetOf()
)