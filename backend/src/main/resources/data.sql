INSERT INTO tb_usuario (ID, NOME, EMAIL, SENHA, CREATED_AT, UPDATED_AT)
SELECT * FROM (SELECT nextval('tb_usuario_id_seq') as ID, 'Alura' as NOME, 'admin@alura.com.br' as EMAIL, '$2a$10$VutdNubfMFxiDJQSeIFvaOb27Tw/zQ6uMHj2pqpHN1sL7IAAW72Da' as SENHA, NOW() as CREATED_AT, NOW() as UPDATED_AT) as dados
WHERE NOT EXISTS (SELECT EMAIL FROM tb_usuario WHERE EMAIL = 'admin@alura.com.br' LIMIT 1);

INSERT INTO tb_categoria (ID, TITULO, COR, CREATED_AT, UPDATED_AT)
SELECT * FROM (SELECT nextval('tb_categoria_id_seq') as ID, 'LIVRE' as TITULO, 'BLUE' as COR, NOW() as CREATED_AT, NOW() as UPDATED_AT) as dados
WHERE NOT EXISTS (SELECT TITULO FROM tb_categoria WHERE TITULO = 'LIVRE' LIMIT 1);

INSERT INTO tb_video (ID, DESCRICAO, TITULO, URL, CREATED_AT, UPDATED_AT, CATEGORIA_ID)
SELECT * FROM (SELECT nextval('tb_video_id_seq') as ID, 'Teste Video' as DESCRICAO, 'TESTEVIDEO' as TITULO, 'https://www.youtube.com' as URL, NOW() as CREATED_AT, NOW() as UPDATED_AT, 1 as CATEGORIA_ID) as dados
WHERE NOT EXISTS (SELECT TITULO FROM tb_video WHERE TITULO = 'TESTEVIDEO' LIMIT 1);
COMMIT;