package one.digitalinnovation.gof.pintura.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.decorator.PinturaBase;
import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/** Pintura externa (fachada): só paredes, tinta resistente a intempéries, 2 demãos. */
@Component
public class PinturaExternaFactory extends ServicoFactory {

	/** Cria a fábrica de pintura externa (sem estado). */
	public PinturaExternaFactory() {
	}

	/** Preço do m² por demão na pintura externa. */
	private static final BigDecimal PRECO_M2_DEMAO = new BigDecimal("24.00");

	/** Quantidade de demãos da pintura externa. */
	private static final int DEMAOS = 2;

	/**
	 * Chave usada pela {@link FabricaDeServicos} para escolher esta fábrica.
	 *
	 * @return {@link TipoPintura#EXTERNA}
	 */
	@Override
	public TipoPintura tipo() {
		return TipoPintura.EXTERNA;
	}

	/**
	 * Factory Method: monta a pintura externa, que cobre apenas as paredes.
	 *
	 * @param ambiente local a ser pintado
	 * @return pintura base com a área das paredes
	 */
	@Override
	protected ServicoPintura criarPintura(Ambiente ambiente) {
		return new PinturaBase(
				"Pintura externa - " + ambiente.getNome() + " (" + DEMAOS + " demãos)",
				ambiente.areaParedes(), PRECO_M2_DEMAO, DEMAOS);
	}
}
