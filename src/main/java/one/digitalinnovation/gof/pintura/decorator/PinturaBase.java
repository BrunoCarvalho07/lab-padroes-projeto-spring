package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Componente concreto do <b>Decorator</b>: a pintura "pura", sem adicionais. Os decorators
 * embrulham uma instância desta classe (ou de outro decorator).
 */
public class PinturaBase implements ServicoPintura {

	/** Texto exibido no orçamento. */
	private final String descricao;

	/** Área a pintar, em m². */
	private final double area;

	/** Preço do m² para cada demão de tinta. */
	private final BigDecimal precoM2PorDemao;

	/** Número de demãos aplicadas. */
	private final int demaos;

	/**
	 * Cria a pintura base.
	 *
	 * @param descricao       texto exibido no orçamento
	 * @param area            área a pintar, em m²
	 * @param precoM2PorDemao preço do m² para cada demão de tinta
	 * @param demaos          número de demãos aplicadas
	 */
	public PinturaBase(String descricao, double area, BigDecimal precoM2PorDemao, int demaos) {
		this.descricao = descricao;
		this.area = area;
		this.precoM2PorDemao = precoM2PorDemao;
		this.demaos = demaos;
	}

	/**
	 * Descrição da pintura, sem adicionais.
	 *
	 * @return texto exibido no orçamento
	 */
	@Override
	public String descricao() {
		return descricao;
	}

	/**
	 * Área da pintura.
	 *
	 * @return área em m²
	 */
	@Override
	public double area() {
		return area;
	}

	/**
	 * Custo da pintura, calculado como {@code área * preço por m² * demãos}.
	 *
	 * @return valor em reais, com 2 casas decimais
	 */
	@Override
	public BigDecimal custo() {
		// BigDecimal é obrigatório para dinheiro: double acumularia erros de arredondamento.
		return BigDecimal.valueOf(area)
				.multiply(precoM2PorDemao)
				.multiply(BigDecimal.valueOf(demaos))
				.setScale(2, RoundingMode.HALF_UP);
	}
}
