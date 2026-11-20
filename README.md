Gabriel Quadros Garcia Pimentel, Gustavo Iichiro Nagata, Thiago Christen Pereira Soares

Vídeo: https://youtu.be/Yby1r18Uz9c

# BrincaFácil

O BrincaFácil é uma API para gerenciar o estoque de empresas que alugam brinquedos e kits de decoração para festas. A empresa cadastra cada unidade da empresa, vincula os itens às reservas dos clientes e evita que a mesma unidade seja reservada para eventos com datas sobrepostas.

## Funcionalidades:

- Estoque — cadastro, consulta, edição e exclusão de brinquedos e kits de festa. O catálogo permite buscar por nome, ordenar os resultados e marcar itens como ativos ou inativos.
- Clientes — cadastro e consulta dos dados de contato de quem faz a locação.
- Reservas — registro de um período de locação para um cliente, com opção de adicionar e remover brinquedos e kits. O cancelamento libera os itens para novas reservas no período.
- Usuários e permissões — controle de acesso pelos perfis ADMIN e PREMIUM.
Como o estoque funciona
Cada item cadastrado representa uma unidade de locação. Unidades do mesmo modelo podem ser reservadas separadamente.

O sistema só permite reservar itens ativos e impede que a mesma unidade seja reservada para períodos sobrepostos. A data inicial e a final contam no período. Quando um item já está vinculado a uma reserva, a exclusão é bloqueada e ele pode ser desativado para preservar o histórico.
Como executar
Instale o JDK 21, extraia o ZIP e abra um terminal na pasta BrincaFacil, onde está o arquivo build.gradle.kts.

No PowerShell, defina uma chave aleatória de pelo menos 32 bytes e escolha a senha do administrador inicial. O caminho de banco abaixo cria uma base local separada da que acompanha o ZIP.
$env:JWT_SECRET = 'substitua-por-uma-chave-aleatoria-de-pelo-menos-32-bytes'
$env:APP_ADMIN_PASSWORD = 'substitua-por-uma-senha-forte'
$env:SPRING_DATASOURCE_URL = 'jdbc:h2:file:./data/brincafacil-local;DB_CLOSE_ON_EXIT=FALSE'
.\gradlew.bat bootRun
Depois de iniciar o projeto, abra o Swagger UI. O administrador inicial usa o e-mail admin@authserver.com e a senha definida em APP_ADMIN_PASSWORD.
