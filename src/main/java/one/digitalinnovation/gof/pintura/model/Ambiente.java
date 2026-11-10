package one.digitalinnovation.gof.pintura.model;

import java.util.Objects;

/**
 * Um cômodo (ou fachada) a ser pintado. Todas as dimensões são em metros.
 *
 * <p>A classe é imutável: depois de criada, nenhum valor muda. Além disso, ela valida os dados no
 * construtor, então um {@code Ambiente} inválido nunca chega a existir no sistema.</p>
 */
public final class Ambiente {

	/** Área média de uma porta, em m², descontada da área das paredes. */
	private static final double AREA_PORTA = 1.6;

	/** Área média de uma janela, em m², descontada da área das paredes. */
	private static final double AREA_JANELA = 1.5;

	/** Nome do ambiente, por exemplo "Sala". */
	private final String nome;

	/** Largura do ambiente, em metros. */
	private final double largura;

	/** Comprimento do ambiente, em metros. */
	private final double comprimento;

	/** Altura do pé-direito, em metros. */
	private final double altura;

	/** Quantidade de portas (não são pintadas junto com a parede). */
	private final int portas;

	/** Quantidade de janelas (também são descontadas da área das paredes). */
	private final int janelas;

	/**
	 * Cria um ambiente validando todos os dados informados.
	 *
	 * @param nome        nome do ambiente (obrigatório)
	 * @param largura     largura em metros (maior que zero)
	 * @param comprimento comprimento em metros (maior que zero)
	 * @param altura      altura do pé-direito em metros (maior que zero)
	 * @param portas      quantidade de portas (zero ou mais)
	 * @param janelas     quantidade de janelas (zero ou mais)
	 * @throws IllegalArgumentException se o nome estiver vazio, alguma dimensão não for positiva
	 *                                  ou a quantidade de portas/janelas for negativa
	 */
	public Ambiente(String nome, double largura, double comprimento, double altura, int portas, int janelas) {
		// Validamos tudo logo na criação ("fail fast"): é melhor falhar aqui do que calcular um orçamento errado.
		if (nome == null || nome.isBlank()) {
			throw new IllegalArgumentException("O nome do ambiente é obrigatório");
		}
		if (largura <= 0 || comprimento <= 0 || altura <= 0) {
			throw new IllegalArgumentException("As dimensões do ambiente devem ser maiores que zero");
		}
		if (portas < 0 || janelas < 0) {
			throw new IllegalArgumentException("Quantidade de portas e janelas não pode ser negativa");
		}
		this.nome = nome;
		this.largura = largura;
		this.comprimento = comprimento;
		this.altura = altura;
		this.portas = portas;
		this.janelas = janelas;
	}

	/**
	 * Calcula a área das quatro paredes, já descontando portas e janelas.
	 * Fórmula: {@code 2 * (largura + comprimento) * altura - vãos}.
	 *
	 * @return área em m², arredondada para 2 casas decimais e nunca negativa
	 */
	public double areaParedes() {
		// Perímetro vezes a altura dá a área bruta das quatro paredes.
		double bruta = 2 * (largura + comprimento) * altura;
		// Portas e janelas não recebem tinta de parede, então saem da conta.
		double vaos = portas * AREA_PORTA + janelas * AREA_JANELA;
		// Math.max evita área negativa quando os vãos são maiores que as paredes.
		return arredondar(Math.max(0, bruta - vaos));
	}

	/**
	 * Calcula a área do teto ({@code largura * comprimento}).
	 *
	 * @return área em m², arredondada para 2 casas decimais
	 */
	public double areaTeto() {
		return arredondar(largura * comprimento);
	}

	/**
	 * Arredonda para 2 casas decimais, evitando ruído de ponto flutuante nos cálculos.
	 *
	 * @param valor valor a arredondar
	 * @return valor arredondado
	 */
	private static double arredondar(double valor) {
		return Math.round(valor * 100.0) / 100.0;
	}

	/**
	 * Informa o nome do ambiente.
	 *
	 * @return nome do ambiente
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Informa a largura do ambiente.
	 *
	 * @return largura em metros
	 */
	public double getLargura() {
		return largura;
	}

	/**
	 * Informa o comprimento do ambiente.
	 *
	 * @return comprimento em metros
	 */
	public double getComprimento() {
		return comprimento;
	}

	/**
	 * Informa a altura do pé-direito.
	 *
	 * @return altura em metros
	 */
	public double getAltura() {
		return altura;
	}

	/**
	 * Informa a quantidade de portas.
	 *
	 * @return quantidade de portas
	 */
	public int getPortas() {
		return portas;
	}

	/**
	 * Informa a quantidade de janelas.
	 *
	 * @return quantidade de janelas
	 */
	public int getJanelas() {
		return janelas;
	}

	/**
	 * Compara dois ambientes pelo conteúdo (todos os campos).
	 *
	 * @param outro objeto a comparar
	 * @return {@code true} se for um ambiente com exatamente os mesmos valores
	 */
	@Override
	public boolean equals(Object outro) {
		if (this == outro) {
			return true;
		}
		if (!(outro instanceof Ambiente)) {
			return false;
		}
		Ambiente a = (Ambiente) outro;
		return Double.compare(largura, a.largura) == 0
				&& Double.compare(comprimento, a.comprimento) == 0
				&& Double.compare(altura, a.altura) == 0
				&& portas == a.portas
				&& janelas == a.janelas
				&& nome.equals(a.nome);
	}

	/**
	 * Calcula o hash consistente com {@link #equals(Object)}.
	 *
	 * @return código hash do ambiente
	 */
	@Override
	public int hashCode() {
		return Objects.hash(nome, largura, comprimento, altura, portas, janelas);
	}

	/**
	 * Representação textual, útil em logs e mensagens de erro.
	 *
	 * @return texto com os dados do ambiente
	 */
	@Override
	public String toString() {
		return "Ambiente{" + nome + ", " + largura + "x" + comprimento + "x" + altura
				+ ", portas=" + portas + ", janelas=" + janelas + "}";
	}
}
