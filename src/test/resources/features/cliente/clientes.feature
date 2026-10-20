# language: pt
Funcionalidade: Gerenciamento de Clientes
  Como administrador
  Quero gerenciar os clientes do sistema
  Para manter um cadastro atualizado

  Cenário: Cadastrar novo cliente com sucesso
    Dado que estou autenticado como administrador
    E tenho os dados válidos de um novo cliente
    Quando cadastro o novo cliente
    Então devo receber status 201
    E o cliente deve ser criado no sistema

  Cenário: Listar todos os clientes
    Dado que estou autenticado como administrador
    E existem 3 clientes no sistema
    Quando solicito listar todos os clientes
    Então devo receber status 200
    E devo receber uma lista com 3 clientes

  Cenário: Buscar cliente por CPF
    Dado que estou autenticado como administrador
    E existe um cliente com CPF "12345678901"
    Quando solicito o cliente pelo CPF "12345678901"
    Então devo receber status 200
    E devo receber os dados corretos do cliente

  Cenário: Atualizar dados do cliente
    Dado que estou autenticado como administrador
    E existe um cliente com CPF "12345678901"
    Quando atualizo o nome do cliente para "João Novo"
    Então devo receber status 200
    E o nome do cliente deve ser atualizado

  Cenário: Deletar cliente com sucesso
    Dado que estou autenticado como administrador
    E existe um cliente no sistema
    Quando deleto o cliente
    Então devo receber status 200
    E o cliente deve ser removido do sistema

  Cenário: Tentar cadastrar cliente com dados inválidos
    Dado que estou autenticado como administrador
    E tenho dados inválidos de cliente
    Quando tento cadastrar o cliente
    Então devo receber status 400

  Cenário: Tentar cadastrar cliente com CPF duplicado
    Dado que estou autenticado como administrador
    E já existe um cliente com CPF "12345678901"
    Quando tento cadastrar outro cliente com o mesmo CPF
    Então devo receber status 409

  Cenário: Tentar acessar cliente sem autenticação
    Dado que não estou autenticado
    Quando solicito listar clientes
    Então devo receber status 401

  Cenário: Listar clientes vazio
    Dado que estou autenticado como administrador
    E não há clientes no sistema
    Quando solicito listar todos os clientes
    Então devo receber status 200
    E devo receber uma lista vazia

  Cenário: Buscar cliente inexistente
    Dado que estou autenticado como administrador
    Quando solicito um cliente que não existe
    Então devo receber status 404
