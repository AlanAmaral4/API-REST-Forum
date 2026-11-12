package br.com.forum.forumapi.service

import br.com.forum.forumapi.exception.NotFoundException
import br.com.forum.forumapi.model.Usuario
import br.com.forum.forumapi.repositories.UsuarioRepository
import br.com.forum.forumapi.security.UsuarioDetail
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository
): UserDetailsService {

    fun buscarPorId(id: Long): Usuario {
        return usuarioRepository.findById(id)
            .orElseThrow { NotFoundException("Usuario de id $id não existe!") }
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val usuario = usuarioRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Usuário com e-mail $username não encontrado")

        return UsuarioDetail(usuario)
    }

}