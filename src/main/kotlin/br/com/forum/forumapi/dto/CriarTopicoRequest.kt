package br.com.forum.forumapi.dto

import br.com.forum.forumapi.model.Curso
import br.com.forum.forumapi.model.Topico
import br.com.forum.forumapi.model.Usuario
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Data transfer object
data class CriarTopicoRequest(
    @NotBlank
    @Size(min = 5, max = 100)
    val titulo: String,

    @NotBlank
    @Size(min = 5, max = 600)
    val mensagem: String,

    val curso: Long,

    val autor: Long,
)

// Função de extensão
fun CriarTopicoRequest.toTopico(
    curso: Curso,
    autor: Usuario
): Topico {
    val topico = Topico(
        mensagem = this.mensagem,
        titulo = this.titulo,
        curso = curso,
        autor = autor
    )
    return topico
}