CREATE TABLE topico(
    id BIGINT AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    mensagem VARCHAR(600) NOT NULL,
    data_criacao datetime NOT NULL,
    status VARCHAR(30),
    id_autor BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_autor) REFERENCES usuario(id),
    FOREIGN KEY (id_curso) REFERENCES curso(id)
);