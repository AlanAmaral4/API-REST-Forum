package br.com.forum.forumapi.service

import br.com.forum.forumapi.dto.AtualizarTopicoRequest
import br.com.forum.forumapi.dto.CriarTopicoRequest
import br.com.forum.forumapi.dto.TopicoResponse
import br.com.forum.forumapi.dto.toResponse
import br.com.forum.forumapi.dto.toTopico
import br.com.forum.forumapi.exception.NotFoundException
import br.com.forum.forumapi.model.Topico
import br.com.forum.forumapi.repositories.TopicoRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service


@Service
class TopicoService(
    private val cursoService: CursoService,
    private val usuarioService: UsuarioService,
    private val topicoRepository: TopicoRepository
) {

    @Cacheable("topicos")
    fun listar(
        nomeCurso: String?,
        paginacao: Pageable
    ): Page<TopicoResponse> {
        val topicos = if (nomeCurso == null) {
            topicoRepository.findAll(paginacao)
        } else {
            topicoRepository.findByCursoNomeContainingIgnoreCase(nomeCurso, paginacao)
        }
        return topicos.map { it.toResponse() }
    }

    fun buscarTopicoPorId(id: Long): Topico {
        val topico = topicoRepository.findById(id)
            .orElseThrow { NotFoundException("Tópico de id $id não encontrado") }

        return topico
    }

    fun buscarTopicoResponsePorId(id: Long): TopicoResponse {
        return buscarTopicoPorId(id).toResponse()
    }

    @CacheEvict(value = ["topicos"], allEntries = true)
    @Transactional
    fun criarTopico(
        topicoDto: CriarTopicoRequest
    ): TopicoResponse {
        val curso = cursoService.buscarPorId(topicoDto.curso)
        val autor = usuarioService.buscarPorId(topicoDto.autor)

        val topico = topicoDto.toTopico(
            curso = curso,
            autor = autor
        )

        topicoRepository.save(topico)

        return topico.toResponse()
    }

    @CacheEvict(value = ["topicos"], allEntries = true)
    @Transactional
    fun atualizarTopico(
        id: Long,
        atualizarTopicoRequest: AtualizarTopicoRequest
    ): TopicoResponse {
        val topico = buscarTopicoPorId(id)

        topico.apply {
            titulo = atualizarTopicoRequest.titulo
            mensagem = atualizarTopicoRequest.mensagem
        }

        return topico.toResponse()
    }

    @CacheEvict(value = ["topicos"], allEntries = true)
    @Transactional
    fun deletarTopico(id: Long) {
        if (!topicoRepository.existsById(id)) {
            throw NotFoundException("Tópico de id $id não existe!")
        }
        topicoRepository.deleteById(id)
    }
}