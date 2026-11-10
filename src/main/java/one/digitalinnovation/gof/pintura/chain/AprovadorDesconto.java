package one.digitalinnovation.gof.pintura.chain;

import java.math.BigDecimal;

/**
 * Handler do padrão <b>Chain of Responsibility</b>.
 *
 * <p>Cada aprovador tem uma alçada (percentual máximo). Se o pedido está dentro dela, ele resolve;
 * senão, repassa para o próximo da cadeia. Se ninguém puder aprovar, o pedido é negado.</p>
 *
 * <p>{@link #aprovar(BigDecimal)} é um Template Method: o fluxo (aprovar, repassar ou negar) é fixo,
 * e as subclasses só definem {@link #alcada()} e {@link #cargo()}.</p>
 */
public abstract class AprovadorDesconto {

	/** Construtor para uso das subclasses; a cadeia nasce sem próximo elo. */
	protected AprovadorDesconto() {
	}

	/** Próximo elo da cadeia; {@code null} no último aprovador. */
	private AprovadorDesconto proximo;

	/**
	 * Encadeia o próximo aprovador e o devolve, permitindo {@code a.proximo(b).proximo(c)}.
	 *
	 * @param proximo aprovador que recebe os pedidos acima da alçada deste
	 * @return o próprio {@code proximo}, para encadeamento fluente
	 */
	public AprovadorDesconto proximo(AprovadorDesconto proximo) {
		this.proximo = proximo;
		return proximo;
	}

	/**
	 * Processa o pedido de desconto percorrendo a cadeia.
	 *
	 * @param percentual percentual solicitado (por exemplo, 12 para 12%)
	 * @return aprovado pelo primeiro aprovador cuja alçada comporta o pedido; negado se ninguém
	 *         puder aprovar ou se o percentual for negativo
	 */
	public final ResultadoAprovacao aprovar(BigDecimal percentual) {
		// Percentual negativo seria um "acréscimo": nunca é válido como desconto.
		if (percentual.signum() < 0) {
			return ResultadoAprovacao.negado(percentual, "Percentual de desconto não pode ser negativo");
		}
		// Dentro da minha alçada: eu resolvo e a cadeia termina aqui.
		if (percentual.compareTo(alcada()) <= 0) {
			return ResultadoAprovacao.aprovado(cargo(), percentual);
		}
		// Acima da minha alçada: repasso para o próximo elo, se houver.
		if (proximo != null) {
			return proximo.aprovar(percentual);
		}
		// Cheguei ao fim da cadeia sem ninguém que pudesse aprovar.
		return ResultadoAprovacao.negado(percentual,
				"Desconto de " + percentual + "% está acima da alçada de todos os aprovadores");
	}

	/**
	 * Percentual máximo que este aprovador pode conceder.
	 *
	 * @return alçada em pontos percentuais (por exemplo, 5 para 5%)
	 */
	protected abstract BigDecimal alcada();

	/**
	 * Nome do cargo, usado na mensagem de aprovação.
	 *
	 * @return cargo do aprovador
	 */
	protected abstract String cargo();
}
