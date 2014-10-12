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

	public static final String REGRA_INICIAL = "_S_EARLEY_";
	ArrayList<Estado> [] chart = null;
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

		parser(gramatica, sentencas.get(sentencas.size()-1));

	}
	
	public void parser(LinkedHashMap<String, LinkedHashSet<Regra>> gramatica, ArrayList<Regra> sentenca){
		// cria novas possições no chart, uma para cada palavra da sentenca
		chart  = new ArrayList[sentenca.size()+1];
		for (int i = 0; i < sentenca.size()+1; i++) {
			chart[i] = new ArrayList<Estado>();
		}

		// adiciona estado S(0) ao chart
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, REGRA_INICIAL, ManipulaCorpus.REGRA_INICIAL_CORPUS);
		Estado novoEstado = new Estado(novaRegra, 0, 0, "Dummy start state");
		enfileirar(novoEstado, chart[0]);

		// para cada palavra da sentenca
		for (int i = 0, k = sentenca.size()-1; k >= 0; k--, i++) {
			String palavra = null;
			if(k<sentenca.size())
				palavra = sentenca.get(k).direita.get(0).valor;
			// verifica cada estado na lista de posicao [i] do chart
			ArrayList<Estado> estados = chart[i];
			int j = 0; 
			while(estados != null && j < estados.size()) {
				Estado estado = estados.get(j);
				// se existe proximo elemento no lado direito de um ponto
				Elemento elemento = estado.getElemento();
				if(estado.incompleto){
					// elemento não é lexico
					if(!lexico.contains(elemento.valor)){
						predictor(estado);
					}else{
						scanner(estado, palavra);							
					}
				}else{
					completer(estado);
				}
				j++;
			}
		}

		imprimirChart();
	}
	
	public void enfileirar(Estado estado, ArrayList<Estado> estados){
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
	
	public void predictor(Estado estado){
		Elemento elemento = estado.getElemento();
		if(gramatica.containsKey(elemento.valor)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento.valor)){
				if(regra.tipo == Regra.NAO_LEXICO){
					Estado novoEstado = new Estado(regra, estado.j, estado.j, "Predictor");
					enfileirar(novoEstado, chart[estado.j]);
				}
			}
		}
//		System.out.println("Predictor: " + chart);
	}
	
	public void scanner(Estado estado, String palavra){
		String elemento = estado.getElemento().valor;
		for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento)){
			if(regra.tipo == Regra.LEXICO && regra.direita.get(0).valor.equals(palavra)){
				Estado novoEstado = new Estado(regra, estado.j,estado.j+1, "Scanner");
				enfileirar(novoEstado, chart[estado.j+1]);
			}
		}
//		System.out.println("Scanner: " + chart);
	}
	
	public void completer(Estado estado){
		String cabeca = estado.regra.variavel;
		int j = estado.i;
		int k = estado.j;
		for (int l = 0; l < chart[j].size(); l++) {
			Estado estadoChart = chart[j].get(l);
			int i = estadoChart.i;
			//estadoChart.regra.direita.size() <= estado.regra.direita.size()
			if(estadoChart.incompleto && estadoChart.getElemento().valor.equals(cabeca) && estadoChart.regra.direita.size() <= estado.regra.direita.size()){
				System.out.println("Elemento: "+cabeca);

				Estado novoEstado = new Estado(estadoChart.regra, i, k, "Completer");
				enfileirar(novoEstado, chart[k]);
			}
		}
		imprimirChart();
	}

	public static void main(String[] args) {
		new Earley();
	}
	
	public void imprimirChart(){
		String retorno = "";
		if(chart!=null){
			for (int i = 0; i < chart.length; i++) {
				retorno += String.format("Chart [%d] :\n%s\n",i,chart[i]);	
			}
		}
		System.out.println(retorno+"\n");
	}
}
