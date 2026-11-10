package one.digitalinnovation.gof.pintura.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import one.digitalinnovation.gof.pintura.decorator.Adicional;
import one.digitalinnovation.gof.pintura.decorator.ServicoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/**
 * Testes unitários da fábrica de serviços e do catálogo de adicionais (Factory Method e Decorator).
 * Não sobem o Spring: as fábricas são passadas à mão, como o contêiner faria por injeção.
 */
class FabricaDeServicosTest {

	/** Fábrica com os três tipos de pintura registrados. */
	private final FabricaDeServicos fabrica = new FabricaDeServicos(
			Arrays.<ServicoFactory>asList(new PinturaInternaFactory(), new PinturaExternaFactory(), new TexturaFactory()));

	/** Sala usada como cenário: 4 x 5 m, pé-direito de 2,6 m, uma porta e uma janela. */
	private final Ambiente sala = new Ambiente("Sala", 4, 5, 2.6, 1, 1);

	/** A fábrica escolhida depende apenas do tipo pedido. */
	@Test
	void escolheAFabricaPeloTipo() {
		// interna: (43,7 de paredes + 20 de teto) * 18 * 2 demãos
		assertEquals(0, new BigDecimal("2293.20").compareTo(fabrica.criar(TipoPintura.INTERNA, sala).custo()));
		// externa: 43,7 m² * 24 * 2 demãos
		assertEquals(0, new BigDecimal("2097.60").compareTo(fabrica.criar(TipoPintura.EXTERNA, sala).custo()));
		// textura: 43,7 m² * 32 * 1 aplicação
		assertEquals(0, new BigDecimal("1398.40").compareTo(fabrica.criar(TipoPintura.TEXTURA, sala).custo()));
	}

	/** Pedir um tipo sem fábrica registrada é um erro claro, não um NullPointerException. */
	@Test
	void tipoSemFabricaRegistradaEhRejeitado() {
		FabricaDeServicos incompleta = new FabricaDeServicos(
				Collections.<ServicoFactory>singletonList(new PinturaInternaFactory()));
		assertThrows(IllegalArgumentException.class, () -> incompleta.criar(TipoPintura.TEXTURA, sala));
	}

	/** O enum {@link Adicional} aplica o decorator correspondente e soma o custo. */
	@Test
	void decoratorsSaoAplicadosPeloEnumAdicional() {
		ServicoPintura base = fabrica.criar(TipoPintura.INTERNA, sala);
		ServicoPintura completo = Adicional.PROTECAO_MOVEIS.aplicarEm(Adicional.MASSA_CORRIDA.aplicarEm(base));
		// 2293,20 + 63,7 * 12 (764,40) + 80,00
		assertEquals(0, new BigDecimal("3137.60").compareTo(completo.custo()));
		assertTrue(completo.descricao().endsWith("+ Massa corrida + Proteção de móveis"));
	}
}
