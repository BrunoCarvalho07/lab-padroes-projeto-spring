package one.digitalinnovation.gof.pintura.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.CriarOrcamentoRequest;
import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.DescontoRequest;
import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.DescontoResponse;
import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.ItemRequest;
import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.OrcamentoResponse;
import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.StatusRequest;
import one.digitalinnovation.gof.pintura.model.Orcamento;
import one.digitalinnovation.gof.pintura.service.ItemPedido;
import one.digitalinnovation.gof.pintura.service.OrcamentoService;
import one.digitalinnovation.gof.pintura.service.OrcamentoService.ResultadoDesconto;

/**
 * Endpoints REST de orçamento de pintura. A classe só traduz HTTP para chamadas ao serviço; as
 * regras de negócio ficam no domínio e os erros são tratados por {@link TratadorDeErros}.
 */
@RestController
@RequestMapping("orcamentos")
public class OrcamentoController {

	/** Serviço que orquestra os padrões de projeto. */
	private final OrcamentoService service;

	/**
	 * Recebe o serviço por injeção de dependência.
	 *
	 * @param service serviço que orquestra os padrões (injetado pelo Spring)
	 */
	public OrcamentoController(OrcamentoService service) {
		this.service = service;
	}

	/**
	 * Cria um orçamento (Factory Method, Decorator e Builder).
	 *
	 * @param request dados do orçamento (validados automaticamente)
	 * @return HTTP 201 com o orçamento criado e o cabeçalho {@code Location}
	 */
	@PostMapping
	public ResponseEntity<OrcamentoResponse> criar(@Valid @RequestBody CriarOrcamentoRequest request) {
		List<ItemPedido> itens = new ArrayList<>();
		for (ItemRequest item : request.getItens()) {
			itens.add(item.paraPedido());
		}
		Orcamento orcamento = service.criar(
				request.getCliente(), request.getEmail(), request.getTelefone(), request.getPrazoDias(), itens);
		return ResponseEntity
				.created(URI.create("/orcamentos/" + orcamento.getId()))
				.body(OrcamentoResponse.de(orcamento));
	}

	/**
	 * Lista todos os orçamentos.
	 *
	 * @return HTTP 200 com a lista
	 */
	@GetMapping
	public ResponseEntity<List<OrcamentoResponse>> listar() {
		List<OrcamentoResponse> respostas = new ArrayList<>();
		service.listar().forEach(o -> respostas.add(OrcamentoResponse.de(o)));
		return ResponseEntity.ok(respostas);
	}

	/**
	 * Busca um orçamento pelo id.
	 *
	 * @param id identificador do orçamento
	 * @return HTTP 200 com o orçamento (ou 404 se não existir)
	 */
	@GetMapping("{id}")
	public ResponseEntity<OrcamentoResponse> buscar(@PathVariable String id) {
		return ResponseEntity.ok(OrcamentoResponse.de(service.buscar(id)));
	}

	/**
	 * Solicita desconto (Chain of Responsibility). Responde 200 tanto para aprovado quanto para
	 * negado: o campo {@code aprovado} da resposta indica a decisão.
	 *
	 * @param id      identificador do orçamento
	 * @param request percentual desejado
	 * @return HTTP 200 com a decisão e o orçamento atualizado
	 */
	@PostMapping("{id}/desconto")
	public ResponseEntity<DescontoResponse> solicitarDesconto(@PathVariable String id,
															  @Valid @RequestBody DescontoRequest request) {
		ResultadoDesconto r = service.solicitarDesconto(id, request.getPercentual());
		return ResponseEntity.ok(DescontoResponse.de(r.getResultado(), r.getOrcamento()));
	}

	/**
	 * Altera o status do orçamento (Observer).
	 *
	 * @param id      identificador do orçamento
	 * @param request status de destino
	 * @return HTTP 200 com o orçamento atualizado (ou 409 se a transição for inválida)
	 */
	@PostMapping("{id}/status")
	public ResponseEntity<OrcamentoResponse> alterarStatus(@PathVariable String id,
														   @Valid @RequestBody StatusRequest request) {
		return ResponseEntity.ok(OrcamentoResponse.de(service.alterarStatus(id, request.getStatus())));
	}
}
