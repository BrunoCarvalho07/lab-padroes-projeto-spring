# Explorando Padrões de Projetos na Prática com Java

Repositório com as implementações dos padrões de projeto explorados no Lab "Explorando Padrões de Projetos na Prática com Java". Especificamente, este projeto explorou alguns padrões usando o Spring Framework, são eles:
- Singleton
- Strategy/Repository
- Facade


## Desafio de Projeto: Orçamento de Pintura

Para o desafio **"Design Patterns com Java: Dos Clássicos (GoF) ao Spring Framework"** (DIO), este repositório
ganhou um projeto novo e original, no pacote `one.digitalinnovation.gof.pintura`: uma **API REST de orçamento de
serviços de pintura**. O cliente informa os cômodos, escolhe o tipo de pintura e os adicionais, o orçamento é
calculado, passa por uma aprovação de desconto e muda de status (enviado, aprovado, agendado), notificando o
cliente a cada passo. Os exemplos do lab (Cliente, CEP, Feign, JPA) continuam intactos.

O projeto usa outros cinco padrões, aproveitando o que o Spring oferece para conectá-los:

| Padrão | Problema que resolve | Como o Spring ajuda |
|---|---|---|
| **Factory Method** | Cada tipo de pintura calcula área, preço e demãos de um jeito. | Cada fábrica é `@Component`; `FabricaDeServicos` recebe todas por injeção e escolhe pela chave `TipoPintura`. Um novo tipo é só uma nova classe, sem `if/switch`. |
| **Decorator** | Adicionais combinam de qualquer jeito. | O enum `Adicional` sabe criar o decorator certo, então a pilha é montada conforme o JSON da requisição. |
| **Builder** | O orçamento tem campos opcionais e regras de validação. | `Orcamento.builder()`, usado pelo `OrcamentoService`. |
| **Chain of Responsibility** | Descontos têm alçadas (5%, 15%, 30%). | Cada aprovador é `@Component @Order(n)`; `CadeiaAprovacao` recebe a lista já ordenada e liga os elos. |
| **Observer** | Mudar o status deve disparar e-mail, WhatsApp e auditoria. | O serviço publica o evento `StatusOrcamentoAlterado` (`ApplicationEventPublisher`) e os listeners `@EventListener` reagem, sem o orçamento conhecê-los. |

Também aparecem: **Repository** (`OrcamentoRepository`, com implementação em memória), **Template Method**
(`ServicoFactory.novoServico` e `AprovadorDesconto.aprovar`) e **Singleton** (todo bean do Spring é singleton).

A versão em Java puro (sem Spring) do mesmo projeto está no fork
[lab-padroes-projeto-java](https://github.com/BrunoCarvalho07/lab-padroes-projeto-java).

### Como executar

Requisitos: JDK 11 (versão definida no `pom.xml` do lab) e Maven (ou o `mvnw` já incluído).

```bash
./mvnw test              # testes unitários e de API (no Windows: mvnw.cmd test)
./mvnw spring-boot:run   # sobe em http://localhost:8080
```

Documentação interativa (Swagger): <http://localhost:8080/swagger-ui.html>

### Exemplo de uso da API

Criar um orçamento:

```bash
curl -X POST http://localhost:8080/orcamentos \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Maria Souza",
    "email": "maria@email.com",
    "telefone": "(11) 91234-5678",
    "prazoDias": 10,
    "itens": [
      {
        "ambiente": {"nome": "Sala", "largura": 4, "comprimento": 5, "altura": 2.6, "portas": 1, "janelas": 1},
        "tipo": "INTERNA",
        "adicionais": ["MASSA_CORRIDA", "PRIMER_SELADOR", "PROTECAO_MOVEIS"]
      }
    ]
  }'
```

Pedir desconto (passa pela cadeia de aprovação) e mudar o status (dispara os observers, que aparecem nos logs
do console):

```bash
curl -X POST http://localhost:8080/orcamentos/{id}/desconto -H "Content-Type: application/json" -d '{"percentual": 12}'
curl -X POST http://localhost:8080/orcamentos/{id}/status   -H "Content-Type: application/json" -d '{"status": "ENVIADO"}'
```

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/orcamentos` | Cria um orçamento (Factory Method, Decorator e Builder) |
| `GET` | `/orcamentos` | Lista os orçamentos |
| `GET` | `/orcamentos/{id}` | Busca um orçamento |
| `POST` | `/orcamentos/{id}/desconto` | Solicita desconto (Chain of Responsibility) |
| `POST` | `/orcamentos/{id}/status` | Altera o status (Observer) |

Erros seguem um corpo padronizado (`status`, `mensagem` e, na validação, `campos`): `400` dados inválidos,
`404` orçamento inexistente, `409` operação não permitida no estado atual.

### Regras de negócio

- **Área de parede** = `2 x (largura + comprimento) x altura - (portas x 1,6 m² + janelas x 1,5 m²)`
- **Pintura interna** pinta paredes mais teto (R$ 18/m² por demão, 2 demãos). **Externa** pinta só paredes
  (R$ 24/m², 2 demãos). **Textura** (R$ 32/m², 1 aplicação).
- **Adicionais:** massa corrida R$ 12/m², selador R$ 6/m², proteção de móveis R$ 80 fixo, andaime R$ 350 fixo.
- **Desconto:** até 5% atendente, até 15% gerente, até 30% diretor; acima disso é negado. Só vale enquanto o
  orçamento é rascunho.
- **Status:** `RASCUNHO`, depois `ENVIADO`, depois `APROVADO` ou `RECUSADO`; de `APROVADO` para `AGENDADO`.

### Estrutura do projeto novo

```
src/main/java/one/digitalinnovation/gof/pintura/
  controller/  OrcamentoController, TratadorDeErros
  service/     OrcamentoService, ItemPedido, OrcamentoNaoEncontradoException
  dto/         OrcamentoDtos (requisições e respostas)
  model/       Ambiente, Orcamento (com Builder), StatusOrcamento, OperacaoNaoPermitidaException
  decorator/   ServicoPintura, PinturaBase, ServicoDecorator, Adicional + os 4 decorators
  factory/     TipoPintura, ServicoFactory, FabricaDeServicos + 3 fábricas
  chain/       AprovadorDesconto, CadeiaAprovacao, ResultadoAprovacao + Atendente, Gerente, Diretor
  event/       StatusOrcamentoAlterado
  observer/    NotificadorEmail, NotificadorWhatsApp, RegistroAuditoria
  repository/  OrcamentoRepository, OrcamentoRepositoryEmMemoria
src/test/java/one/digitalinnovation/gof/pintura/   testes unitários e de integração (MockMvc)
```

### Ideias para evoluir

- Trocar `OrcamentoRepositoryEmMemoria` por Spring Data JPA com H2 (o restante do código não muda).
- Adicionar o padrão **State** para o ciclo de vida do orçamento e **Strategy** para formas de pagamento.
