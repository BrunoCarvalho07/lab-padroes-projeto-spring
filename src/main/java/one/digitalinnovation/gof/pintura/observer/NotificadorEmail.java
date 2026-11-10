package one.digitalinnovation.gof.pintura.observer;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.event.StatusOrcamentoAlterado;
import one.digitalinnovation.gof.pintura.model.Orcamento;
import one.digitalinnovation.gof.pintura.model.StatusOrcamento;

/** Simula e-mail ao cliente quando o orçamento é enviado, aprovado ou recusado. */
@Component
public class NotificadorEmail {

	/** Cria o listener (bean do Spring, sem estado). */
	public NotificadorEmail() {
	}

	/** Logger usado para "enviar" o e-mail (a simulação aparece no console). */
	private static final Logger LOG = LoggerFactory.getLogger(NotificadorEmail.class);

	/** Status que geram e-mail para o cliente. */
	private static final Set<StatusOrcamento> STATUS_NOTIFICADOS =
			Set.of(StatusOrcamento.ENVIADO, StatusOrcamento.APROVADO, StatusOrcamento.RECUSADO);

	/**
	 * Reage ao evento de mudança de status publicado pelo {@code OrcamentoService}.
	 *
	 * @param evento dados da mudança (orçamento, status anterior e atual)
	 */
	@EventListener
	public void aoMudarStatus(StatusOrcamentoAlterado evento) {
		Orcamento orcamento = evento.getOrcamento();
		// Sem e-mail cadastrado ou com status irrelevante, este listener fica em silêncio.
		if (orcamento.getEmail() == null || !STATUS_NOTIFICADOS.contains(evento.getAtual())) {
			return;
		}
		LOG.info("[E-MAIL] Para {}: seu orçamento {} agora está {}",
				orcamento.getEmail(), orcamento.getId(), evento.getAtual());
	}
}
