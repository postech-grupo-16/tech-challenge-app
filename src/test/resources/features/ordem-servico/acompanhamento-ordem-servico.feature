# language: pt
Funcionalidade: Acompanhamento do Status de Ordem de Serviço pelo Cliente
  Como cliente da oficina
  Quero consultar minhas ordens de serviço
  Para acompanhar o progresso do atendimento do meu veículo

  Cenário: Visualizar detalhes completos de uma ordem em execução
    Dado que sou um cliente autenticado
    E tenho uma ordem de serviço em execução
    Quando solicito os detalhes dessa ordem
    Então devo receber status 200
    E devo ver os detalhes da ordem
    E devo ver o veículo associado
    E devo ver o valor total da ordem

  Cenário: Listar apenas as próprias ordens de serviço
    Dado que sou um cliente autenticado
    E tenho uma ordem de serviço em execução
    Quando solicito listar minhas ordens de serviço
    Então devo receber status 200
    E devo ver minha ordem na lista

  Cenário: Verificar isolamento entre ordens de clientes diferentes
    Dado que sou um cliente autenticado
    E outro cliente tem uma ordem de serviço
    Quando tento buscar a ordem do outro cliente
    Então devo receber status 404

  Cenário: Verificar o status atual da ordem em andamento
    Dado que sou um cliente autenticado
    E tenho uma ordem com status "EM_EXECUCAO"
    Quando solicito os detalhes dessa ordem
    Então devo receber status 200
    E devo receber o status "EM_EXECUCAO"
    E devo ver a descrição "Em execução"

  Cenário: Retornar erro ao buscar ordem inexistente
    Dado que sou um cliente autenticado
    Quando solicito uma ordem que não existe
    Então devo receber status 404

  Cenário: Exigir autenticação para acessar ordens de serviço
    Dado que não estou autenticado
    Quando solicito acessar minhas ordens de serviço
    Então devo receber status 401
