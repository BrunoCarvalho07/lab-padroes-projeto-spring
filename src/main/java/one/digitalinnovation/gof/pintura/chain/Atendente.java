package one.digitalinnovation.gof.pintura.chain;

import java.math.BigDecimal;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Atendente: descontos de até 5%. É o primeiro elo da cadeia. */
@Component
@Order(1)
public class Atendente extends AprovadorDesconto {

	/** Cria o aprovador de nível atendente (bean do Spring, ordenado por {@code @Order}). */
	public Atendente() {
	}

	/**
	 * Percentual máximo de desconto que o atendente pode aprovar.
	 *
	 * @return 5
	 */
	@Override
	protected BigDecimal alcada() {
		return new BigDecimal("5");
	}

	/**
	 * Nome do cargo exibido na mensagem de aprovação.
	 *
	 * @return "Atendente"
	 */
	@Override
	protected String cargo() {
		return "Atendente";
	}
}
