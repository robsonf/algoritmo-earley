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
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.swing.tree.DefaultMutableTreeNode;


/* 
 * O Earley Parser recebe como entrada uma gramática (GLC) e uma sentença a ser analisada, retornando verdadeiro caso a sentença seja derivada pela gramática, ou falso caso contrário. Para isso, o algoritmo utiliza uma lista denominada chart, com tamanho igual ao número de palavras da sentença mais um; onde cada elemento do chart guarda uma lista de estados. Um estado pode ser representado por (X -> u • v , [i,j]). Ou seja, ele é composto de três partes: a regra em análise (X -> uv); a posição do ponto que indica qual elemento da regra já foi reconhecido; e, um índice que indica a qual parte da constituinte já fori reconhecida.
 * O primeiro elemento do chart é um estado chamado Dummy State: (S'-> • S, [0,0]), onde S' é uma nova variável que deriva S, o estado inicial da gramática. Uma observação importante é que não é possível acrescentar estados duplicados no chart. Já a analise de cada elemento pelo incremento do índice do ponto leva a uma das ações:
 * Predictor: o elemento analisado é não terminal e deriva outro não terminal, logo, acrescentam-se novos estados no chart incluindo todas as regras com a variável mais a esquerda (cabeça) iguais ao elemento analisado.
 * Scanner: o elemento analisado é não terminal e deriva um terminal (lexico), caso o terminal seja igual à palavra de mesmo indice do chart, acrescenta-se um novo estado completo ao chart (um estado é completo, quando o ponto já percorreu todos os elementos de sua regra).
 * Completer: analisa a variável mais a esquerda (cabeça) da regra de cada novo estado completo do chart, criando novos estados a partir do incremento do indice do ponto, dos estados que possuem o elemento, indexado pelo ponto, de valor igual à cabeça do estado completo.
 */

public class Earley {

	public static final String REGRA_INICIAL = "_S_EARLEY_";
	ArrayList<Estado> [] chart = null;
	LinkedHashSet<String> lexico = null;
	LinkedHashMap<String, LinkedHashSet<Regra>> gramatica = null;
	ArrayList<ArrayList<Regra>> sentencas = null;
	ArrayList<DefaultMutableTreeNode> listaArvoresSintaticas = null;
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
	    
