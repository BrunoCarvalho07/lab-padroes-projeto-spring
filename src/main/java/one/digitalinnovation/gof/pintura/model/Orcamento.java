package one.digitalinnovation.gof.pintura.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import one.digitalinnovation.gof.pintura.chain.CadeiaAprovacao;
import one.digitalinnovation.gof.pintura.chain.ResultadoAprovacao;
import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;

/**
 * Orçamento de serviços de pintura.
 *
 * <ul>
 *   <li><b>Builder</b>: construído via {@link #builder()}, que valida os dados obrigatórios.</li>
 *   <li><b>Observer</b>: quem muda o status publica um evento e o Spring entrega aos listeners.</li>
 *   <li><b>Chain of Responsibility</b>: o desconto só é aplicado se a cadeia de aprovadores aceitar.</li>
 * </ul>
 *
 * <p>Os métodos que leem ou alteram o estado mutável (status e desconto) são {@code synchronized},
 * então a mesma instância pode ser usada por várias requisições simultâneas com segurança.</p>
 */
public class Orcamento {

	/** Identificador único, no formato {@code ORC-XXXXXXXX}. */
	private final String id;

	/** Nome do cliente. */
	private final String cliente;

	/** E-mail do cliente (opcional). */
	private final String email;

	/** Telefone do cliente (opcional). */
	private final String telefone;

	/** Prazo de execução, em dias. */
	private final int prazoDias;

	/** Serviços incluídos no orçamento. */
	private final List<ServicoPintura> servicos;

	/** Percentual de desconto aprovado (zero enquanto não houver desconto). */
	private BigDecimal descontoPercentual = BigDecimal.ZERO;

	/** Cargo de quem aprovou o desconto (ou {@code null}). */
	private String descontoAprovadoPor;

	/** Status atual do orçamento; todo orçamento nasce como rascunho. */
	private StatusOrcamento status = StatusOrcamento.RASCUNHO;

	/**
	 * Construtor privado: a única forma de criar um orçamento é pelo {@link Builder}.
	 *
	 * @param b builder já validado, com os dados do orçamento
	 */
	private Orcamento(Builder b) {
		this.id = "ORC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.cliente = b.cliente;
		this.email = b.email;
		this.telefone = b.telefone;
		this.prazoDias = b.prazoDias;
		// Copiamos a lista para que mudanças posteriores no Builder não afetem este orçamento.
		this.servicos = new ArrayList<>(b.servicos);
	}

	/**
	 * Inicia a construção de um orçamento.
	 *
	 * @return novo builder vazio
	 */
	public static Builder builder() {
		return new Builder();
	}

	// ---------- valores ----------

