import java.util.ArrayList;
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

	ConcurrentLinkedQueue<Estado> [] chart = null;
	LinkedHashSet<String> lexico = null;
	public static LinkedHashMap<String, LinkedHashSet<Regra>> gramatica = null;
	ArrayList<ArrayList<Regra>> sentencas = null;
	
	public Earley() {
		ManipulaCorpus.extrairRegrasESentenca();

		gramatica = ManipulaCorpus.gramatica;
		lexico = ManipulaCorpus.lexico;
		sentencas = ManipulaCorpus.sentencas; 

//		for (ArrayList<Regra> sentenca: sentencas) {
//			parser(gramatica, sentenca);
//		}

		parser(gramatica, sentencas.get(0));

	}
	
	public void parser(LinkedHashMap<String, LinkedHashSet<Regra>> gramatica, ArrayList<Regra> sentenca){
		// cria novas possições no chart, uma para cada palavra da sentenca
		chart  = new ConcurrentLinkedQueue[sentenca.size()];
		for (int i = 0; i < sentenca.size(); i++) {
			chart[i] = new ConcurrentLinkedQueue<Estado>();
		}

		// adiciona estado S(0) ao chart
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, "S", "ST");
		Estado novoEstado = new Estado(novaRegra, 0, 0);
		enfileirar(novoEstado, chart[0]);

		// adiciona nova regra S' na gramática
//		gramatica.put("S'", new LinkedHashSet<Regra>());
//		gramatica.get("S'").add(novaRegra);
		
		// para cada palavra da sentenca
		for (int i = 0, k = sentenca.size()-1; k >= 0; k--, i++) {
			String palavra = sentenca.get(k).direita.get(0).valor;
			// para cada linha do chart, verifica cada elemento de um estado
			ConcurrentLinkedQueue<Estado> estados = chart[i];
			int j = 0;
			for (Estado estado : estados) {
				// se existe proximo elemento no estado 
				if(estado.incompleto){
					Elemento elemento = estado.getProximoElemento(j);
					if(elemento != null){
						if(elemento.tipo == Elemento.NAO_TERMINAL){
							predictor(estado, i, j);
						}
					}
					else if(estado.incompleto && elemento.tipo == Elemento.TERMINAL){
						scanner(estado, i, j);
					}
				}
				else{
					completer(estado, j, j+1);
				}
				j++;
			}
		}

		imprimirChart();
	}
	
	public void enfileirar(Estado estado, ConcurrentLinkedQueue<Estado> estados){
		if(chart!=null){
			if(!estados.contains(estado)){
				try {
					Estado e = (Estado)estado.clone();
					estados.add(e);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void predictor(Estado estado, int i, int j){
		Elemento elemento = estado.getProximoElemento(j);
		if(gramatica.containsKey(elemento.valor)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento.valor)){
				System.out.println(gramatica);
				if(regra.tipo == Regra.NAO_LEXICO){
					Estado novoEstado = new Estado(regra, j, j);
					enfileirar(novoEstado, chart[j]);
				}
			}
		}
//		System.out.println("Predictor: " + chart);
	}
	
	public void scanner(Estado estado, int i, int j){
		String elemento = estado.regra.direita.get(estado.entrada).valor;
		if(lexico.contains(elemento)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento)){
				if(regra.direita.get(0).valor.equals(elemento)){
					Estado novoEstado = new Estado(regra, j,j+1);
					enfileirar(novoEstado, chart[j+1]);
				}
			}
		}
//		System.out.println("Scanner: " + chart);
	}
	
	public void completer(Estado estado, int i, int j){
		String elemento = estado.regra.direita.get(estado.entrada).valor;
		System.out.println("E: "+elemento);
		for(Estado estadoChart : chart[i]){
			System.out.println("V: "+estado.regra.variavel);
			if(estadoChart.incompleto && elemento.equals(estado.regra.variavel)){
				estadoChart.ponto = j;
				enfileirar(estadoChart, chart[j]);
			}
		}
	}

	public static void main(String[] args) {
		new Earley();
	}
	
	public void imprimirChart(){
		String retorno = "";
		if(chart!=null){
			for (int i = 0; i < chart.length; i++) {
				retorno += "Chart ["+ i + "] :\n"+ chart[i] + "\n";	
			}
		}
		System.out.println(retorno+"\n");
	}
}
