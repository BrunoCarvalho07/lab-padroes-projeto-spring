package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;

/** Aluguel e montagem de andaime para trabalho em altura: valor fixo de R$ 350,00. */
public class Andaime extends ServicoDecorator {

	/** Valor fixo cobrado pelo andaime, independente da área. */
	private static final BigDecimal VALOR_FIXO = new BigDecimal("350.00");

	/**
	 * Embrulha o serviço com o andaime.
	 *
	 * @param servico serviço ao qual o andaime será adicionado
	 */
	public Andaime(ServicoPintura servico) {
		super(servico);
	}

	/**
	 * Nome exibido no orçamento.
	 *
	 * @return "Andaime"
	 */
	@Override
	protected String nomeAdicional() {
		return "Andaime";
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
