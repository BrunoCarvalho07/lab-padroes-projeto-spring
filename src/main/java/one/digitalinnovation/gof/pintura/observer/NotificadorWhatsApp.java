package one.digitalinnovation.gof.pintura.observer;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.event.StatusOrcamentoAlterado;
import one.digitalinnovation.gof.pintura.model.Orcamento;
import one.digitalinnovation.gof.pintura.model.StatusOrcamento;

/** Simula mensagem de WhatsApp quando o serviço é aprovado ou agendado. */
@Component
public class NotificadorWhatsApp {

	/** Cria o listener (bean do Spring, sem estado). */
	public NotificadorWhatsApp() {
	}

	/** Logger usado para "enviar" a mensagem (a simulação aparece no console). */
	private static final Logger LOG = LoggerFactory.getLogger(NotificadorWhatsApp.class);

	/** Status que geram mensagem de WhatsApp. */
	private static final Set<StatusOrcamento> STATUS_NOTIFICADOS =
			Set.of(StatusOrcamento.APROVADO, StatusOrcamento.AGENDADO);

	/**
	 * Reage ao evento de mudança de status publicado pelo {@code OrcamentoService}.
	 *
	 * @param evento dados da mudança (orçamento, status anterior e atual)
	 */
	@EventListener
	public void aoMudarStatus(StatusOrcamentoAlterado evento) {
		Orcamento orcamento = evento.getOrcamento();
		// Sem telefone cadastrado ou com status irrelevante, este listener fica em silêncio.
		if (orcamento.getTelefone() == null || !STATUS_NOTIFICADOS.contains(evento.getAtual())) {
			return;
		}
		LOG.info("[WHATSAPP] Para {}: orçamento {} - {}",
				orcamento.getTelefone(), orcamento.getId(), evento.getAtual());
	}
}
