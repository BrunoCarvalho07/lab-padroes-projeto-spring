package one.digitalinnovation.gof.pintura.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import one.digitalinnovation.gof.pintura.event.StatusOrcamentoAlterado;
import one.digitalinnovation.gof.pintura.model.StatusOrcamento;

/**
 * Teste de integração da API de orçamento: sobe o contexto completo do Spring e chama os endpoints
 * com o {@link MockMvc}, exercitando os cinco padrões juntos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@RecordApplicationEvents
class OrcamentoApiTest {

	/** JSON de um orçamento válido: sala de 4 x 5 m com três adicionais (subtotal esperado: 3.519,80). */
	private static final String NOVO_ORCAMENTO = "{"
			+ "\"cliente\": \"Maria Souza\","
			+ "\"email\": \"maria@email.com\","
			+ "\"telefone\": \"(11) 91234-5678\","
			+ "\"prazoDias\": 10,"
			+ "\"itens\": [{"
			+ "  \"ambiente\": {\"nome\": \"Sala\", \"largura\": 4, \"comprimento\": 5, \"altura\": 2.6,"
			+ "               \"portas\": 1, \"janelas\": 1},"
			+ "  \"tipo\": \"INTERNA\","
			+ "  \"adicionais\": [\"MASSA_CORRIDA\", \"PRIMER_SELADOR\", \"PROTECAO_MOVEIS\"]"
			+ "}]"
			+ "}";

	/** Cliente HTTP simulado do Spring MVC. */
	@Autowired
	private MockMvc mockMvc;

	/** Eventos publicados durante o teste (gravados por {@code @RecordApplicationEvents}). */
	@Autowired
	private ApplicationEvents eventos;

	/**
	 * Cria um orçamento válido e devolve o id.
	 *
	 * @return identificador do orçamento criado
	 * @throws Exception se a chamada falhar
	 */
	private String criarOrcamento() throws Exception {
		String corpo = mockMvc.perform(post("/orcamentos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(NOVO_ORCAMENTO))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(corpo, "$.id");
	}

	/**
	 * Criar um orçamento usa Factory Method (tipo), Decorator (adicionais) e Builder (validação).
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void criaOrcamentoComFactoryDecoratorEBuilder() throws Exception {
		mockMvc.perform(post("/orcamentos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(NOVO_ORCAMENTO))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("RASCUNHO"))
				.andExpect(jsonPath("$.prazoDias").value(10))
				.andExpect(jsonPath("$.itens[0].area").value(63.7))
				.andExpect(jsonPath("$.subtotal").value(3519.80));
	}

	/**
	 * O pedido de desconto passa pela cadeia: 12% é aprovado pelo gerente; 40% é negado.
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void descontoPassaPelaCadeiaDeAprovacao() throws Exception {
		String id = criarOrcamento();

		mockMvc.perform(post("/orcamentos/{id}/desconto", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"percentual\": 12}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.aprovado").value(true))
				.andExpect(jsonPath("$.aprovadoPor").value("Gerente"))
				.andExpect(jsonPath("$.orcamento.total").value(3097.42));

		mockMvc.perform(post("/orcamentos/{id}/desconto", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"percentual\": 40}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.aprovado").value(false));
	}

	/**
	 * Mudar o status publica um evento que os observers recebem.
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void mudancaDeStatusPublicaEventoParaOsObservers() throws Exception {
		String id = criarOrcamento();

		mockMvc.perform(post("/orcamentos/{id}/status", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"status\": \"ENVIADO\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("ENVIADO"));

		long publicados = eventos.stream(StatusOrcamentoAlterado.class)
				.filter(e -> e.getOrcamento().getId().equals(id) && e.getAtual() == StatusOrcamento.ENVIADO)
				.count();
		assertEquals(1, publicados);
	}

	/**
	 * Transição de status inválida resulta em HTTP 409.
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void transicaoInvalidaRetorna409() throws Exception {
		String id = criarOrcamento();

		mockMvc.perform(post("/orcamentos/{id}/status", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"status\": \"AGENDADO\"}"))
				.andExpect(status().isConflict());
	}

	/**
	 * Orçamento inexistente resulta em HTTP 404.
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void orcamentoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/orcamentos/ORC-NAOEXISTE"))
				.andExpect(status().isNotFound());
	}

	/**
	 * Dados inválidos resultam em HTTP 400 com o motivo de cada campo.
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void dadosInvalidosRetornam400() throws Exception {
		mockMvc.perform(post("/orcamentos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"cliente\": \"\", \"itens\": []}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.campos.cliente").exists())
				.andExpect(jsonPath("$.campos.itens").exists());
	}

	/**
	 * Tipo de pintura inexistente resulta em HTTP 400 (e não em erro 500).
	 *
	 * @throws Exception se a chamada falhar
	 */
	@Test
	void tipoDePinturaInexistenteRetorna400() throws Exception {
		String corpo = NOVO_ORCAMENTO.replace("INTERNA", "SUBMARINA");
		mockMvc.perform(post("/orcamentos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(corpo))
				.andExpect(status().isBadRequest());
	}
}
