package br.com.forum.forumapi.security

import br.com.forum.forumapi.model.Usuario
import org.springframework.security.core.userdetails.UserDetails

class UsuarioDetail(
    private val usuario: Usuario
): UserDetails {
    override fun getAuthorities() = usuario.roles

    override fun getPassword() = usuario.senha

    override fun getUsername() = usuario.email
}