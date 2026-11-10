package one.digitalinnovation.gof.pintura.repository;

import java.util.List;
import java.util.Optional;

import one.digitalinnovation.gof.pintura.model.Orcamento;

/**
 * Abstração de persistência de orçamentos (padrão <b>Repository</b>). O restante do sistema depende
 * apenas desta interface, então a forma de armazenar pode mudar sem afetar as regras de negócio.
 */
public interface OrcamentoRepository {

	/**
	 * Grava (ou substitui) o orçamento.
	 *
	 * @param orcamento orçamento a gravar
	 * @return o próprio orçamento gravado
	 */
	Orcamento salvar(Orcamento orcamento);

	/**
	 * Busca um orçamento pelo identificador.
	 *
	 * @param id identificador do orçamento
	 * @return o orçamento, ou vazio se não existir
	 */
	Optional<Orcamento> buscarPorId(String id);

	/**
	 * Lista todos os orçamentos.
	 *
	 * @return orçamentos na ordem em que foram salvos pela primeira vez
	 */
	List<Orcamento> listar();
}
