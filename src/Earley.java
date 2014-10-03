import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

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

public class Earley {

	Chart chart = null;
	LinkedHashSet<String> lexico = null;
	public static LinkedHashMap<String, LinkedHashSet<Regra>> gramatica = null;
	ArrayList<ArrayList<Regra>> sentencas = null;
	
	public class Estado{
		Regra regra;
		String elemento;
		int linha;
		int coluna;
		public Estado(Regra regra, String elemento, int linha, int coluna) {
			super();
			this.regra = regra;
			this.elemento = elemento;
			this.linha = linha;
			this.coluna = coluna;
		}
	}
	
	public class Chart{
		int linha;
		int coluna;
		LinkedHashMap<Integer, Estado> estados = new LinkedHashMap<Integer, Estado>();
		public Chart(LinkedHashMap<Integer, Estado> estados, int linha, int coluna) {
			super();
			this.linha = linha;
			this.coluna = coluna;
			this.estados = estados;
		}
	}


	public Earley() {
		ManipulaCorpus.extrairRegrasESentenca();

		gramatica = ManipulaCorpus.gramatica;
		lexico = ManipulaCorpus.lexico;
		sentencas = ManipulaCorpus.sentencas; 

		for (ArrayList<Regra> listaRegras: sentencas) {
			int i = 0;
			for (int j = listaRegras.size()-1; j >= 0; j--) {
				String palavra = listaRegras.get(j).direita.get(0).valor;
				System.out.println(palavra + " "+ i);
				earley(gramatica, palavra);
				i++;
			}
		}
	}
	
	public void earley(LinkedHashMap<String, LinkedHashSet<Regra>> gramatica, String palavra){
		
	}
	
	public void enfileirar(Estado estado, int linha){
		if(chart!=null){
			if(!chart.estados.containsValue(estado)){
				chart.estados.put(new Integer(linha+1), estado);
			}
		}
	}
	
	public void predictor(Estado estado, int linha, int coluna){
		if(gramatica.containsKey(estado.elemento)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(estado.elemento)){
				if(regra.tipo == Regra.LEXICO){
					Estado novoEstado = new Estado(regra, regra.direita.get(0).valor, coluna, coluna);
					enfileirar(novoEstado, coluna);
				}
			}
		}
	}
	
	public void scanner(Estado estado){
		if(lexico.contains(estado.elemento)){
			
		}
	}
	
	public static void main(String[] args) {
		new Earley();
	}
	
}