	    int indice = 4;
	    DefaultMutableTreeNode arvore = listaArvoresSintaticas.get(indice);
	    System.out.println(sentencas.size());
	    ArrayList<Regra> sentenca = sentencas.get(indice);
	    System.out.println(gramatica);
	    System.out.println("Sentenca = " + sentenca);
	    System.out.println("Sentenca reconhecida pelo parser = " + parser(gramatica, sentenca));
	    System.out.println("Arvore validada = " + validarArvoreSintatica(arvore, sentenca));

//		imprimirChart();

	}
	
	public boolean validarArvoreSintatica(DefaultMutableTreeNode arvore, ArrayList<Regra> sentenca) {
		boolean retorno = false;
		int tamanhoSentenca = sentenca.size();
		for (int i = 0; i < chart[chart.length-1].size(); i++) {
			Estado estado = chart[chart.length-1].get(i);
			if(estado.regra.variavel.equals(REGRA_INICIAL) 
					&& estado.regra.direita.size()==1 
					&& estado.regra.direita.get(0).valor.equals(ManipulaCorpus.REGRA_INICIAL_CORPUS) 
					&& estado.incompleto==false
					&& estado.i == 0 && estado.j == tamanhoSentenca){
				Point point = ((Point)estado.backPointers.get(0).toArray()[0]);
				Estado inicialCorpus = chart[point.x].get(point.y);
//				point = ((Point)inicialCorpus.backPointers.get(0).toArray()[0]);
//				Estado inicialGramatica = chart[point.x].get(point.y);
				retorno = percorrerArvore(arvore, inicialCorpus);
			}
		}
		return retorno;
	}

	private boolean percorrerArvore(DefaultMutableTreeNode arvore, Estado estado) {
		boolean retorno = false;
		String raiz = arvore.toString();
//    	System.out.println(String.format("Ra:    %s",raiz.toString().trim()));
    	if(estado.regra.tipo != Regra.LEXICO){
	    	ArrayList<LinkedHashSet<Point>> pointers = estado.backPointers;
	    	for (int i = 0; i < pointers.size(); i++) {
	    		LinkedHashSet<Point> listaPointers = pointers.get(i);
	    		for (Point point : listaPointers) {
	    			Estado proximoEstado = chart[point.x].get(point.y);
	    			// se a quantidade de elementos mais a direita for igual a de filhos deste no
	    			if(proximoEstado.regra.direita.size() == arvore.getChildCount()){
	    				Enumeration<DefaultMutableTreeNode> e = ((Enumeration<DefaultMutableTreeNode>)arvore.children());
	    				ArrayList<Elemento> filhos = new ArrayList<Elemento>();
				    	while(e.hasMoreElements()){
					        DefaultMutableTreeNode filho = e.nextElement();
					        filhos.add(new Elemento(filho.toString()));
				    	}
				    	// se os elementos a direita da regra sao iguais aos dos filhos deste no
				    	if(filhos.equals(proximoEstado.regra.direita)){
				    		boolean aux = false;
				    		e = ((Enumeration<DefaultMutableTreeNode>)arvore.children());
					    	while(e.hasMoreElements()){
						        DefaultMutableTreeNode filho = e.nextElement();
						        aux = percorrerArvore(filho, proximoEstado);
						        if(!aux){
						        	break;
						        }else{
						        	retorno = true;
						        }
					    	}	
//				    		System.out.println("Mesmo elementos");
//		    				System.out.println(String.format("arvore: %s",filhos));
//		    				System.out.println(String.format("estado: %s",proximoEstado.regra.direita));
				    	}
	    			}else{
	//    		    	System.out.println("Estado size =" + estado.regra.direita.size());
	//    		    	System.out.println("Aravore size =" +arvore.getChildCount());
	//    		    	System.out.println("Estado Raiz: "+proximoEstado);    				
	    			}
				}
			}
    	}else{
			if(estado.regra.direita.get(0).valor.equals(raiz)){
//				System.out.println(raiz);
//				System.out.println(estado);
				retorno = true;
			}
		}
    	
    	return retorno;
//    	// PERCORRER TODA A ARVORE
//    	Enumeration<DefaultMutableTreeNode> e = ((Enumeration<DefaultMutableTreeNode>)arvore.children());
//    	while(e.hasMoreElements()){
//	        DefaultMutableTreeNode filho = e.nextElement();
//	        if(!filho.isLeaf()){
//	        	System.out.println(String.format("No:    %s",filho.toString().trim()));
//	        	Point point = ((Point)estado.backPointers.get(0).toArray()[0]);
//				Estado proximoEstado = chart[point.x].get(point.y);
//	        	System.out.println(String.format("EstNo: %s",proximoEstado.regra));
//	        	System.out.println("-------------------");
//	        	percorrerArvore(filho, proximoEstado);
//	        }else{
//	        	System.out.println(String.format("Folh:  %s",filho.toString().trim()));
//	        	System.out.println(String.format("EFolh: %s",estado.regra));
//	        	System.out.println("-------------------");
//	        }
//		}
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
					completer(estado, new Point(i,j));
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
	/**
	 * @param estado que já está completo
	 * @param backPointer indice (i,j) do chart para o estado completo
	 */
	public void completer(Estado estado, Point backPointer){
		String cabeca = estado.regra.variavel;
		int j = estado.i;
		int k = estado.j;
		for (int l = 0; l < chart[j].size(); l++) {
			// estado que dá origem ao estado com ponto avancado
			Estado estadoASerAvancado = chart[j].get(l);
			int i = estadoASerAvancado.i;
			Estado novoEstado = new Estado(estadoASerAvancado.regra, estadoASerAvancado.ponto+1, i, k, "Completer");
			if(estadoASerAvancado!=null 
				&& estadoASerAvancado.incompleto==true 
				&& estadoASerAvancado.getElemento()!=null 
				&& estadoASerAvancado.getElemento().valor.equals(cabeca)){

				// se não existe estado no chart
				if(!chart[k].contains(novoEstado)){
					while(novoEstado.backPointers.size() < estadoASerAvancado.backPointers.size()){
						novoEstado.backPointers.add(new LinkedHashSet<Point>());
					}
					for (int m = 0; m < estadoASerAvancado.backPointers.size(); m++) {
						novoEstado.backPointers.get(m).addAll((LinkedHashSet<Point>) estadoASerAvancado.backPointers.get(m).clone());
					}
					novoEstado.backPointers.get(novoEstado.backPointers.size()-1).add(backPointer);
					enfileirar(novoEstado, chart[k]);
				}else{
					// busca pelo estado que já existe no chart
					for (Estado antigo : chart[k]) {
						if(antigo.equals(novoEstado)){
							// atualiza backpointers do antigo
							while(antigo.backPointers.size() < estadoASerAvancado.backPointers.size() + 1){
								antigo.backPointers.add(new LinkedHashSet<Point>());
							}
							for (int m = 0; m < estadoASerAvancado.backPointers.size(); m++) {
								antigo.backPointers.get(m).addAll((LinkedHashSet<Point>)  estadoASerAvancado.backPointers.get(m).clone());
							}
							antigo.backPointers.get(antigo.backPointers.size()-1).add(backPointer);
							break;
						}
					}
				}
// DEBUG: Ó DO BOROGODÓ
//				if(backPointer.x == 3 && backPointer.y == 9){
//					System.out.println("NOVO: " + novoEstado);
//					System.out.println("NOVO: " + chart[3].get(10));
//					System.out.println(novoEstado.equals(chart[3].get(10)));
//					System.out.println(chart[j].contains(novoEstado));
//					System.out.println(chart[j].contains(chart[3].get(10)));
//					for (Estado e : chart[k]) {
//						System.out.println("E: "+e);
//						if(chart[3].get(10)==null ? e==null : chart[3].get(10).equals(e)){
//							System.out.println("$$$");
//						}
//						if(novoEstado==null ? e==null : novoEstado.equals(e)){
//							System.out.println("asdf");
//						}
//					}
//				}

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
				if(chart[i]!=null){
					retorno += String.format("Chart [%d] :\n", i);
					for (int j = 0; j < chart[i].size(); j++) {
						retorno += String.format("[%d][%d] %s",i,j,chart[i].get(j));	
					}
				}
						
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
				listaArvoresSintaticas = objetoSerializado.listaArvoresSintaticas; 
				ois.close();
			}else{
				ManipulaCorpus manipula = new ManipulaCorpus();
				manipula.extrairRegrasESentenca(nomeArquivo);
				gramatica = manipula.gramatica;
				lexico = manipula.lexico;
				sentencas = manipula.sentencas; 
				listaArvoresSintaticas = manipula.listaArvoresSintaticas;
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
