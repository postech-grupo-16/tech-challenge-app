# language: pt
Funcionalidade: Acompanhamento de Ordem de Serviço
  Como cliente
  Quero acompanhar minhas ordens de serviço
  Para saber o status e detalhes do meu veículo

  Cenário: Listar ordens de serviço do cliente com sucesso
    Dado que sou um cliente autenticado
    E tenho uma ordem de serviço em execução
    Quando solicito listar minhas ordens de serviço
    Então devo receber status 200
    E devo ver minha ordem na lista

  Cenário: Buscar detalhe de uma ordem de serviço com sucesso
    Dado que sou um cliente autenticado
    E tenho uma ordem de serviço com ID 1
    Quando solicito os detalhes da ordem 1
    Então devo receber status 200
    E devo ver os detalhes da ordem
    E devo ver o veículo associado
    E devo ver o valor total da ordem

  Cenário: Listar ordens vazias quando não há ordens
    Dado que sou um cliente autenticado
    E não tenho nenhuma ordem de serviço
    Quando solicito listar minhas ordens de serviço
    Então devo receber status 200
    E devo receber uma lista vazia

  Cenário: Tentar buscar ordem de outro cliente
    Dado que sou um cliente autenticado
    E outro cliente tem uma ordem de serviço
    Quando tento buscar a ordem do outro cliente
    Então devo receber status 404

  Cenário: Tentar acessar ordens sem autenticação
    Dado que não estou autenticado
    Quando solicito acessar minhas ordens de serviço
    Então devo receber status 401

  Cenário: Buscar ordem inexistente
    Dado que sou um cliente autenticado
    Quando solicito uma ordem que não existe
    Então devo receber status 404

  Cenário: Validar diferentes status de ordem
    Dado que sou um cliente autenticado
    E tenho uma ordem com status "EM_EXECUCAO"
    Quando solicito os detalhes dessa ordem
    Então devo receber o status "EM_EXECUCAO"
    E devo ver a descrição "Em execução"
