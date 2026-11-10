package one.digitalinnovation.gof.pintura.chain;

import java.math.BigDecimal;

/**
 * Resultado de um pedido de desconto percorrendo a cadeia de aprovadores. Classe imutável,
 * criada pelos métodos {@link #aprovado(String, BigDecimal)} e {@link #negado(BigDecimal, String)}.
 */
public final class ResultadoAprovacao {

	/** {@code true} se algum aprovador aceitou o pedido. */
	private final boolean aprovado;

	/** Cargo de quem aprovou, ou {@code null} se o pedido foi negado. */
	private final String aprovadoPor;

	/** Percentual que foi solicitado. */
	private final BigDecimal percentual;

	/** Mensagem explicando o resultado. */
	private final String motivo;

	/**
	 * Construtor privado: use os métodos de fábrica estáticos, que deixam a intenção mais clara.
	 *
	 * @param aprovado    se o pedido foi aprovado
	 * @param aprovadoPor cargo de quem aprovou (ou {@code null})
	 * @param percentual  percentual solicitado
	 * @param motivo      mensagem explicando o resultado
	 */
	private ResultadoAprovacao(boolean aprovado, String aprovadoPor, BigDecimal percentual, String motivo) {
		this.aprovado = aprovado;
		this.aprovadoPor = aprovadoPor;
		this.percentual = percentual;
		this.motivo = motivo;
	}

	/**
	 * Cria um resultado de aprovação.
	 *
	 * @param cargo      cargo de quem aprovou
	 * @param percentual percentual aprovado
	 * @return resultado aprovado
	 */
	public static ResultadoAprovacao aprovado(String cargo, BigDecimal percentual) {
		return new ResultadoAprovacao(true, cargo, percentual, "Aprovado por " + cargo);
	}

	/**
	 * Cria um resultado de negação.
	 *
	 * @param percentual percentual que foi pedido
	 * @param motivo     por que o pedido foi negado
	 * @return resultado negado
	 */
	public static ResultadoAprovacao negado(BigDecimal percentual, String motivo) {
		return new ResultadoAprovacao(false, null, percentual, motivo);
	}

	/**
	 * Informa se o pedido foi aprovado.
	 *
	 * @return {@code true} se aprovado
	 */
	public boolean isAprovado() {
		return aprovado;
	}

	/**
	 * Informa quem aprovou.
	 *
	 * @return cargo do aprovador, ou {@code null} se o pedido foi negado
	 */
	public String getAprovadoPor() {
		return aprovadoPor;
	}

	/**
	 * Informa o percentual solicitado.
	 *
	 * @return percentual do pedido
	 */
	public BigDecimal getPercentual() {
		return percentual;
	}

	/**
	 * Informa a explicação do resultado.
	 *
	 * @return mensagem de aprovação ou de negação
	 */
	public String getMotivo() {
		return motivo;
	}

	/**
	 * Representação textual, útil em logs e mensagens de teste.
	 *
	 * @return texto com o resultado
	 */
	@Override
	public String toString() {
		return "ResultadoAprovacao{aprovado=" + aprovado + ", aprovadoPor=" + aprovadoPor
				+ ", percentual=" + percentual + ", motivo=" + motivo + "}";
	}
}
