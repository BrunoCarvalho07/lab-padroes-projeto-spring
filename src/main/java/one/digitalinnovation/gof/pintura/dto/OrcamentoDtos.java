package one.digitalinnovation.gof.pintura.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import one.digitalinnovation.gof.pintura.chain.ResultadoAprovacao;
import one.digitalinnovation.gof.pintura.decorator.Adicional;
import one.digitalinnovation.gof.pintura.factory.TipoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;
import one.digitalinnovation.gof.pintura.model.Orcamento;
import one.digitalinnovation.gof.pintura.model.StatusOrcamento;
import one.digitalinnovation.gof.pintura.service.ItemPedido;

/**
 * Objetos de entrada (requisição) e de saída (resposta) da API REST. Existem para que o contrato da
 * API não fique preso às classes de domínio: o domínio pode mudar sem quebrar quem consome a API.
 *
 * <p>As classes de entrada usam Bean Validation ({@code javax.validation}) para recusar dados
 * inválidos antes de chegarem ao domínio.</p>
 */
public final class OrcamentoDtos {

	/** Classe utilitária: só agrupa os DTOs, não deve ser instanciada. */
	private OrcamentoDtos() {
	}

	// ------------------------- entrada -------------------------

	/** Dados de um ambiente enviados pelo cliente da API. */
	public static class AmbienteRequest {

		/** Nome do ambiente. */
		@NotBlank
		private String nome;

		/** Largura em metros. */
		@Positive
		private double largura;

		/** Comprimento em metros. */
		@Positive
		private double comprimento;

		/** Altura do pé-direito em metros. */
		@Positive
		private double altura;

		/** Quantidade de portas. */
		@PositiveOrZero
		private int portas;

		/** Quantidade de janelas. */
		@PositiveOrZero
		private int janelas;

		/** Construtor vazio, exigido pelo Jackson para montar o objeto a partir do JSON. */
		public AmbienteRequest() {
		}

		/**
		 * Converte para o objeto de domínio (que repete as validações).
		 *
		 * @return ambiente de domínio
		 */
		public Ambiente paraDominio() {
			return new Ambiente(nome, largura, comprimento, altura, portas, janelas);
		}

		/**
		 * Nome do ambiente.
		 *
		 * @return nome
		 */
		public String getNome() {
			return nome;
		}

		/**
		 * Define o nome do ambiente.
		 *
		 * @param nome nome do ambiente
		 */
		public void setNome(String nome) {
			this.nome = nome;
		}

		/**
		 * Largura do ambiente.
		 *
		 * @return largura em metros
		 */
		public double getLargura() {
			return largura;
		}

		/**
		 * Define a largura do ambiente.
		 *
		 * @param largura largura em metros
		 */
		public void setLargura(double largura) {
			this.largura = largura;
		}

		/**
		 * Comprimento do ambiente.
		 *
		 * @return comprimento em metros
		 */
		public double getComprimento() {
			return comprimento;
		}

		/**
		 * Define o comprimento do ambiente.
		 *
		 * @param comprimento comprimento em metros
		 */
		public void setComprimento(double comprimento) {
			this.comprimento = comprimento;
		}

		/**
		 * Altura do pé-direito.
		 *
		 * @return altura em metros
		 */
		public double getAltura() {
			return altura;
		}

		/**
		 * Define a altura do pé-direito.
		 *
		 * @param altura altura em metros
		 */
		public void setAltura(double altura) {
			this.altura = altura;
		}

		/**
		 * Quantidade de portas.
		 *
		 * @return número de portas
		 */
		public int getPortas() {
			return portas;
		}

		/**
		 * Define a quantidade de portas.
		 *
		 * @param portas número de portas
		 */
		public void setPortas(int portas) {
			this.portas = portas;
		}

		/**
		 * Quantidade de janelas.
		 *
		 * @return número de janelas
		 */
		public int getJanelas() {
			return janelas;
		}

		/**
		 * Define a quantidade de janelas.
		 *
		 * @param janelas número de janelas
		 */
		public void setJanelas(int janelas) {
			this.janelas = janelas;
		}
	}

	/** Um item do pedido: ambiente, tipo de pintura e adicionais opcionais. */
	public static class ItemRequest {

		/** Ambiente a pintar. */
		@NotNull
		@Valid
		private AmbienteRequest ambiente;

		/** Tipo de pintura. */
		@NotNull
		private TipoPintura tipo;

		/** Adicionais desejados (opcional). */
		private Set<Adicional> adicionais;

		/** Construtor vazio, exigido pelo Jackson para montar o objeto a partir do JSON. */
		public ItemRequest() {
		}

