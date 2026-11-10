package one.digitalinnovation.gof.pintura.factory;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import one.digitalinnovation.gof.pintura.decorator.PinturaBase;
import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/** Textura decorativa: só paredes, 1 aplicação (preço mais alto por m²). */
@Component
public class TexturaFactory extends ServicoFactory {

	/** Cria a fábrica de textura (sem estado). */
	public TexturaFactory() {
	}

	/** Preço do m² por aplicação de textura. */
	private static final BigDecimal PRECO_M2_DEMAO = new BigDecimal("32.00");

	/** Quantidade de aplicações da textura. */
	private static final int DEMAOS = 1;

	/**
	 * Chave usada pela {@link FabricaDeServicos} para escolher esta fábrica.
	 *
	 * @return {@link TipoPintura#TEXTURA}
	 */
	@Override
	public TipoPintura tipo() {
		return TipoPintura.TEXTURA;
	}

	/**
	 * Factory Method: monta a textura, que cobre apenas as paredes.
	 *
	 * @param ambiente local a ser texturizado
	 * @return pintura base com a área das paredes
	 */
	@Override
	protected ServicoPintura criarPintura(Ambiente ambiente) {
		return new PinturaBase(
				"Textura - " + ambiente.getNome(),
				ambiente.areaParedes(), PRECO_M2_DEMAO, DEMAOS);
	}
}
