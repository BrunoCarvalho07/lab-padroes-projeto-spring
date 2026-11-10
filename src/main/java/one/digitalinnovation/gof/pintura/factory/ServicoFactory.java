package one.digitalinnovation.gof.pintura.factory;

import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/**
 * Creator do padrão <b>Factory Method</b>.
 *
 * <p>{@link #novoServico(Ambiente)} é o algoritmo comum (valida a área). O que muda de um tipo de
 * pintura para outro — qual área pintar, preço e número de demãos — fica no método fábrica
 * {@link #criarPintura(Ambiente)}, implementado pelas subclasses.</p>
 */
public abstract class ServicoFactory {

	/** Construtor para uso das subclasses (a fábrica não guarda estado). */
	protected ServicoFactory() {
	}

	/**
	 * Tipo de pintura que esta fábrica produz; é a chave usada pela {@link FabricaDeServicos}.
	 *
	 * @return tipo de pintura da fábrica
	 */
	public abstract TipoPintura tipo();

	/**
	 * Cria o serviço de pintura para o ambiente, garantindo que haja área a pintar.
	 *
	 * @param ambiente local a ser pintado
	 * @return serviço pronto para receber adicionais (decorators)
	 * @throws IllegalArgumentException se o tipo de pintura não tiver área pintável no ambiente
	 */
	public final ServicoPintura novoServico(Ambiente ambiente) {
		ServicoPintura servico = criarPintura(ambiente);
		// Regra comum a todos os tipos: não existe serviço sem área para pintar.
		if (servico.area() <= 0) {
			throw new IllegalArgumentException(
					"Ambiente '" + ambiente.getNome() + "' não possui área pintável para este tipo de serviço");
		}
		return servico;
	}

	/**
	 * Factory Method: cada subclasse decide como montar a pintura do seu tipo.
	 *
	 * @param ambiente local a ser pintado
	 * @return pintura base (sem adicionais)
	 */
	protected abstract ServicoPintura criarPintura(Ambiente ambiente);
}
