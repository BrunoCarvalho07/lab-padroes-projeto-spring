package one.digitalinnovation.gof.pintura.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.event.StatusOrcamentoAlterado;

/** Registra toda transição de status, sem filtro, como histórico. */
@Component
public class RegistroAuditoria {

	/** Cria o listener (bean do Spring, sem estado). */
	public RegistroAuditoria() {
	}

	/** Logger usado para registrar a auditoria. */
	private static final Logger LOG = LoggerFactory.getLogger(RegistroAuditoria.class);

	/**
	 * Reage ao evento de mudança de status publicado pelo {@code OrcamentoService}.
	 *
	 * @param evento dados da mudança (orçamento, status anterior e atual)
	 */
	@EventListener
	public void aoMudarStatus(StatusOrcamentoAlterado evento) {
		LOG.info("[AUDITORIA] Orçamento {}: {} -> {}",
				evento.getOrcamento().getId(), evento.getAnterior(), evento.getAtual());
	}
}
