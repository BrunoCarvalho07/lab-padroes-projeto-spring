package one.digitalinnovation.gof.pintura.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Testes unitários da cadeia de aprovação de descontos. Não sobem o Spring: montamos a cadeia à mão,
 * na mesma ordem que o {@code @Order} produz no contêiner.
 */
class CadeiaAprovacaoTest {

	/** Cadeia Atendente, Gerente e Diretor, como o Spring a monta. */
	private final CadeiaAprovacao cadeia =
			new CadeiaAprovacao(Arrays.asList(new Atendente(), new Gerente(), new Diretor()));

	/** Cada aprovador resolve os pedidos dentro da sua alçada (inclusive nos limites). */
	@Test
	void cadaAprovadorResolveNaSuaAlcada() {
		assertEquals("Atendente", cadeia.aprovar(new BigDecimal("5")).getAprovadoPor());
		assertEquals("Gerente", cadeia.aprovar(new BigDecimal("5.01")).getAprovadoPor());
		assertEquals("Gerente", cadeia.aprovar(new BigDecimal("15")).getAprovadoPor());
		assertEquals("Diretor", cadeia.aprovar(new BigDecimal("30")).getAprovadoPor());
	}

	/** Pedido acima da alçada de todos é negado, com mensagem explicativa. */
	@Test
	void acimaDaAlcadaDeTodosEhNegado() {
		ResultadoAprovacao resultado = cadeia.aprovar(new BigDecimal("30.01"));
		assertFalse(resultado.isAprovado());
		assertTrue(resultado.getMotivo().contains("acima da alçada"));
	}

	/** Percentual negativo nunca é aprovado. */
	@Test
	void percentualNegativoEhNegado() {
		assertFalse(cadeia.aprovar(new BigDecimal("-1")).isAprovado());
	}

	/** Sem nenhum aprovador configurado, a cadeia não pode ser criada. */
	@Test
	void semAprovadoresNaoMontaCadeia() {
		List<AprovadorDesconto> vazia = Collections.emptyList();
		assertThrows(IllegalStateException.class, () -> new CadeiaAprovacao(vazia));
	}
}
