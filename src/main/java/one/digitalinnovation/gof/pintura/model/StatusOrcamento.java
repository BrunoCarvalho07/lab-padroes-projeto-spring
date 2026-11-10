package one.digitalinnovation.gof.pintura.model;

import java.util.Set;

/**
 * Ciclo de vida de um orçamento. Cada status conhece para quais outros pode ir:
 *
 * <pre>
 * RASCUNHO, depois ENVIADO; de ENVIADO para APROVADO ou RECUSADO; de APROVADO para AGENDADO.
 * RECUSADO e AGENDADO são status finais.
 * </pre>
 */
public enum StatusOrcamento {

	/** Orçamento ainda em elaboração; é o único status em que ainda se pode pedir desconto. */
	RASCUNHO,

	/** Orçamento enviado ao cliente, aguardando resposta. */
	ENVIADO,

	/** Cliente aprovou o orçamento. */
	APROVADO,

	/** Cliente recusou o orçamento (status final). */
	RECUSADO,

	/** Serviço agendado para execução (status final). */
	AGENDADO;

	/**
	 * Informa se a transição deste status para o {@code destino} é permitida.
	 *
	 * @param destino status desejado
	 * @return {@code true} se a transição é válida
	 */
	public boolean podeIrPara(StatusOrcamento destino) {
		return proximosPermitidos().contains(destino);
	}

	/**
	 * Conjunto de status alcançáveis a partir deste.
	 *
	 * @return status de destino permitidos (vazio para os status finais)
	 */
	private Set<StatusOrcamento> proximosPermitidos() {
		switch (this) {
		case RASCUNHO:
			return Set.of(ENVIADO);
		case ENVIADO:
			return Set.of(APROVADO, RECUSADO);
		case APROVADO:
			return Set.of(AGENDADO);
		default:
			// RECUSADO e AGENDADO são finais: não há para onde ir.
			return Set.of();
		}
	}
}
