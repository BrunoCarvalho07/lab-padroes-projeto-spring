package one.digitalinnovation.gof.pintura.event;

import one.digitalinnovation.gof.pintura.model.Orcamento;
import one.digitalinnovation.gof.pintura.model.StatusOrcamento;

/**
 * Evento do padrão <b>Observer</b> no Spring: publicado a cada mudança de status. O publicador
 * ({@code ApplicationEventPublisher}) não conhece os listeners que vão reagir a ele.
 */
public final class StatusOrcamentoAlterado {

	/** Orçamento que mudou de status. */
	private final Orcamento orcamento;

	/** Status antes da mudança. */
	private final StatusOrcamento anterior;

	/** Status depois da mudança. */
	private final StatusOrcamento atual;

	/**
	 * Cria o evento.
	 *
	 * @param orcamento orçamento que mudou de status
	 * @param anterior  status antes da mudança
	 * @param atual     status depois da mudança
	 */
	public StatusOrcamentoAlterado(Orcamento orcamento, StatusOrcamento anterior, StatusOrcamento atual) {
		this.orcamento = orcamento;
		this.anterior = anterior;
		this.atual = atual;
	}

	/**
	 * Orçamento que mudou.
	 *
	 * @return orçamento do evento
	 */
	public Orcamento getOrcamento() {
		return orcamento;
	}

	/**
	 * Status antes da mudança.
	 *
	 * @return status anterior
	 */
	public StatusOrcamento getAnterior() {
		return anterior;
	}

	/**
	 * Status depois da mudança.
	 *
	 * @return status atual
	 */
	public StatusOrcamento getAtual() {
		return atual;
	}
}
