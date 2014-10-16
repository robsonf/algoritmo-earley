import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/*
 * O Earley Parser recebe como entrada uma gramática (GLC) e uma sentença 
 * a ser analisada, retornando verdadeiro ou falso caso a sentença 
 * seja derivada pela gramática. Para isso, o algoritmo utiliza uma 
 * lista denominada chart, com tamanho igual ao número de palavras da
 * sentença mais um; onde cada elemento do chart guarda uma lista
 * de estados. Um estado pode ser representado por (X -> u • v , [i,j]). 
 * Ou seja, ele é composto de três partes: a regra em análise(X -> uv); 
 * a posição do ponto que indica qual elemento da regra já foi reconhecido;
 * e, um índice que indica a partir de qual posição inicial e final de 
 * palavras da sentença já foram reconhecidas.
 * 
 * O primeiro elemento do chart é chamado de Dummy State: 
 * chart[0] = (S'-> • S, [0,0]), onde S é o estado inicial da gramática
 * 
 * Não é possível acrescentar estados duplicados no chart.
 * 
 * A analise de cada elemento pelo índice do ponto leva a uma das ações:
 * 
 * Predictor: o elemento é não terminal e deriva outro não terminal,
 * acrescenta novo estado no chart incrementando o ponto.
 * 
 * Scanner: o elemento é não terminal e deriva um terminal (lexico),
 * caso o terminal seja igual à palavra do indice [j] acrescenta um 
 * novo estado do tipo "completo" (um estado é completo quando o ponto
 * já percorreu todos os elementos de sua regra).
 *  
 * Completer: cria novos estados no próximo indice do chart a partir dos
 * estados "completos", ou seja, verifica a cabeça da regra de um estado 
 * completo e incrementa o ponto dos elementos da regra dos estados anteriores.
 */

public class Earley {

	public static final String REGRA_INICIAL = "_S_EARLEY_";
	ArrayList<Estado> [] chart = null;
	LinkedHashSet<String> lexico = null;
	LinkedHashMap<String, LinkedHashSet<Regra>> gramatica = null;
	ArrayList<ArrayList<Regra>> sentencas = null;
	int contador = 0;
	
	public Earley() {
//	    long tempoInicial = System.currentTimeMillis();  
//	    obterGramatica("aires-treino.parsed");
//	    obterGramatica("corpus-pequeno");
	    obterGramatica("corpus-livro");
//		long tempoFinal = System.currentTimeMillis();  
//	    System.out.println(String.format("Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));  

//		for (ArrayList<Regra> sentenca: sentencas) {
//		    tempoInicial = System.currentTimeMillis(); 
//			System.out.println(sentenca);
//			if(parser(gramatica, sentenca))
//				contador++;
//			tempoFinal = System.currentTimeMillis();  
//			System.out.println(String.format("Sentenca: %d, Tempo: %d segundos.", contador, (tempoFinal - tempoInicial)/1000));
//			gravarChart(contador);
//		}
	    System.out.println(sentencas.get(5));
	    System.out.println(parser(gramatica, sentencas.get(5)));

//		System.out.println(String.format("Sentenca: %d, Tempo: %d segundos.", contador, (tempoFinal - tempoInicial)/1000));
		imprimirChart();
	}
	
	public boolean parser(LinkedHashMap<String, LinkedHashSet<Regra>> gramatica, ArrayList<Regra> sentenca){
		boolean reconheceu = false;
		// cria novas possições no chart, uma para cada palavra da sentenca
		chart  = new ArrayList[sentenca.size()+1];
		for (int i = 0; i < sentenca.size()+1; i++) {
			chart[i] = new ArrayList<Estado>();
		}

		// adiciona estado S(0) ao chart
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, REGRA_INICIAL, ManipulaCorpus.REGRA_INICIAL_CORPUS);
		Estado novoEstado = new Estado(novaRegra, 0, 0, 0, "Dummy start state");
		enfileirar(novoEstado, chart[0]);

