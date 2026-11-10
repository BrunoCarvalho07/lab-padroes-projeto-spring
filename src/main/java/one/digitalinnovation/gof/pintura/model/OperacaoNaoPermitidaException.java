package one.digitalinnovation.gof.pintura.model;

/**
 * Lançada quando uma operação é válida em si, mas não pode ser feita no estado atual do orçamento
 * (por exemplo, pedir desconto de um orçamento já enviado ou fazer uma transição de status inválida).
 *
 * <p>Na API REST isso vira HTTP 409 (Conflict). Dados mal formados continuam sendo
 * {@link IllegalArgumentException} (HTTP 400).</p>
 */
public class OperacaoNaoPermitidaException extends RuntimeException {

	/** Versão de serialização (exigida por boa prática em exceções, que são serializáveis). */
	private static final long serialVersionUID = 1L;

	/**
	 * Cria a exceção com a explicação do problema.
	 *
	 * @param mensagem explicação de por que a operação não é permitida agora
	 */
	public OperacaoNaoPermitidaException(String mensagem) {
		super(mensagem);
	}
}
