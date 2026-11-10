package one.digitalinnovation.gof.pintura.factory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/**
 * Ponto único de acesso às fábricas. O Spring injeta todos os beans {@link ServicoFactory}
 * encontrados; para suportar um novo tipo de pintura basta criar uma nova classe anotada com
 * {@code @Component} (princípio Aberto/Fechado na prática: nada existente precisa mudar).
 */
@Component
public class FabricaDeServicos {

	/** Fábricas indexadas pelo tipo de pintura que produzem. */
	private final Map<TipoPintura, ServicoFactory> fabricas = new EnumMap<>(TipoPintura.class);

	/**
	 * Registra todas as fábricas disponíveis.
	 *
	 * @param fabricasDisponiveis todos os beans {@link ServicoFactory} do contexto (injetados pelo Spring)
	 */
	public FabricaDeServicos(List<ServicoFactory> fabricasDisponiveis) {
		// Cada fábrica informa o seu próprio tipo: não há nenhum if/switch por tipo.
		fabricasDisponiveis.forEach(f -> fabricas.put(f.tipo(), f));
	}

	/**
	 * Cria o serviço de pintura usando a fábrica do tipo pedido.
	 *
	 * @param tipo     tipo de pintura desejado
	 * @param ambiente local a ser pintado
	 * @return serviço base (sem adicionais)
	 * @throws IllegalArgumentException se não houver fábrica registrada para o tipo
	 */
	public ServicoPintura criar(TipoPintura tipo, Ambiente ambiente) {
		ServicoFactory fabrica = fabricas.get(tipo);
		if (fabrica == null) {
			throw new IllegalArgumentException("Tipo de pintura não suportado: " + tipo);
		}
		return fabrica.novoServico(ambiente);
	}
}