		// para cada palavra da sentenca
		for (int i = 0; i <= sentenca.size(); i++) {
			String palavra = null;
			int k = sentenca.size()-i-1;
			if(k<sentenca.size() && k>=0)
				palavra = sentenca.get(k).direita.get(0).valor;
			// verifica cada estado na lista de posicao [i] do chart
			ArrayList<Estado> estados = chart[i];
			for (int j = 0; j < estados.size(); j++) {
				Estado estado = estados.get(j);
				// se existe proximo elemento no lado direito de um ponto
				Elemento elemento = estado.getElemento();
				if(estado.incompleto){
					// elemento não é lexico
					if(elemento!=null){
						if(!lexico.contains(elemento.valor)){
							predictor(estado);
						}else{
							scanner(estado, palavra);							
						}
					}
				}else{
					completer(estado);
				}
			}
		}
		
		for(Estado estado : chart[sentenca.size()]){
			if(estado.regra.variavel.equals(REGRA_INICIAL) 
				&& estado.regra.direita.size()==1 
				&& estado.regra.direita.get(0).valor.equals(ManipulaCorpus.REGRA_INICIAL_CORPUS) 
				&& estado.incompleto==false
				&& estado.i == 0 && estado.j == sentenca.size()){
				reconheceu = true;
				break;
			}
		}
		return reconheceu;
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
//			else{
//				for (Estado antigo : estados) {
//					if(antigo.equals(estado)){
//						for (Point tracker : estado.backPointers) {
//							antigo.backPointers.add((Point) tracker.clone());
//						}
//						break;
//					}
//				}
//			}
		}
	}
	
	public void predictor(Estado estado){
		Elemento elemento = estado.getElemento();
		if(gramatica.containsKey(elemento.valor)){
			for(Regra regra : (LinkedHashSet<Regra>)gramatica.get(elemento.valor)){
				if(regra.tipo == Regra.NAO_LEXICO){
					Estado novoEstado = new Estado(regra, 0, estado.j, estado.j, "Predictor");
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
				Estado novoEstado = new Estado(regra, 1, estado.j,estado.j+1, "Scanner");
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
			Estado novoEstado = new Estado(estadoChart.regra, estadoChart.ponto+1, i, k, "Completer");
			if(estadoChart!=null 
				&& estadoChart.incompleto==true 
				&& estadoChart.getElemento()!=null 
				&& estadoChart.getElemento().valor.equals(cabeca)){
				enfileirar(novoEstado, chart[k]);
			}
		}
//		imprimirChart();
	}

	public static void main(String[] args) {
		new Earley();
	}
	
	public void gravarChart(int indice){
		try {
			File diretorio = new File("charts");
			if(!diretorio.exists()){
				diretorio.mkdir();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("charts"+File.separator+"chart"+indice));
			if(chart!=null){
				for (int i = 0; i < chart.length; i++) {
					if(chart[i]!=null)
						bw.write(String.format("Chart [%d] :\n%s\n",i,chart[i]));	
				}
			}
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void imprimirChart(){
		String retorno = "";
		if(chart!=null){
			for (int i = 0; i < chart.length; i++) {
				if(chart[i]!=null)
					retorno += String.format("Chart [%d] :\n%s\n",i,chart[i]);	
			}
		}
		System.out.println(retorno+"\n");
	}
	
	public void obterGramatica(String nomeArquivo){
		try {
			File arquivo = new File(nomeArquivo+".dat");
			if(arquivo.exists()){
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo));
				ManipulaCorpus objetoSerializado = (ManipulaCorpus) ois.readObject();
				gramatica = objetoSerializado.gramatica;
				lexico = objetoSerializado.lexico;
				sentencas = objetoSerializado.sentencas; 
				ois.close();
			}else{
				ManipulaCorpus manipula = new ManipulaCorpus();
				manipula.extrairRegrasESentenca(nomeArquivo);
				gramatica = manipula.gramatica;
				lexico = manipula.lexico;
				sentencas = manipula.sentencas; 
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo));
				oos.writeObject(manipula);
				oos.close();		
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
