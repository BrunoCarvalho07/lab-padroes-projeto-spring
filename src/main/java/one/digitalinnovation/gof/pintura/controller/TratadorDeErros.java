package one.digitalinnovation.gof.pintura.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import one.digitalinnovation.gof.pintura.dto.OrcamentoDtos.ErroResposta;
import one.digitalinnovation.gof.pintura.model.OperacaoNaoPermitidaException;
import one.digitalinnovation.gof.pintura.service.OrcamentoNaoEncontradoException;

/**
 * Converte exceções de domínio em respostas HTTP padronizadas, para que os controllers fiquem livres
 * de {@code try/catch}. Só atua nos controllers do pacote {@code pintura}.
 */
@RestControllerAdvice(basePackages = "one.digitalinnovation.gof.pintura")
public class TratadorDeErros {

	/** Cria o tratador (bean do Spring, sem estado). */
	public TratadorDeErros() {
	}

	/**
	 * Orçamento inexistente.
	 *
	 * @param e exceção lançada pelo serviço
	 * @return HTTP 404 com a mensagem
	 */
	@ExceptionHandler(OrcamentoNaoEncontradoException.class)
	public ResponseEntity<ErroResposta> naoEncontrado(OrcamentoNaoEncontradoException e) {
		return resposta(HttpStatus.NOT_FOUND, e.getMessage(), null);
	}

	/**
	 * Dados inválidos vindos de regras do domínio (ex.: dimensão zero, serviço sem área).
	 *
	 * @param e exceção lançada pelo domínio
	 * @return HTTP 400 com a mensagem
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErroResposta> requisicaoInvalida(IllegalArgumentException e) {
		return resposta(HttpStatus.BAD_REQUEST, e.getMessage(), null);
	}

	/**
	 * Operação não permitida no estado atual (ex.: transição de status inválida).
	 *
	 * @param e exceção lançada pelo domínio
	 * @return HTTP 409 com a mensagem
	 */
	@ExceptionHandler(OperacaoNaoPermitidaException.class)
	public ResponseEntity<ErroResposta> conflito(OperacaoNaoPermitidaException e) {
		return resposta(HttpStatus.CONFLICT, e.getMessage(), null);
	}

	/**
	 * Falha de Bean Validation no corpo da requisição.
	 *
	 * @param e exceção com os erros de cada campo
	 * @return HTTP 400 com o motivo de cada campo inválido
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException e) {
		Map<String, String> campos = new LinkedHashMap<>();
		// Guardamos só o primeiro erro de cada campo para a resposta ficar enxuta.
		e.getBindingResult().getFieldErrors()
				.forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
		return resposta(HttpStatus.BAD_REQUEST, "Dados da requisição inválidos", campos);
	}

	/**
	 * Corpo ilegível: JSON malformado ou valor de enum inexistente (por exemplo, um tipo de pintura
	 * que não existe).
	 *
	 * @param e exceção lançada na leitura do corpo
	 * @return HTTP 400 com mensagem genérica
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResposta> corpoIlegivel(HttpMessageNotReadableException e) {
		return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição ilegível ou com valor inválido", null);
	}

	/**
	 * Monta a resposta de erro padronizada.
	 *
	 * @param status   status HTTP
	 * @param mensagem explicação do erro
	 * @param campos   erros por campo (pode ser {@code null})
	 * @return resposta HTTP com o corpo de erro
	 */
	private static ResponseEntity<ErroResposta> resposta(HttpStatus status, String mensagem,
														Map<String, String> campos) {
		return ResponseEntity.status(status).body(new ErroResposta(status.value(), mensagem, campos));
	}
}
