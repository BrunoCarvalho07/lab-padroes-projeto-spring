package one.digitalinnovation.gof.pintura.service;

import java.util.Set;

import one.digitalinnovation.gof.pintura.decorator.Adicional;
import one.digitalinnovation.gof.pintura.factory.TipoPintura;
import one.digitalinnovation.gof.pintura.model.Ambiente;

/**
 * Um item pedido pelo cliente: onde pintar, que tipo de pintura e quais adicionais. É o objeto que
 * a camada web entrega à camada de serviço, sem depender de nada de HTTP.
 */
public final class ItemPedido {

	/** Local a ser pintado. */
	private final Ambiente ambiente;

	/** Tipo de pintura (escolhe a fábrica). */
	private final TipoPintura tipo;

	/** Adicionais desejados; pode ser {@code null} ou vazio. */
	private final Set<Adicional> adicionais;

	/**
	 * Cria o item do pedido.
	 *
	 * @param ambiente   local a ser pintado
	 * @param tipo       tipo de pintura
	 * @param adicionais adicionais desejados (pode ser {@code null} ou vazio)
	 */
	public ItemPedido(Ambiente ambiente, TipoPintura tipo, Set<Adicional> adicionais) {
		this.ambiente = ambiente;
		this.tipo = tipo;
		this.adicionais = adicionais;
	}

	/**
	 * Local a ser pintado.
	 *
	 * @return ambiente do item
	 */
	public Ambiente getAmbiente() {
		return ambiente;
	}

	/**
	 * Tipo de pintura do item.
	 *
	 * @return tipo de pintura
	 */
	public TipoPintura getTipo() {
		return tipo;
	}

	/**
	 * Adicionais desejados.
	 *
	 * @return conjunto de adicionais, ou {@code null} se nenhum foi informado
	 */
	public Set<Adicional> getAdicionais() {
		return adicionais;
	}
}
