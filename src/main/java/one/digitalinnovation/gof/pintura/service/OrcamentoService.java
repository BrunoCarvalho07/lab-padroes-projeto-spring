package one.digitalinnovation.gof.pintura.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.pintura.chain.CadeiaAprovacao;
import one.digitalinnovation.gof.pintura.chain.ResultadoAprovacao;
import one.digitalinnovation.gof.pintura.decorator.Adicional;
import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.event.StatusOrcamentoAlterado;
import one.digitalinnovation.gof.pintura.factory.FabricaDeServicos;
import one.digitalinnovation.gof.pintura.model.OperacaoNaoPermitidaException;
import one.digitalinnovation.gof.pintura.model.Orcamento;
import one.digitalinnovation.gof.pintura.model.StatusOrcamento;
import one.digitalinnovation.gof.pintura.repository.OrcamentoRepository;

/**
 * Orquestra os padrões do sistema: Factory Method (cria o serviço), Decorator (aplica adicionais),
 * Builder (monta o orçamento), Chain of Responsibility (aprova desconto) e Observer (publica evento a
 * cada mudança de status). As regras de negócio em si ficam nas classes de domínio; aqui só se
 * coordena o fluxo.
 */
@Service
public class OrcamentoService {

	/** Resultado de um pedido de desconto: a decisão da cadeia e o orçamento atualizado. */
	public static final class ResultadoDesconto {

		/** Decisão tomada pela cadeia de aprovação. */
		private final ResultadoAprovacao resultado;

		/** Orçamento depois do pedido (com ou sem desconto, conforme a decisão). */
		private final Orcamento orcamento;

		/**
		 * Cria o resultado.
		 *
		 * @param resultado decisão da cadeia
		 * @param orcamento orçamento depois do pedido
		 */
		public ResultadoDesconto(ResultadoAprovacao resultado, Orcamento orcamento) {
			this.resultado = resultado;
			this.orcamento = orcamento;
		}

		/**
		 * Decisão da cadeia.
		 *
		 * @return resultado da aprovação
		 */
		public ResultadoAprovacao getResultado() {
			return resultado;
		}

		/**
		 * Orçamento depois do pedido.
		 *
		 * @return orçamento atualizado
		 */
		public Orcamento getOrcamento() {
			return orcamento;
		}
	}

	/** Fábrica que escolhe a pintura certa pelo tipo (Factory Method). */
	private final FabricaDeServicos fabrica;

	/** Cadeia de aprovação de descontos (Chain of Responsibility). */
	private final CadeiaAprovacao cadeia;

	/** Persistência dos orçamentos (Repository). */
	private final OrcamentoRepository repositorio;

	/** Publicador de eventos do Spring (Observer). */
	private final ApplicationEventPublisher eventos;

	/**
	 * Recebe as dependências por construtor (injeção recomendada: facilita testes e deixa os campos
	 * imutáveis).
	 *
	 * @param fabrica     fábrica de serviços de pintura
	 * @param cadeia      cadeia de aprovação de descontos
	 * @param repositorio repositório de orçamentos
	 * @param eventos     publicador de eventos do Spring
	 */
	public OrcamentoService(FabricaDeServicos fabrica,
							CadeiaAprovacao cadeia,
							OrcamentoRepository repositorio,
							ApplicationEventPublisher eventos) {
		this.fabrica = fabrica;
		this.cadeia = cadeia;
		this.repositorio = repositorio;
		this.eventos = eventos;
	}

	/**
	 * Cria e salva um orçamento: para cada item escolhe a fábrica (Factory Method), aplica os
	 * adicionais (Decorator) e monta o resultado (Builder).
	 *
	 * @param cliente   nome do cliente
	 * @param email     e-mail do cliente (opcional)
	 * @param telefone  telefone do cliente (opcional)
	 * @param prazoDias prazo em dias; {@code null} usa o padrão do Builder
	 * @param itens     itens pedidos (pelo menos um)
	 * @return orçamento criado, no status rascunho
	 * @throws IllegalArgumentException se os dados forem inválidos (cliente vazio, sem itens, etc.)
	 */
	public Orcamento criar(String cliente, String email, String telefone, Integer prazoDias,
						   List<ItemPedido> itens) {
		Orcamento.Builder builder = Orcamento.builder()
				.cliente(cliente)
				.email(email)
				.telefone(telefone);
		if (prazoDias != null) {
			builder.prazoDias(prazoDias);
		}

		for (ItemPedido item : itens) {
			// Factory Method: a fábrica do tipo pedido cria a pintura base.
			ServicoPintura servico = fabrica.criar(item.getTipo(), item.getAmbiente());
			// TreeSet ordena pelo enum, então a descrição sai sempre na mesma ordem.
			Set<Adicional> adicionais = item.getAdicionais() == null
					? Collections.<Adicional>emptySet()
					: new TreeSet<>(item.getAdicionais());
			// Decorator: cada adicional embrulha o serviço anterior.
			for (Adicional adicional : adicionais) {
				servico = adicional.aplicarEm(servico);
			}
			builder.adicionarServico(servico);
		}
		// Builder: valida os dados obrigatórios ao construir.
		return repositorio.salvar(builder.build());
	}

	/**
	 * Busca um orçamento pelo id.
	 *
	 * @param id identificador do orçamento
	 * @return orçamento encontrado
	 * @throws OrcamentoNaoEncontradoException se o id não existir
	 */
	public Orcamento buscar(String id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new OrcamentoNaoEncontradoException(id));
	}

	/**
	 * Lista todos os orçamentos.
	 *
	 * @return orçamentos na ordem de criação
	 */
	public List<Orcamento> listar() {
		return repositorio.listar();
	}

	/**
	 * Pede desconto à cadeia de aprovação (Chain of Responsibility).
	 *
	 * @param id         identificador do orçamento
	 * @param percentual percentual de desconto desejado
	 * @return decisão da cadeia e orçamento atualizado
	 * @throws OrcamentoNaoEncontradoException se o id não existir
	 * @throws OperacaoNaoPermitidaException   se o orçamento não for mais rascunho
	 */
	public ResultadoDesconto solicitarDesconto(String id, BigDecimal percentual) {
		Orcamento orcamento = buscar(id);
		ResultadoAprovacao resultado = orcamento.solicitarDesconto(percentual, cadeia);
		repositorio.salvar(orcamento);
		return new ResultadoDesconto(resultado, orcamento);
	}

	/**
	 * Muda o status e publica o evento {@link StatusOrcamentoAlterado} para os observers (Observer).
	 *
	 * @param id         identificador do orçamento
	 * @param novoStatus status de destino
	 * @return orçamento atualizado
	 * @throws OrcamentoNaoEncontradoException se o id não existir
	 * @throws OperacaoNaoPermitidaException   se a transição for inválida
	 */
	public Orcamento alterarStatus(String id, StatusOrcamento novoStatus) {
		Orcamento orcamento = buscar(id);
		StatusOrcamento anterior = orcamento.alterarStatus(novoStatus);
		repositorio.salvar(orcamento);
		// Observer: publicamos o evento e o Spring entrega a todos os listeners interessados.
		eventos.publishEvent(new StatusOrcamentoAlterado(orcamento, anterior, novoStatus));
		return orcamento;
	}
}
