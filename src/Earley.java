import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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
		int elemento;
		public Estado(Regra regra, int elemento) {
			super();
			this.regra = regra;
			this.elemento = elemento;
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

		for (ArrayList<Regra> sentenca: sentencas) {
				earley(gramatica, sentenca);
		}

	}
	
	public void earley(LinkedHashMap<String, LinkedHashSet<Regra>> gramatica, ArrayList<Regra> sentenca){
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, "S'");
		Estado novoEstado = new Estado(novaRegra, 0);
		gramatica.put("S'", new LinkedHashSet<Regra>());
		gramatica.get("S'").add(novaRegra);
		enfileirar(novoEstado, 0);

		int j = 0;
		for (int k = sentenca.size()-1; k >= 0; k--) {
			String palavra = sentenca.get(k).direita.get(0).valor;

			LinkedHashMap<Integer, Estado> estados = chart.estados;
		    Iterator i = estados.keySet().iterator();
		    while(i.hasNext()){
		    	Integer linha = (Integer)i.next();
		    	estados.get(linha);
		    }
			j++;
		}



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
				Estado novoEstado = new Estado(regra, 0);
				enfileirar(novoEstado, coluna);
			}
		}
	}
	
	public void scanner(Estado estado, int linha, int coluna){
		if(lexico.contains(estado.elemento)){
			Estado novoEstado = new Estado(estado.regra, 0);
			enfileirar(novoEstado, coluna);
		}
	}
	
	public static void main(String[] args) {
		new Earley();
	}
	
}
