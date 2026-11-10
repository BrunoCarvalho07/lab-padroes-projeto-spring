package one.digitalinnovation.gof.pintura.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decorator base: embrulha outro {@link ServicoPintura} e soma um adicional ao custo e à descrição.
 * Como o decorator também é um {@code ServicoPintura}, eles podem ser empilhados em qualquer
 * ordem e quantidade.
 *
 * <p>As subclasses só informam o nome e o valor do adicional; a composição de descrição, área e
 * custo fica aqui (Template Method), evitando código repetido.</p>
 */
public abstract class ServicoDecorator implements ServicoPintura {

	/** Serviço embrulhado por este decorator. */
	protected final ServicoPintura servico;

	/**
	 * Cria o decorator embrulhando um serviço.
	 *
	 * @param servico serviço a ser decorado
	 */
	protected ServicoDecorator(ServicoPintura servico) {
		this.servico = servico;
	}

	/**
	 * Nome do adicional, por exemplo "Massa corrida".
	 *
	 * @return nome que será acrescentado à descrição
	 */
	protected abstract String nomeAdicional();

	/**
	 * Quanto o adicional soma ao custo do serviço embrulhado.
	 *
	 * @return valor do adicional, em reais
	 */
	protected abstract BigDecimal valorAdicional();

	/**
	 * Auxiliar para adicionais cobrados por metro quadrado.
	 *
	 * @param precoM2 preço do adicional por m²
	 * @return {@code área do serviço * precoM2}, com 2 casas decimais
	 */
	protected BigDecimal porM2(BigDecimal precoM2) {
		return BigDecimal.valueOf(servico.area()).multiply(precoM2).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Acrescenta o nome do adicional ao final da descrição do serviço embrulhado.
	 *
	 * @return descrição composta, por exemplo "Pintura interna + Massa corrida"
	 */
	@Override
	public String descricao() {
		return servico.descricao() + " + " + nomeAdicional();
	}

	/**
	 * A área não muda: o adicional age sobre a mesma superfície.
	 *
	 * @return área do serviço embrulhado, em m²
	 */
	@Override
	public double area() {
		return servico.area();
	}

	/**
	 * Soma o valor do adicional ao custo do serviço embrulhado.
	 *
	 * @return custo total, em reais
	 */
	@Override
	public BigDecimal custo() {
		return servico.custo().add(valorAdicional());
	}
}
