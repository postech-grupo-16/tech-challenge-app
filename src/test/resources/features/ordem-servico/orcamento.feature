# language: pt
Funcionalidade: Orçamento de Ordem de Serviço
  Como funcionário da oficina
  Quero gerenciar orçamentos das ordens de serviço
  Para apresentar ao cliente o custo estimado do reparo

  Cenário: Tentar criar orçamento para ordem inexistente sem autenticação
    Dado que o sistema está inicializado
    Quando um orçamento é criado para uma ordem inexistente
    Então deve retornar erro de não autenticado

  Cenário: Tentar criar orçamento com identificador de ordem inválido
    Dado que o sistema está inicializado
    Quando um orçamento é criado com ID de ordem inexistente e valores:
      | descricao | Revisão completa do motor |
      | valor     | 1500.00                   |
    Então deve retornar erro de não autenticado

  Cenário: Tentar aprovar orçamento sem autenticação
    Dado que o sistema está inicializado
    Quando o orçamento é aprovado
    Então deve retornar erro de não autenticado

  Cenário: Tentar rejeitar orçamento sem autenticação
    Dado que o sistema está inicializado
    Quando o orçamento é rejeitado com motivo "Valor acima do orçamento previsto pelo cliente"
    Então deve retornar erro de não autenticado

  Cenário: Tentar rejeitar orçamento pendente sem autenticação
    Dado que o sistema está inicializado
    E que existe uma ordem de serviço com orçamento pendente
    Quando o orçamento é rejeitado
    Então deve retornar erro de não autenticado

  Cenário: Tentar aprovar novamente orçamento já aprovado sem autenticação
    Dado que o sistema está inicializado
    E que existe uma ordem de serviço com orçamento aprovado
    Quando tenta-se aprovar novamente o orçamento
    Então deve retornar erro de não autenticado
