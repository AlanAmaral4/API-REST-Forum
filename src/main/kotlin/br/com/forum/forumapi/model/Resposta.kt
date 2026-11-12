package br.com.forum.forumapi.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Resposta (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "id_autor")
    var autor: Usuario,

    @ManyToOne
    @JoinColumn(name = "id_topico")
    var topico: Topico,

    var mensagem: String,
    var dataCriacao: LocalDateTime = LocalDateTime.now(),
    var solucao: Boolean = false
)