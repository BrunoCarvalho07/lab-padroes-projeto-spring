package one.digitalinnovation.gof.pintura.factory;

/** Tipos de pintura oferecidos; serve de chave para escolher a fábrica certa. */
public enum TipoPintura {

	/** Pintura de ambientes internos: paredes mais teto. */
	INTERNA,

	/** Pintura de fachadas e áreas externas: somente paredes. */
	EXTERNA,

	/** Textura decorativa: somente paredes. */
	TEXTURA
}
