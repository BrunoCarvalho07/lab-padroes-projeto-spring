package one.digitalinnovation.gof.pintura.decorator;

import java.util.function.UnaryOperator;

/**
 * Catálogo de adicionais que o cliente pode escolher na requisição. Cada constante sabe "embrulhar"
 * um {@link ServicoPintura} com o decorator correspondente, então a pilha de decorators é montada em
 * tempo de execução conforme o pedido.
 */
public enum Adicional {

	/** Massa corrida (por m²). */
	MASSA_CORRIDA(MassaCorrida::new),

	/** Selador/primer (por m²). */
	PRIMER_SELADOR(PrimerSelador::new),

	/** Proteção de móveis e piso (valor fixo). */
	PROTECAO_MOVEIS(ProtecaoMoveis::new),

	/** Andaime para trabalho em altura (valor fixo). */
	ANDAIME(Andaime::new);

	/** Função que embrulha um serviço com o decorator deste adicional. */
	private final UnaryOperator<ServicoPintura> decorador;

	/**
	 * Associa a constante ao seu decorator.
	 *
	 * @param decorador função que embrulha um serviço com o decorator correspondente
	 */
	Adicional(UnaryOperator<ServicoPintura> decorador) {
		this.decorador = decorador;
	}

	/**
	 * Embrulha o serviço com o decorator deste adicional.
	 *
	 * @param servico serviço a decorar
	 * @return novo serviço com o adicional aplicado
	 */
	public ServicoPintura aplicarEm(ServicoPintura servico) {
		return decorador.apply(servico);
	}
}
