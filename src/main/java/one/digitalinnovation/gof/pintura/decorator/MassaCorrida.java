package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;

/** Aplicação de massa corrida para nivelar a parede: R$ 12,00 por m². */
public class MassaCorrida extends ServicoDecorator {

	/** Preço da massa corrida por metro quadrado. */
	private static final BigDecimal PRECO_M2 = new BigDecimal("12.00");

	/**
	 * Embrulha o serviço com massa corrida.
	 *
	 * @param servico serviço ao qual a massa corrida será adicionada
	 */
	public MassaCorrida(ServicoPintura servico) {
		super(servico);
	}

	/**
	 * Nome exibido no orçamento.
	 *
	 * @return "Massa corrida"
	 */
	@Override
	protected String nomeAdicional() {
		return "Massa corrida";
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
