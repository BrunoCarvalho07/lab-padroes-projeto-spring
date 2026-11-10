package one.digitalinnovation.gof.pintura.chain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Monta a cadeia de responsabilidade a partir dos beans {@link AprovadorDesconto}.
 *
 * <p>O Spring injeta a lista já ordenada por {@code @Order}; aqui só ligamos cada elo ao seguinte.
 * Para criar um novo nível de aprovação basta uma nova classe {@code @Component} com a ordem
 * desejada, sem mexer nas demais.</p>
 */
@Component
public class CadeiaAprovacao {

	/** Primeiro elo da cadeia (o aprovador de menor alçada). */
	private final AprovadorDesconto inicio;

	/**
	 * Liga os aprovadores em cadeia.
	 *
	 * @param aprovadores aprovadores já ordenados por {@code @Order} (do menor para o maior nível)
	 * @throws IllegalStateException se a lista estiver vazia
	 */
	public CadeiaAprovacao(List<AprovadorDesconto> aprovadores) {
		if (aprovadores.isEmpty()) {
			throw new IllegalStateException("Nenhum aprovador de desconto configurado");
		}
		// Cada aprovador aponta para o seguinte; o último fica sem próximo (fim da cadeia).
		for (int i = 0; i < aprovadores.size() - 1; i++) {
			aprovadores.get(i).proximo(aprovadores.get(i + 1));
		}
		this.inicio = aprovadores.get(0);
	}

	/**
	 * Envia o pedido ao primeiro elo da cadeia.
	 *
	 * @param percentual percentual de desconto solicitado
	 * @return decisão final da cadeia
	 */
	public ResultadoAprovacao aprovar(BigDecimal percentual) {
		return inicio.aprovar(percentual);
	}
}