		/**
		 * Converte para o objeto usado pela camada de serviço.
		 *
		 * @return item do pedido
		 */
		public ItemPedido paraPedido() {
			return new ItemPedido(ambiente.paraDominio(), tipo, adicionais);
		}

		/**
		 * Ambiente a pintar.
		 *
		 * @return ambiente
		 */
		public AmbienteRequest getAmbiente() {
			return ambiente;
		}

		/**
		 * Define o ambiente a pintar.
		 *
		 * @param ambiente ambiente
		 */
		public void setAmbiente(AmbienteRequest ambiente) {
			this.ambiente = ambiente;
		}

		/**
		 * Tipo de pintura.
		 *
		 * @return tipo
		 */
		public TipoPintura getTipo() {
			return tipo;
		}

		/**
		 * Define o tipo de pintura.
		 *
		 * @param tipo tipo de pintura
		 */
		public void setTipo(TipoPintura tipo) {
			this.tipo = tipo;
		}

		/**
		 * Adicionais desejados.
		 *
		 * @return conjunto de adicionais (pode ser {@code null})
		 */
		public Set<Adicional> getAdicionais() {
			return adicionais;
		}

		/**
		 * Define os adicionais desejados.
		 *
		 * @param adicionais conjunto de adicionais
		 */
		public void setAdicionais(Set<Adicional> adicionais) {
			this.adicionais = adicionais;
		}
	}

	/** Corpo do {@code POST /orcamentos}. */
	public static class CriarOrcamentoRequest {

		/** Nome do cliente. */
		@NotBlank
		private String cliente;

		/** E-mail do cliente (opcional, mas se vier precisa ser válido). */
		@Email
		private String email;

		/** Telefone do cliente (opcional). */
		private String telefone;

		/** Prazo em dias (opcional; se vier precisa ser positivo). */
		@Positive
		private Integer prazoDias;

		/** Itens do pedido (pelo menos um). */
		@NotEmpty
		@Valid
		private List<ItemRequest> itens;

		/** Construtor vazio, exigido pelo Jackson para montar o objeto a partir do JSON. */
		public CriarOrcamentoRequest() {
		}

		/**
		 * Nome do cliente.
		 *
		 * @return nome
		 */
		public String getCliente() {
			return cliente;
		}

		/**
		 * Define o nome do cliente.
		 *
		 * @param cliente nome do cliente
		 */
		public void setCliente(String cliente) {
			this.cliente = cliente;
		}

		/**
		 * E-mail do cliente.
		 *
		 * @return e-mail (pode ser {@code null})
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * Define o e-mail do cliente.
		 *
		 * @param email e-mail do cliente
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * Telefone do cliente.
		 *
		 * @return telefone (pode ser {@code null})
		 */
		public String getTelefone() {
			return telefone;
		}

		/**
		 * Define o telefone do cliente.
		 *
		 * @param telefone telefone do cliente
		 */
		public void setTelefone(String telefone) {
			this.telefone = telefone;
		}

		/**
		 * Prazo de execução.
		 *
		 * @return prazo em dias (pode ser {@code null})
		 */
		public Integer getPrazoDias() {
			return prazoDias;
		}

		/**
		 * Define o prazo de execução.
		 *
		 * @param prazoDias prazo em dias
		 */
		public void setPrazoDias(Integer prazoDias) {
			this.prazoDias = prazoDias;
		}

		/**
		 * Itens do pedido.
		 *
		 * @return lista de itens
		 */
		public List<ItemRequest> getItens() {
			return itens;
		}

		/**
		 * Define os itens do pedido.
		 *
		 * @param itens lista de itens
		 */
		public void setItens(List<ItemRequest> itens) {
			this.itens = itens;
		}
	}

	/** Corpo do {@code POST /orcamentos/{id}/desconto}. */
	public static class DescontoRequest {

		/** Percentual de desconto desejado (por exemplo, 12 para 12%). */
		@NotNull
		@DecimalMin("0.0")
		private BigDecimal percentual;

		/** Construtor vazio, exigido pelo Jackson para montar o objeto a partir do JSON. */
		public DescontoRequest() {
		}

		/**
		 * Percentual desejado.
		 *
		 * @return percentual de desconto
		 */
		public BigDecimal getPercentual() {
			return percentual;
		}

		/**
		 * Define o percentual desejado.
		 *
		 * @param percentual percentual de desconto
		 */
		public void setPercentual(BigDecimal percentual) {
			this.percentual = percentual;
		}
	}

	/** Corpo do {@code POST /orcamentos/{id}/status}. */
	public static class StatusRequest {

		/** Status de destino. */
		@NotNull
		private StatusOrcamento status;

