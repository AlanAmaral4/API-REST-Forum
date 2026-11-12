CREATE TABLE role (
    id   BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE usuario_role (
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

-- Cadastrando os 3 papéis no banco:
INSERT INTO role (id, nome)
VALUES (1, 'ADMIN');

INSERT INTO role (id, nome)
VALUES (2, 'PROFESSOR');

INSERT INTO role (id, nome)
VALUES (3, 'ALUNO');

-- Usuário 1 (ivan@email.com) tem DOIS papéis: ADMIN e PROFESSOR
INSERT INTO usuario_role (usuario_id, role_id) VALUES (1, 1);
INSERT INTO usuario_role (usuario_id, role_id) VALUES (1, 2);

-- Usuário 2 (maria@email.com) tem DOIS papéis: PROFESSOR e ALUNO
INSERT INTO usuario_role (usuario_id, role_id) VALUES (2, 2);
INSERT INTO usuario_role (usuario_id, role_id) VALUES (2, 3);

-- Usuário 3 (juliana@email.com) tem apenas UM papel: ALUNO
INSERT INTO usuario_role (usuario_id, role_id) VALUES (3, 3);