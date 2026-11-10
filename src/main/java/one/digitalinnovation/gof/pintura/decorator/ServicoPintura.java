package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;

/**
 * Componente do padrão <b>Decorator</b>: tudo que pode entrar no orçamento (a pintura em si e cada
 * adicional) é um {@code ServicoPintura}. Como decorators também implementam esta interface, eles
 * podem ser empilhados uns sobre os outros.
 */
public interface ServicoPintura {

	/**
	 * Texto que aparece no orçamento, incluindo os adicionais já aplicados.
	 *
	 * @return descrição do serviço
	 */
	String descricao();

	/**
	 * Área sobre a qual o serviço é executado.
	 *
	 * @return área em m²
	 */
	double area();

	/**
	 * Custo total do serviço.
	 *
	 * @return valor em reais, com 2 casas decimais
	 */
	BigDecimal custo();
}