	/**
	 * Soma do custo de todos os serviços, sem desconto.
	 *
	 * @return subtotal em reais, com 2 casas decimais
	 */
	public BigDecimal getSubtotal() {
		return servicos.stream()
				.map(ServicoPintura::custo)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Valor em reais do desconto aprovado.
	 *
	 * @return desconto em reais (zero se não houver)
	 */
	public synchronized BigDecimal getValorDesconto() {
		return getSubtotal().multiply(descontoPercentual)
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}

	/**
	 * Valor final a pagar.
	 *
	 * @return subtotal menos o desconto aprovado
	 */
	public synchronized BigDecimal getTotal() {
		return getSubtotal().subtract(getValorDesconto());
	}

	// ---------- desconto (Chain of Responsibility) ----------

	/**
	 * Pede um desconto à cadeia de aprovadores. Só é aplicado se alguém da cadeia aprovar; se for
	 * negado, o orçamento permanece como estava.
	 *
	 * @param percentual percentual desejado (por exemplo, 12 para 12%)
	 * @param cadeia     cadeia de aprovação montada pelo Spring
	 * @return resultado com a decisão e quem aprovou
	 * @throws OperacaoNaoPermitidaException se o orçamento não for mais um rascunho
	 */
	public synchronized ResultadoAprovacao solicitarDesconto(BigDecimal percentual, CadeiaAprovacao cadeia) {
		// Depois de enviado ao cliente, o preço não pode mais mudar.
		if (status != StatusOrcamento.RASCUNHO) {
			throw new OperacaoNaoPermitidaException(
					"Desconto só pode ser aplicado enquanto o orçamento é rascunho");
		}
		ResultadoAprovacao resultado = cadeia.aprovar(percentual);
		// Só gravamos o desconto se a cadeia aprovou; negado não altera nada.
		if (resultado.isAprovado()) {
			this.descontoPercentual = percentual;
			this.descontoAprovadoPor = resultado.getAprovadoPor();
		}
		return resultado;
	}

	// ---------- status ----------

	/**
	 * Muda o status e devolve o status anterior (usado pelo serviço para montar o evento).
	 *
	 * @param novo status de destino
	 * @return status que o orçamento tinha antes da mudança
	 * @throws OperacaoNaoPermitidaException se a transição não for permitida a partir do status atual
	 */
	public synchronized StatusOrcamento alterarStatus(StatusOrcamento novo) {
		if (!status.podeIrPara(novo)) {
			throw new OperacaoNaoPermitidaException("Transição inválida: " + status + " -> " + novo);
		}
		StatusOrcamento anterior = this.status;
		this.status = novo;
		// Quem chamou publica o evento; o orçamento não conhece nenhum observador.
		return anterior;
	}

	// ---------- acesso ----------

	/**
	 * Identificador único do orçamento.
	 *
	 * @return id no formato {@code ORC-XXXXXXXX}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Nome do cliente.
	 *
	 * @return nome do cliente
	 */
	public String getCliente() {
		return cliente;
	}

	/**
	 * E-mail do cliente.
	 *
	 * @return e-mail, ou {@code null} se não informado
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Telefone do cliente.
	 *
	 * @return telefone, ou {@code null} se não informado
	 */
	public String getTelefone() {
		return telefone;
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
	 * Status atual.
	 *
	 * @return status do orçamento
	 */
	public synchronized StatusOrcamento getStatus() {
		return status;
	}

	/**
	 * Percentual de desconto aprovado.
	 *
	 * @return percentual (zero se não houver desconto)
	 */
	public synchronized BigDecimal getDescontoPercentual() {
		return descontoPercentual;
	}

	/**
	 * Cargo de quem aprovou o desconto.
	 *
	 * @return cargo, ou {@code null} se não houver desconto
	 */
	public synchronized String getDescontoAprovadoPor() {
		return descontoAprovadoPor;
	}

	/**
	 * Serviços do orçamento.
	 *
	 * @return lista somente leitura
	 */
	public List<ServicoPintura> getServicos() {
		return Collections.unmodifiableList(servicos);
	}

	// ---------- Builder ----------

	/** Builder do orçamento: encadeie os métodos e finalize com {@link #build()}. */
	public static class Builder {

		/** Nome do cliente (obrigatório). */
		private String cliente;

		/** E-mail do cliente (opcional). */
		private String email;

		/** Telefone do cliente (opcional). */
		private String telefone;

		/** Prazo de execução em dias (padrão: 7). */
		private int prazoDias = 7;

		/** Serviços acumulados até agora. */
		private final List<ServicoPintura> servicos = new ArrayList<>();

		/** Construtor privado: use {@link Orcamento#builder()}. */
		private Builder() {
		}

		/**
		 * Define o nome do cliente.
		 *
		 * @param cliente nome do cliente (obrigatório)
		 * @return este builder, para encadeamento
		 */
		public Builder cliente(String cliente) {
			this.cliente = cliente;
			return this;
		}

		/**
		 * Define o e-mail do cliente; sem ele o e-mail não é enviado.
		 *
		 * @param email e-mail do cliente (opcional)
		 * @return este builder, para encadeamento
		 */
		public Builder email(String email) {
			this.email = email;
			return this;
		}

		/**
		 * Define o telefone do cliente; sem ele o WhatsApp não é enviado.
		 *
		 * @param telefone telefone do cliente (opcional)
		 * @return este builder, para encadeamento
		 */
		public Builder telefone(String telefone) {
			this.telefone = telefone;
			return this;
		}

		/**
		 * Define o prazo de execução.
		 *
		 * @param prazoDias prazo em dias (padrão: 7)
		 * @return este builder, para encadeamento
		 */
		public Builder prazoDias(int prazoDias) {
			this.prazoDias = prazoDias;
			return this;
		}

		/**
		 * Adiciona um serviço ao orçamento (é necessário pelo menos um).
		 *
		 * @param servico serviço a adicionar
		 * @return este builder, para encadeamento
		 */
		public Builder adicionarServico(ServicoPintura servico) {
			this.servicos.add(servico);
			return this;
		}

		/**
		 * Valida os dados e cria o orçamento, sempre no status {@link StatusOrcamento#RASCUNHO}.
		 *
		 * @return orçamento pronto
		 * @throws IllegalArgumentException se faltar cliente ou serviço, ou se o prazo for inválido
		 */
		public Orcamento build() {
			// Todas as regras de "orçamento válido" ficam concentradas aqui.
			if (cliente == null || cliente.isBlank()) {
				throw new IllegalArgumentException("O cliente é obrigatório");
			}
			if (servicos.isEmpty()) {
				throw new IllegalArgumentException("O orçamento precisa de pelo menos um serviço");
			}
			if (prazoDias <= 0) {
				throw new IllegalArgumentException("O prazo deve ser de pelo menos 1 dia");
			}
			return new Orcamento(this);
		}
	}
}
