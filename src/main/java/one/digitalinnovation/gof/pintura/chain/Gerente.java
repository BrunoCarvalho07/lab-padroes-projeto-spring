package one.digitalinnovation.gof.pintura.chain;

import java.math.BigDecimal;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Gerente: descontos de até 15%. */
@Component
@Order(2)
public class Gerente extends AprovadorDesconto {

	/** Cria o aprovador de nível gerente (bean do Spring, ordenado por {@code @Order}). */
	public Gerente() {
	}

	/**
	 * Percentual máximo de desconto que o gerente pode aprovar.
	 *
	 * @return 15
	 */
	@Override
	protected BigDecimal alcada() {
		return new BigDecimal("15");
	}

	/**
	 * Nome do cargo exibido na mensagem de aprovação.
	 *
	 * @return "Gerente"
	 */
	@Override
	protected String cargo() {
		return "Gerente";
	}
}
