package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;

/** Selador/primer aplicado antes da tinta para melhorar a aderência: R$ 6,00 por m². */
public class PrimerSelador extends ServicoDecorator {

	/** Preço do selador por metro quadrado. */
	private static final BigDecimal PRECO_M2 = new BigDecimal("6.00");

	/**
	 * Embrulha o serviço com selador.
	 *
	 * @param servico serviço ao qual o selador será adicionado
	 */
	public PrimerSelador(ServicoPintura servico) {
		super(servico);
	}

	/**
	 * Nome exibido no orçamento.
	 *
	 * @return "Selador"
	 */
	@Override
	protected String nomeAdicional() {
		return "Selador";
	}

	/**
	 * Valor do adicional: preço por m² vezes a área do serviço.
	 *
	 * @return valor em reais
	 */
	@Override
	protected BigDecimal valorAdicional() {
		return porM2(PRECO_M2);
	}
}
