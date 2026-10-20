# language: pt
Funcionalidade: Validações Obrigatórias do Sistema
  Como desenvolvedor responsável pela qualidade
  Quero garantir que o sistema aplique validações corretas nos dados de entrada
  Para proteger a integridade dos dados e a segurança da aplicação

  Cenário: Acesso a endpoint protegido exige autenticação
    Dado que o sistema está inicializado
    Quando um endpoint protegido é acessado sem autenticação
    Então deve retornar erro de não autenticado

  Cenário: Acesso a funcionalidade exclusiva de administrador exige autenticação
    Dado que o sistema está inicializado
    E que um usuário tem perfil "CLIENTE"
    Quando tenta acessar um endpoint apenas para "ADMIN"
    Então deve retornar erro de não autenticado

  Cenário: Cadastro de cliente com CNPJ inválido exige autenticação
    Dado que o sistema está inicializado
    Quando um cliente é cadastrado com CNPJ inválido "123456789"
    Então deve retornar erro de não autenticado

  Cenário: Cadastro de cliente sem email exige autenticação
    Dado que o sistema está inicializado
    Quando um cliente é cadastrado sem email
    Então deve retornar erro de não autenticado

  Cenário: Cadastro de cliente sem telefone exige autenticação
    Dado que o sistema está inicializado
    Quando um cliente é cadastrado sem telefone
    Então deve retornar erro de não autenticado

  Cenário: Cadastro de cliente com nome em branco exige autenticação
    Dado que o sistema está inicializado
    Quando um cliente é cadastrado com nome inválido
    Então deve retornar erro de não autenticado

  Cenário: Cadastro de cliente com email malformado exige autenticação
    Dado que o sistema está inicializado
    Quando um cliente é cadastrado com email inválido
    Então deve retornar erro de não autenticado

  Cenário: Cadastro de cliente com telefone inválido exige autenticação
    Dado que o sistema está inicializado
    Quando um cliente é cadastrado com telefone inválido
    Então deve retornar erro de não autenticado

  Cenário: Movimentação de estoque com quantidade negativa exige autenticação
    Dado que o sistema está inicializado
    Quando uma movimentação de estoque é registrada com quantidade negativa
    Então deve retornar erro de não autenticado

  Cenário: Criação de orçamento com valor negativo exige autenticação
    Dado que o sistema está inicializado
    Quando um orçamento é criado com valor negativo
    Então deve retornar erro de não autenticado

  Cenário: Transição inválida de status de ordem exige autenticação
    Dado que o sistema está inicializado
    E que uma ordem tem status "RECEBIDA"
    Quando a ordem tenta transicionar para um status inválido "CONCLUIDA"
    Então deve retornar erro de não autenticado
