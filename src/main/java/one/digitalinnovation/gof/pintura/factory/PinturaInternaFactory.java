package one.digitalinnovation.gof.pintura.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.decorator.PinturaBase;
import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/** Pintura interna: paredes mais teto, tinta acrílica, 2 demãos. */
@Component
public class PinturaInternaFactory extends ServicoFactory {

	/** Cria a fábrica de pintura interna (sem estado). */
	public PinturaInternaFactory() {
	}

	/** Preço do m² por demão na pintura interna. */
	private static final BigDecimal PRECO_M2_DEMAO = new BigDecimal("18.00");

	/** Quantidade de demãos da pintura interna. */
	private static final int DEMAOS = 2;

	/**
	 * Chave usada pela {@link FabricaDeServicos} para escolher esta fábrica.
	 *
	 * @return {@link TipoPintura#INTERNA}
	 */
	@Override
	public TipoPintura tipo() {
		return TipoPintura.INTERNA;
	}

	/**
	 * Factory Method: monta a pintura interna, que inclui as paredes e o teto.
	 *
	 * @param ambiente local a ser pintado
	 * @return pintura base com área de paredes mais teto
	 */
	@Override
	protected ServicoPintura criarPintura(Ambiente ambiente) {
		double area = ambiente.areaParedes() + ambiente.areaTeto();
		return new PinturaBase(
				"Pintura interna - " + ambiente.getNome() + " (" + DEMAOS + " demãos)",
				area, PRECO_M2_DEMAO, DEMAOS);
	}
}
