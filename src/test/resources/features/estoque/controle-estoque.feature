# language: pt
Funcionalidade: Controle de Movimentações de Estoque
  Como responsável pelo estoque da oficina
  Quero registrar e controlar as movimentações de peças e insumos
  Para garantir que o estoque esteja sempre atualizado e consistente

  Cenário: Registrar entrada de peças no estoque
    Dado que estou autenticado como gerenciador de estoque
    Quando registro uma entrada de 20 unidades de "vela de ignição"
    Então devo receber status 201
    E a quantidade de "vela de ignição" deve aumentar em 20

  Cenário: Registrar saída de peças com estoque suficiente
    Dado que estou autenticado como gerenciador de estoque
    E existem 30 unidades de "pastilha de freio" em estoque
    Quando registro uma saída de 10 unidades de "pastilha de freio"
    Então devo receber status 201
    E a quantidade de "pastilha de freio" deve diminuir em 10

  Cenário: Calcular saldo após operações de entrada e saída
    Dado que estou autenticado como gerenciador de estoque
    E registrei uma entrada de 50 unidades
    E registrei uma saída de 30 unidades
    Quando consulto o saldo
    Então devo receber saldo de 20 unidades

  Cenário: Impedir saída quando estoque é insuficiente
    Dado que estou autenticado como gerenciador de estoque
    E tenho 3 unidades de uma peça em estoque
    Quando tento registrar uma saída de 8 unidades
    Então devo receber status 400
    E devo receber mensagem de estoque insuficiente

  Cenário: Consultar histórico de movimentações de uma peça específica
    Dado que estou autenticado como gerenciador de estoque
    E registrei 5 movimentações para "fluido de freio"
    Quando solicito o histórico de "fluido de freio"
    Então devo receber todas as 5 movimentações dessa peça

  Cenário: Listar movimentações quando não há registros
    Dado que estou autenticado como gerenciador de estoque
    E não há movimentações registradas
    Quando solicito listar as movimentações
    Então devo receber status 200
    E devo receber uma lista vazia

  Cenário: Rejeitar movimentação com quantidade negativa
    Dado que estou autenticado como gerenciador de estoque
    Quando registro uma movimentação com quantidade negativa
    Então devo receber status 400

  Cenário: Retornar erro ao movimentar peça inexistente no sistema
    Dado que estou autenticado como gerenciador de estoque
    Quando tento movimentar uma peça que não existe
    Então devo receber status 404

  Cenário: Acesso ao estoque exige autenticação
    Dado que não estou autenticado
    Quando solicito listar movimentações de estoque
    Então devo receber status 401