		/** Construtor vazio, exigido pelo Jackson para montar o objeto a partir do JSON. */
		public StatusRequest() {
		}

		/**
		 * Status de destino.
		 *
		 * @return status desejado
		 */
		public StatusOrcamento getStatus() {
			return status;
		}

		/**
		 * Define o status de destino.
		 *
		 * @param status status desejado
		 */
		public void setStatus(StatusOrcamento status) {
			this.status = status;
		}
	}

	// ------------------------- saída -------------------------

	/** Linha do orçamento na resposta: descrição, área e custo. */
	public static class ItemResponse {

		/** Descrição do serviço, com os adicionais. */
		private final String descricao;

		/** Área em m². */
		private final double area;

		/** Custo em reais. */
		private final BigDecimal custo;

		/**
		 * Cria a linha da resposta.
		 *
		 * @param descricao descrição do serviço
		 * @param area      área em m²
		 * @param custo     custo em reais
		 */
		public ItemResponse(String descricao, double area, BigDecimal custo) {
			this.descricao = descricao;
			this.area = area;
			this.custo = custo;
		}

		/**
		 * Descrição do serviço.
		 *
		 * @return descrição
		 */
		public String getDescricao() {
			return descricao;
		}

		/**
		 * Área do serviço.
		 *
		 * @return área em m²
		 */
		public double getArea() {
			return area;
		}

		/**
		 * Custo do serviço.
		 *
		 * @return custo em reais
		 */
		public BigDecimal getCusto() {
			return custo;
		}
	}

	/** Representação completa de um orçamento na resposta da API. */
	public static class OrcamentoResponse {

		/** Identificador do orçamento. */
		private final String id;

		/** Nome do cliente. */
		private final String cliente;

		/** Status atual. */
		private final StatusOrcamento status;

		/** Prazo de execução em dias. */
		private final int prazoDias;

		/** Itens do orçamento. */
		private final List<ItemResponse> itens;

		/** Subtotal sem desconto. */
		private final BigDecimal subtotal;

		/** Percentual de desconto aprovado. */
		private final BigDecimal descontoPercentual;

		/** Cargo de quem aprovou o desconto (ou {@code null}). */
		private final String descontoAprovadoPor;

		/** Valor do desconto em reais. */
		private final BigDecimal valorDesconto;

		/** Total a pagar. */
		private final BigDecimal total;

		/**
		 * Cria a resposta com todos os campos já calculados.
		 *
		 * @param id                  identificador do orçamento
		 * @param cliente             nome do cliente
		 * @param status              status atual
		 * @param prazoDias           prazo em dias
		 * @param itens               itens do orçamento
		 * @param subtotal            subtotal sem desconto
		 * @param descontoPercentual  percentual de desconto aprovado
		 * @param descontoAprovadoPor cargo de quem aprovou o desconto
		 * @param valorDesconto       valor do desconto em reais
		 * @param total               total a pagar
		 */
		public OrcamentoResponse(String id, String cliente, StatusOrcamento status, int prazoDias,
								 List<ItemResponse> itens, BigDecimal subtotal, BigDecimal descontoPercentual,
								 String descontoAprovadoPor, BigDecimal valorDesconto, BigDecimal total) {
			this.id = id;
			this.cliente = cliente;
			this.status = status;
			this.prazoDias = prazoDias;
			this.itens = itens;
			this.subtotal = subtotal;
			this.descontoPercentual = descontoPercentual;
			this.descontoAprovadoPor = descontoAprovadoPor;
			this.valorDesconto = valorDesconto;
			this.total = total;
		}

		/**
		 * Converte o orçamento de domínio para a resposta.
		 *
		 * @param o orçamento de domínio
		 * @return resposta pronta para virar JSON
		 */
		public static OrcamentoResponse de(Orcamento o) {
			List<ItemResponse> itens = new ArrayList<>();
			o.getServicos().forEach(s -> itens.add(new ItemResponse(s.descricao(), s.area(), s.custo())));
			return new OrcamentoResponse(o.getId(), o.getCliente(), o.getStatus(), o.getPrazoDias(), itens,
					o.getSubtotal(), o.getDescontoPercentual(), o.getDescontoAprovadoPor(),
					o.getValorDesconto(), o.getTotal());
		}

		/**
		 * Identificador do orçamento.
		 *
		 * @return id
		 */
		public String getId() {
			return id;
		}

		/**
		 * Nome do cliente.
		 *
		 * @return cliente
		 */
		public String getCliente() {
			return cliente;
		}

		/**
		 * Status atual.
		 *
		 * @return status
		 */
		public StatusOrcamento getStatus() {
			return status;
		}

