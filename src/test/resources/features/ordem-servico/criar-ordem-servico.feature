# language: pt
Funcionalidade: Criação e Gestão de Ordem de Serviço
  Como funcionário da oficina
  Quero criar e gerenciar ordens de serviço
  Para controlar o atendimento dos veículos dos clientes

  Cenário: Tentar criar ordem de serviço sem autenticação
    Dado que o sistema está inicializado
    Quando uma nova ordem de serviço é criada para o cliente "12345678901" e veículo "ABC1234"
    Então deve retornar erro de não autenticado

  Cenário: Tentar criar ordem sem informações obrigatórias sem autenticação
    Dado que o sistema está inicializado
    Quando uma nova ordem de serviço é criada sem as informações necessárias
    Então deve retornar erro de não autenticado

  Cenário: Tentar criar ordem com dados detalhados sem autenticação
    Dado que o sistema está inicializado
    Quando uma nova ordem de serviço é criada com os seguintes dados:
      | cliente   | 12345678901          |
      | veiculo   | ABC1234              |
      | descricao | Motor fazendo barulho |
      | urgente   | false                |
    Então deve retornar erro de não autenticado

  Cenário: Tentar listar ordens de serviço sem autenticação
    Dado que o sistema está inicializado
    Quando a lista de ordens de serviço é solicitada
    Então deve retornar erro de não autenticado

  Cenário: Tentar buscar ordem de serviço inexistente sem autenticação
    Dado que o sistema está inicializado
    Quando uma ordem de serviço é buscada com ID inexistente
    Então deve retornar erro de não autenticado

  Cenário: Tentar remover ordem de serviço sem autenticação de administrador
    Dado que o sistema está inicializado
    Quando a ordem é deletada por usuário ADMIN
    Então deve retornar erro de não autenticado

  Cenário: Tentar remover ordem sem perfil adequado
    Dado que o sistema está inicializado
    Quando tenta-se deletar a ordem com usuário não-ADMIN
    Então deve retornar erro de não autenticado

  Cenário: Tentar buscar ordens do cliente sem autenticação
    Dado que o sistema está inicializado
    Quando o cliente busca suas ordens de serviço
    Então deve retornar erro de não autenticado
