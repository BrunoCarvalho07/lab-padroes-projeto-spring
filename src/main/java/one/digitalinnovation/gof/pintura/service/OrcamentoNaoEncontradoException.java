package one.digitalinnovation.gof.pintura.service;

/** Lançada quando o id informado não corresponde a nenhum orçamento (HTTP 404 na API). */
public class OrcamentoNaoEncontradoException extends RuntimeException {

	/** Versão de serialização (exigida por boa prática em exceções, que são serializáveis). */
	private static final long serialVersionUID = 1L;

	/**
	 * Cria a exceção com o id que não foi encontrado.
	 *
	 * @param id identificador que não foi encontrado
	 */
	public OrcamentoNaoEncontradoException(String id) {
		super("Orçamento não encontrado: " + id);
	}
}
