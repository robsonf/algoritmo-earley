

public class Earley {

	public static void main(String[] args) {
		ManipulaCorpus.extrairRegrasESentenca();
		System.out.println(ManipulaCorpus.REGRAS);
		System.out.println(ManipulaCorpus.SENTENCAS);

		/*
		 * Um estado no algorítimo é composto de três partes: A regra sendo
		 * analisada (X -> uv) A posição do ponto que indica quanto da regra já
		 * foi reconhecida Um índice que indica a partir de qual posição no
		 * texto foi feito o reconhecimento
		 * 
		 * Este estado pode ser representado por (X -> u • v , i) Ao acrescentar
		 * um estado em um conjunto é preciso tomar o cuidado de não duplicá-lo
		 * se já estiver no conjunto. Começamos colocando em S(0) o estado (S'
		 * -> • S, 0)
		 * 
		 * No exame dos estados vamos identificar uma das seguintes três
		 * situações:
		 * 
		 * Completer:
		 * 
		 * Predictor: 
		 * 
		 * Scanner: 
		 * 
		 */

	}
}
