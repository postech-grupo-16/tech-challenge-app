# language: pt
Funcionalidade: Catálogo de Serviços da Oficina
  Como administrador da oficina
  Quero gerenciar o catálogo de serviços disponíveis
  Para manter os tipos de serviço e seus valores atualizados

  Cenário: Tentar cadastrar serviço sem autenticação
    Dado que o sistema está inicializado
    Quando um novo serviço é cadastrado com os seguintes dados:
      | nome        | Troca de óleo         |
      | descricao   | Troca de óleo mineral |
      | valor_hora  | 150.00                |
    Então deve retornar erro de não autenticado

  Cenário: Tentar listar serviços sem autenticação
    Dado que o sistema está inicializado
    Quando a lista de serviços é solicitada
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar serviço sem campo nome sem autenticação
    Dado que o sistema está inicializado
    Quando um serviço é cadastrado sem o campo "nome"
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar serviço com valor por hora negativo sem autenticação
    Dado que o sistema está inicializado
    Quando um serviço é cadastrado com valor_hora negativo
    Então deve retornar erro de não autenticado

  Cenário: Tentar buscar serviço por nome sem autenticação
    Dado que o sistema está inicializado
    Quando o serviço é buscado por nome "Alinhamento e balanceamento"
    Então deve retornar erro de não autenticado
