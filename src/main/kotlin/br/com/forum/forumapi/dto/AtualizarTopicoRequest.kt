package br.com.forum.forumapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class AtualizarTopicoRequest(
    @NotBlank
    @Size(min = 5, max = 100)
    val titulo: String,

    @NotBlank
    @Size(min = 5, max = 600)
    val mensagem: String

)