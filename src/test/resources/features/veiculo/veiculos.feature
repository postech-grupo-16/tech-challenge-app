# language: pt
Funcionalidade: Gerenciamento de Veículos
  Como funcionário da oficina
  Quero cadastrar e gerenciar os veículos dos clientes
  Para manter um registro atualizado e facilitar a abertura de ordens de serviço

  Cenário: Tentar cadastrar veículo sem autenticação
    Dado que o sistema está inicializado
    Quando um novo veículo é cadastrado com os seguintes dados:
      | placa  | ABC1234 |
      | marca  | Toyota  |
      | modelo | Corolla |
      | ano    | 2020    |
    Então deve retornar erro de não autenticado

  Cenário: Tentar listar veículos sem autenticação
    Dado que o sistema está inicializado
    Quando a lista de veículos é solicitada
    Então deve retornar erro de não autenticado

  Cenário: Tentar buscar veículo por placa sem autenticação
    Dado que o sistema está inicializado
    Quando o veículo é buscado por placa "ABC1234"
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar veículo com placa duplicada sem autenticação
    Dado que o sistema está inicializado
    Quando um veículo é cadastrado com a mesma placa "ABC1234"
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar veículo com placa inválida sem autenticação
    Dado que o sistema está inicializado
    Quando um veículo é cadastrado com placa inválida "INVALIDA999"
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar veículo sem placa sem autenticação
    Dado que o sistema está inicializado
    Quando um veículo é cadastrado sem placa
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar veículo sem marca ou modelo sem autenticação
    Dado que o sistema está inicializado
    Quando um veículo é cadastrado sem marca ou modelo
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar veículo com ano futuro sem autenticação
    Dado que o sistema está inicializado
    Quando um veículo é cadastrado com ano futuro
    Então deve retornar erro de não autenticado

  Cenário: Tentar cadastrar veículo para cliente inexistente sem autenticação
    Dado que o sistema está inicializado
    Quando um veículo é cadastrado para um cliente inexistente
    Então deve retornar erro de não autenticado

  Cenário: Tentar atualizar dados do veículo sem autenticação
    Dado que o sistema está inicializado
    E que um veículo com placa "VEI0001" foi cadastrado
    Quando os dados do veículo são atualizados:
      | modelo | Corolla Sport 2021 |
    Então deve retornar erro de não autenticado

  Cenário: Tentar remover veículo sem autenticação
    Dado que o sistema está inicializado
    E que um veículo com placa "VEI0002" foi cadastrado
    Quando o veículo é deletado
    Então deve retornar erro de não autenticado