		/**
		 * Prazo de execução.
		 *
		 * @return prazo em dias
		 */
		public int getPrazoDias() {
			return prazoDias;
		}

		/**
		 * Itens do orçamento.
		 *
		 * @return lista de itens
		 */
		public List<ItemResponse> getItens() {
			return itens;
		}

		/**
		 * Subtotal sem desconto.
		 *
		 * @return subtotal em reais
		 */
		public BigDecimal getSubtotal() {
			return subtotal;
		}

		/**
		 * Percentual de desconto aprovado.
		 *
		 * @return percentual
		 */
		public BigDecimal getDescontoPercentual() {
			return descontoPercentual;
		}

		/**
		 * Cargo de quem aprovou o desconto.
		 *
		 * @return cargo, ou {@code null} se não houver desconto
		 */
		public String getDescontoAprovadoPor() {
			return descontoAprovadoPor;
		}

		/**
		 * Valor do desconto.
		 *
		 * @return desconto em reais
		 */
		public BigDecimal getValorDesconto() {
			return valorDesconto;
		}

		/**
		 * Total a pagar.
		 *
		 * @return total em reais
		 */
		public BigDecimal getTotal() {
			return total;
		}
	}

	/** Resposta do pedido de desconto: decisão da cadeia e orçamento atualizado. */
	public static class DescontoResponse {

		/** Se o desconto foi aprovado. */
		private final boolean aprovado;

		/** Cargo de quem aprovou (ou {@code null}). */
		private final String aprovadoPor;

		/** Mensagem explicando a decisão. */
		private final String motivo;

		/** Orçamento depois do pedido. */
		private final OrcamentoResponse orcamento;

		/**
		 * Cria a resposta.
		 *
		 * @param aprovado    se o desconto foi aprovado
		 * @param aprovadoPor cargo de quem aprovou
		 * @param motivo      mensagem explicando a decisão
		 * @param orcamento   orçamento depois do pedido
		 */
		public DescontoResponse(boolean aprovado, String aprovadoPor, String motivo, OrcamentoResponse orcamento) {
			this.aprovado = aprovado;
			this.aprovadoPor = aprovadoPor;
			this.motivo = motivo;
			this.orcamento = orcamento;
		}

		/**
		 * Monta a resposta a partir do resultado da cadeia e do orçamento.
		 *
		 * @param r resultado da cadeia de aprovação
		 * @param o orçamento depois do pedido
		 * @return resposta pronta para virar JSON
		 */
		public static DescontoResponse de(ResultadoAprovacao r, Orcamento o) {
			return new DescontoResponse(r.isAprovado(), r.getAprovadoPor(), r.getMotivo(), OrcamentoResponse.de(o));
		}

		/**
		 * Informa se o desconto foi aprovado.
		 *
		 * @return {@code true} se aprovado
		 */
		public boolean isAprovado() {
			return aprovado;
		}

		/**
		 * Cargo de quem aprovou.
		 *
		 * @return cargo, ou {@code null} se negado
		 */
		public String getAprovadoPor() {
			return aprovadoPor;
		}

		/**
		 * Explicação da decisão.
		 *
		 * @return mensagem
		 */
		public String getMotivo() {
			return motivo;
		}

		/**
		 * Orçamento depois do pedido.
		 *
		 * @return orçamento atualizado
		 */
		public OrcamentoResponse getOrcamento() {
			return orcamento;
		}
	}

	/** Corpo padronizado das respostas de erro da API. */
	public static class ErroResposta {

		/** Código de status HTTP (404, 400, 409...). */
		private final int status;

		/** Mensagem explicando o erro. */
		private final String mensagem;

		/** Erros por campo, quando for falha de validação (senão {@code null}). */
		private final Map<String, String> campos;

		/**
		 * Cria o corpo de erro.
		 *
		 * @param status   código HTTP
		 * @param mensagem explicação do erro
		 * @param campos   erros por campo (pode ser {@code null})
		 */
		public ErroResposta(int status, String mensagem, Map<String, String> campos) {
			this.status = status;
			this.mensagem = mensagem;
			this.campos = campos;
		}

		/**
		 * Código HTTP do erro.
		 *
		 * @return status
		 */
		public int getStatus() {
			return status;
		}

		/**
		 * Explicação do erro.
		 *
		 * @return mensagem
		 */
		public String getMensagem() {
			return mensagem;
		}

		/**
		 * Erros por campo.
		 *
		 * @return mapa campo para mensagem, ou {@code null} se não houver
		 */
		public Map<String, String> getCampos() {
			return campos;
		}
	}
}
