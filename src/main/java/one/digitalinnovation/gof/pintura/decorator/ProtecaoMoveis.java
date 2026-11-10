package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;

/** Cobertura e proteção de móveis e piso: valor fixo de R$ 80,00. */
public class ProtecaoMoveis extends ServicoDecorator {

	/** Valor fixo cobrado pela proteção, independente da área. */
	private static final BigDecimal VALOR_FIXO = new BigDecimal("80.00");

	/**
	 * Embrulha o serviço com a proteção de móveis.
	 *
	 * @param servico serviço ao qual a proteção será adicionada
	 */
	public ProtecaoMoveis(ServicoPintura servico) {
		super(servico);
	}

	/**
	 * Nome exibido no orçamento.
	 *
	 * @return "Proteção de móveis"
	 */
	@Override
	protected String nomeAdicional() {
		return "Proteção de móveis";
	}

	/**
	 * Valor do adicional: fixo, não depende da área.
	 *
	 * @return valor em reais
	 */
	@Override
	protected BigDecimal valorAdicional() {
		return VALOR_FIXO;
	}
}
