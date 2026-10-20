# language: pt
Funcionalidade: Controle de Estoque
  Como gerenciador de estoque
  Quero controlar as movimentações de peças e insumos
  Para manter o estoque atualizado e consistente

  Cenário: Registrar entrada de peça no estoque
    Dado que estou autenticado como gerenciador de estoque
    E tenho dados válidos para uma movimentação de entrada
    Quando registro uma entrada de 10 unidades de "filtro de óleo"
    Então devo receber status 201
    E a quantidade de "filtro de óleo" deve aumentar em 10

  Cenário: Registrar saída de peça do estoque
    Dado que estou autenticado como gerenciador de estoque
    E existem 15 unidades de "filtro de óleo" em estoque
    Quando registro uma saída de 5 unidades de "filtro de óleo"
    Então devo receber status 201
    E a quantidade de "filtro de óleo" deve diminuir em 5

  Cenário: Listar movimentações de estoque
    Dado que estou autenticado como gerenciador de estoque
    E existem 3 movimentações registradas
    Quando solicito listar as movimentações
    Então devo receber status 200
    E devo receber uma lista com 3 movimentações

  Cenário: Validar saldo de estoque
    Dado que estou autenticado como gerenciador de estoque
    E registrei uma entrada de 20 unidades
    E registrei uma saída de 8 unidades
    Quando consulto o saldo
    Então devo receber saldo de 12 unidades

  Cenário: Impedir saída maior que estoque disponível
    Dado que estou autenticado como gerenciador de estoque
    E tenho 5 unidades de uma peça em estoque
    Quando tento registrar uma saída de 10 unidades
    Então devo receber status 400
    E devo receber mensagem de estoque insuficiente

  Cenário: Tentar movimentar peça inexistente
    Dado que estou autenticado como gerenciador de estoque
    Quando tento movimentar uma peça que não existe
    Então devo receber status 404

  Cenário: Tentar acessar estoque sem autenticação
    Dado que não estou autenticado
    Quando solicito listar movimentações de estoque
    Então devo receber status 401

  Cenário: Registrar movimentação com dados inválidos
    Dado que estou autenticado como gerenciador de estoque
    Quando registro uma movimentação com quantidade negativa
    Então devo receber status 400

  Cenário: Listar movimentações vazio
    Dado que estou autenticado como gerenciador de estoque
    E não há movimentações registradas
    Quando solicito listar as movimentações
    Então devo receber status 200
    E devo receber uma lista vazia

  Cenário: Consultar histórico de movimentações de uma peça
    Dado que estou autenticado como gerenciador de estoque
    E registrei 3 movimentações para "filtro de óleo"
    Quando solicito o histórico de "filtro de óleo"
    Então devo receber todas as 3 movimentações dessa peça
