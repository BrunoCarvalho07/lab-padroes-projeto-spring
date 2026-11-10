package one.digitalinnovation.gof.pintura.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import one.digitalinnovation.gof.pintura.model.Orcamento;

/**
 * Implementação em memória, suficiente para o desafio (os dados somem ao reiniciar a aplicação).
 * Para usar um banco de verdade, basta trocar por outra implementação de {@link OrcamentoRepository};
 * o restante do código não muda. Como todo bean do Spring, esta classe é um Singleton do contexto.
 */
@Repository
public class OrcamentoRepositoryEmMemoria implements OrcamentoRepository {

	/** Cria o repositório vazio; o Spring o instancia uma única vez (Singleton). */
	public OrcamentoRepositoryEmMemoria() {
	}

	/** Armazenamento por id; LinkedHashMap preserva a ordem de inserção. */
	private final Map<String, Orcamento> armazenamento = new LinkedHashMap<>();

	/**
	 * Grava (ou substitui) o orçamento. Os métodos são {@code synchronized} porque várias requisições
	 * podem acessar o mesmo bean ao mesmo tempo.
	 *
	 * @param orcamento orçamento a gravar
	 * @return o próprio orçamento gravado
	 */
	@Override
	public synchronized Orcamento salvar(Orcamento orcamento) {
		armazenamento.put(orcamento.getId(), orcamento);
		return orcamento;
	}

	/**
	 * Busca um orçamento pelo identificador.
	 *
	 * @param id identificador do orçamento
	 * @return o orçamento, ou vazio se não existir
	 */
	@Override
	public synchronized Optional<Orcamento> buscarPorId(String id) {
		return Optional.ofNullable(armazenamento.get(id));
	}

	/**
	 * Lista todos os orçamentos (cópia da lista, para que quem chama não altere o armazenamento).
	 *
	 * @return orçamentos na ordem de inserção
	 */
	@Override
	public synchronized List<Orcamento> listar() {
		return new ArrayList<>(armazenamento.values());
	}
}
