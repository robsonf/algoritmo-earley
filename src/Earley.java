import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	public Earley() {
		ManipulaCorpus.extrairRegrasESentenca();

		gramatica = ManipulaCorpus.gramatica;
		lexico = ManipulaCorpus.lexico;
		sentencas = ManipulaCorpus.sentencas; 
		chart = new Chart();

//		for (ArrayList<Regra> sentenca: sentencas) {
//			parser(gramatica, sentenca);
//		}

		parser(gramatica, sentencas.get(0));

	}
	
	public void parser(LinkedHashMap<String, LinkedHashSet<Regra>> gramatica, ArrayList<Regra> sentenca){
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, "S'");
		novaRegra.adicionarElemento("S");
		gramatica.put("S'", new LinkedHashSet<Regra>());
		gramatica.get("S'").add(novaRegra);
		Estado novoEstado = new Estado(novaRegra, 0, 0);

		enfileirar(novoEstado, 0);

		
		ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Estado>> tabela = chart.tabela;
		for (int i = 1; i < sentenca.size(); i++) {
			chart.tabela.put(new Integer(i), new ConcurrentLinkedQueue<Estado>());
		}
		
		System.out.println("Chart: " + chart);
		
		for (int i = 0, k = sentenca.size()-1; k >= 0; k--, i++) {
			String palavra = sentenca.get(k).direita.get(0).valor;
			// para cada linha do chart, verifica cada elemento de um estado
			for(Queue<Estado> estados: tabela.values()){
				for (Estado estado : estados) {
					Elemento elemento = estado.regra.direita.get(estado.coluna);
					// se proximo elemento do estado for não terminal
					if(estado.incompleto && elemento.tipo == Elemento.NAO_TERMINAL){
//						System.out.println("Chart ["+i+"]: "+estado.regra);
						predictor(estado, i, estado.coluna);
					}else if(estado.incompleto && elemento.tipo == Elemento.TERMINAL){
						scanner(estado, i, estado.coluna);
					}else{
						completer(estado, i, estado.coluna);
					}
				}
			}
		}

	}
	
	public void enfileirar(Estado estado, int linha){
		if(chart!=null){
			if(chart.tabela.containsKey(new Integer(linha))){
				if(!chart.tabela.get(new Integer(linha)).contains(estado))
					chart.adicionarEstado(estado, linha);
			}
		}
	}
	
	public void predictor(Estado estado, int linha, int coluna){
		String elemento = estado.regra.direita.get(coluna).valor;
		if(gramatica.containsKey(elemento)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento)){
				if(regra.tipo == Regra.NAO_LEXICO){
					Estado novoEstado = new Estado(regra, linha, coluna);
					enfileirar(novoEstado, linha);
				}
			}
		}
//		System.out.println("Predictor: " + chart);
	}
	
	public void scanner(Estado estado, int linha, int coluna){
		String elemento = estado.regra.direita.get(estado.linha).valor;
		if(lexico.contains(elemento)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento)){
				if(regra.direita.get(0).valor.equals(elemento)){
					Estado novoEstado = new Estado(regra, linha,coluna+1);
					enfileirar(novoEstado, coluna+1);
				}
			}
		}
//		System.out.println("Scanner: " + chart);
	}
	
	public void completer(Estado estado, int linha, int coluna){
		String elemento = estado.regra.direita.get(estado.linha).valor;
		
//		System.out.println("Completer: " + chart);
	}

	public static void main(String[] args) {
		new Earley();
	}
	
}
