package one.digitalinnovation.gof.pintura.chain;

import java.math.BigDecimal;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Diretor: descontos de até 30%. É o último elo da cadeia. */
@Component
@Order(3)
public class Diretor extends AprovadorDesconto {

	/** Cria o aprovador de nível diretor (bean do Spring, ordenado por {@code @Order}). */
	public Diretor() {
	}

	/**
	 * Percentual máximo de desconto que o diretor pode aprovar.
	 *
	 * @return 30
	 */
	@Override
	protected BigDecimal alcada() {
		return new BigDecimal("30");
	}

	/**
	 * Nome do cargo exibido na mensagem de aprovação.
	 *
	 * @return "Diretor"
	 */
	@Override
	protected String cargo() {
		return "Diretor";
	}
}
