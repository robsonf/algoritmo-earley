

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class ManipulaCorpus implements Serializable{

	private static final long serialVersionUID = -7064217564445825362L;
	public static final String REGRA_INICIAL_CORPUS = "_S_CORPUS_";
	public LinkedHashMap<String, LinkedHashSet<Regra>> gramatica = new LinkedHashMap<String, LinkedHashSet<Regra>>();
	public LinkedHashSet<Regra> regras = new LinkedHashSet<Regra>();
	public LinkedHashSet<String> lexico = new LinkedHashSet<String>();
	public ArrayList<ArrayList<Regra>> sentencas = new ArrayList<ArrayList<Regra>>();
	public ArrayList<DefaultMutableTreeNode> listaArvoresSintaticas = new ArrayList<DefaultMutableTreeNode>(); 
	
	public void extrairRegrasESentenca(String corpus){
		try {
			// leitura do corpus para a memoria
			StringBuffer sb = suavizarCorpus(corpus);

			// varre os caracteres do corpus criando uma árvore para cada sentença
			listaArvoresSintaticas = converterCorpusEmArvore(sb);

			// lista com árvores sintáticas inconsistentes (sujeira do corpus, eg. VB -> VB terminal)
			LinkedHashSet<DefaultMutableTreeNode> listaArvoresInconsistentes = new LinkedHashSet<DefaultMutableTreeNode>();

			// elimina regras redundantes mantendo a ordem de inserção das regras
			regras = removerRedundancia(listaArvoresSintaticas, listaArvoresInconsistentes);

			// elimina árvores sintáticas inconsistentes
			for (DefaultMutableTreeNode df : listaArvoresInconsistentes) {
				listaArvoresSintaticas.remove(df);
			}

			// constroi gramática a partir do conjunto de regras tratadas
		   	for (Regra r : regras) {
				String cabeca = r.variavel;
				if(gramatica.containsKey(cabeca)){
					gramatica.get(cabeca).add(r);
				}else{
					LinkedHashSet<Regra> l = new LinkedHashSet<Regra>();
					l.add(r);
					gramatica.put(cabeca, l);	
				}
		    }
		   	
		   	// agrupa as sentencas em uma lista de regras do tipo Lexico
			sentencas = extrairSentencas(listaArvoresSintaticas);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			ManipulaCorpus manipula = new ManipulaCorpus();
			// leitura do corpus para a memoria
			StringBuffer sb = suavizarCorpus("aires-treino.parsed");
//			StringBuffer sb = suavizarCorpus("corpus.txt");

			// varre os caracteres do corpus criando uma árvore para cada sentença
			ArrayList<DefaultMutableTreeNode> listaArvoresSintaticas = converterCorpusEmArvore(sb);

			// lista com árvores sintáticas inconsistentes (sujeira do corpus, eg. VB -> VB terminal)
			LinkedHashSet<DefaultMutableTreeNode> listaArvoresInconsistentes = new LinkedHashSet<DefaultMutableTreeNode>();

			// elimina regras redundantes mantendo a ordem de inserção das regras
			LinkedHashSet<Regra> conjuntoRegrasSemRepeticao = manipula.removerRedundancia(listaArvoresSintaticas, listaArvoresInconsistentes);

			System.out.println(listaArvoresSintaticas.size());

			// elimina árvores sintaticas inconsistentes da listaArvoresSintaticas
			for (DefaultMutableTreeNode df : listaArvoresInconsistentes) {
				listaArvoresSintaticas.remove(df);
			}

			// agrupa as sentencas em uma lista de regras do tipo Lexico
			ArrayList<ArrayList<Regra>> listaSentencas = extrairSentencas(listaArvoresSintaticas);
			System.out.println(listaSentencas.size());
			
			// agrupa regras no formato S -> A B | C D \n A -> a
			ArrayList<String> listaGramaticaFinal = formatarRegras(conjuntoRegrasSemRepeticao);

			// cria arquivo para persistir as regras em disco
			salvarGramaticaEmArquivo(listaGramaticaFinal);

	            
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Agrupa regras em um conjunto, onde a chave é a variavel mais a esquerda, 
	 * os valores são todas as regras que iniciam com a chave,
	 * respeitando a ordem de inserção, eliminando regras redundantes 
	 * e inconsistentes (sujeira do corpus, eg. VB -> VB terminal)
	 */
	private LinkedHashSet<Regra>  removerRedundancia(ArrayList<DefaultMutableTreeNode> listaArvoresSintaticas, LinkedHashSet<DefaultMutableTreeNode> listaArvoresInconsistentes) {
		LinkedHashSet<Regra> conjuntoRegrasSemRepeticao = new LinkedHashSet<Regra>();

		// cria nova regra inicial S
		for(DefaultMutableTreeNode df : listaArvoresSintaticas){
			conjuntoRegrasSemRepeticao.add(new Regra(Regra.NAO_LEXICO, REGRA_INICIAL_CORPUS, df.toString()));
		}

		// adiciona as regras em um conjunto (conjuntoRegras), para eliminar regras repetidas
		for (DefaultMutableTreeNode df : listaArvoresSintaticas) {
			// converte uma arvore de regras passada como parametro para uma pilha de regras (mantendo a ordem delas)
		    Stack<Regra> aux = new Stack<Regra>();
		    aux = converteArvoreParaPilha(df, aux);
		    boolean insconsistencia = false;
		    while(!aux.isEmpty()){
        	    Regra regra = aux.pop();
		    	insconsistencia = identificarIncosistencia(conjuntoRegrasSemRepeticao, regra); 
			    if(!insconsistencia){
			    	conjuntoRegrasSemRepeticao.add(regra);
			    	if(regra.tipo == Regra.LEXICO)
			    		lexico.add(regra.variavel);
			    }else
			    	listaArvoresInconsistentes.add(df);				    	
			}
		}
		return conjuntoRegrasSemRepeticao;
	}

	/**
	 * verifica se existe alguma regra do tipo lexico misturada com não lexicos
	 */
	private static boolean identificarIncosistencia(LinkedHashSet<Regra> regras, Regra regra) {
		boolean retorno = false;
	    LinkedHashMap<String, LinkedHashSet<Regra>> conjRegras = new LinkedHashMap<String, LinkedHashSet<Regra>>();
		for (Regra r : regras) {
			String cabeca = r.variavel;
			if(conjRegras.containsKey(cabeca)){
				conjRegras.get(cabeca).add(r);
			}else{
				conjRegras.put(cabeca, new LinkedHashSet<Regra>());	
			}
	    }
		Iterator<String> i = conjRegras.keySet().iterator();
	    while(i.hasNext()){
	    	LinkedHashSet<Regra> lRegras = (LinkedHashSet<Regra>)conjRegras.get(i.next());
	    	for (Regra r : lRegras) {
				if(r.variavel.equals(regra.variavel) && r.tipo != regra.tipo){
					retorno = true;
				}
			}
		}
		return retorno;
	}
	
	/**
	 * converte uma arvore de regras passada como parametro para uma pilha de regras (mantendo a ordem delas)
	 * elimina regras do tipo S -> S
	 */
    private static Stack<Regra> converteArvoreParaPilha(DefaultMutableTreeNode raiz, Stack<Regra> regras) {
    	String noPai = raiz.toString();
    	Regra regra = new Regra(Regra.NAO_LEXICO, noPai);
    	// Trata regras do tipo S -> S
        if(raiz.getChildCount() == 1){
        	DefaultMutableTreeNode filho = (DefaultMutableTreeNode)raiz.getChildAt(0);
        	if(noPai.equals(filho.toString())){
        		raiz = filho;
        	}
        }
    	Enumeration<DefaultMutableTreeNode> e = ((Enumeration<DefaultMutableTreeNode>)raiz.children());
    	while(e.hasMoreElements()){
	        DefaultMutableTreeNode filho = e.nextElement();
	        String no = filho.toString();
	        regra.adicionarElemento(no);
	        if(!filho.isLeaf())
	            converteArvoreParaPilha(filho, regras);
	        else
	        	regra.tipo = Regra.LEXICO;
        }
        regras.push(regra);
        return regras;
    }


    /**
	 * cria interfaca gráfica JTree para cada árvore sintatica
	 */
	private static void imprimirArvore(DefaultMutableTreeNode raiz) {
//		// arvore de teste
//		DefaultMutableTreeNode s = new DefaultMutableTreeNode("S");
//		DefaultMutableTreeNode vp = new DefaultMutableTreeNode("VP");
//		DefaultMutableTreeNode np = new DefaultMutableTreeNode("NP");
//		DefaultMutableTreeNode verb = new DefaultMutableTreeNode("Verb");
//		DefaultMutableTreeNode book = new DefaultMutableTreeNode("book");
//		DefaultMutableTreeNode det = new DefaultMutableTreeNode("Det");
//		DefaultMutableTreeNode that = new DefaultMutableTreeNode("that");
//		DefaultMutableTreeNode nominal = new DefaultMutableTreeNode("Nominal");
//		DefaultMutableTreeNode noun = new DefaultMutableTreeNode("Noun");
//		DefaultMutableTreeNode flight = new DefaultMutableTreeNode("flight");
//		
//		s.add(vp);
//		vp.add(verb);
//		vp.add(np);
//		verb.add(book);
//		np.add(det);
//		det.add(that);
//		np.add(nominal);
//		nominal.add(noun);
//		noun.add(flight);
//		
//		raiz = s;

		JTree tree = new JTree(raiz);
		JFrame j = new JFrame();
		JScrollPane scrollTree = new JScrollPane(tree);
		scrollTree.setViewportView(tree);
		j.add(scrollTree);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setTitle("Arvores Sintaticas");
        j.setSize(800,600);
        j.setVisible(true);
	}

	private static void imprimirMap(LinkedHashMap<String, LinkedHashSet<Regra>> conjuntoRegrasTratadas) {
		Map mapa = conjuntoRegrasTratadas;  
	    Iterator itChaves = mapa.keySet().iterator();  
	    while (itChaves.hasNext()) {  
	        String chave = (String)itChaves.next();  
	        LinkedHashSet<Regra> valor = (LinkedHashSet<Regra>)mapa.get(chave);  
	        System.out.println(chave + " = " + valor);  
	    }
	}

	/**
	 *  debug: ajuda a identificar quantas eram as regras problematicas
	 */
	private static void debugArvoresInconsistentes(LinkedHashSet<Regra> conjuntoRegrasSemRepeticao) {
		TreeMap<String, Integer> lexicos = new TreeMap<String, Integer>();
		TreeMap<String, Integer> naoLexicos = new TreeMap<String, Integer>();
		lexicos.put("NP", 0);
		naoLexicos.put("NP", 0);
		lexicos.put("NX", 0);
		naoLexicos.put("NX", 0);
		lexicos.put("VB", 0);
		naoLexicos.put("VB", 0);
		lexicos.put("WPP", 0);
		naoLexicos.put("WPP", 0);
//		identificarIncosistencia(conjuntoRegrasSemRepeticao, lexicos, naoLexicos);
		System.out.println("Lexico: " + lexicos);
		System.out.println("NaoLexico: " + naoLexicos);
	}

	/**
	 * agrupa regras no formato S -> A B | C D \n A -> a
	 */
	private static 	ArrayList<String> formatarRegras(LinkedHashSet<Regra> conjuntoRegrasSemRepeticao) throws IOException {
		// lista que guarda a gramatica no formato S -> A B | C D | abc
		ArrayList<String> listaGramaticaFinal = new ArrayList<String>();

		// dicionario com chave (Variavel mais a esquerda) e valores (conjunto de regras para cada chave)
		LinkedHashMap<String, LinkedHashSet<Regra>> regras = new LinkedHashMap<String, LinkedHashSet<Regra>>();
	    for (Regra regra : conjuntoRegrasSemRepeticao) {
			String cabeca = regra.variavel;
			if(regras.containsKey(cabeca)){
				(regras.get(cabeca)).add(regra);
			}else{
				LinkedHashSet<Regra> l = new LinkedHashSet<Regra>();
				l.add(regra);
				regras.put(cabeca, l);	
			}
	    }

	    Iterator<String> i = regras.keySet().iterator();
	    while(i.hasNext()){
	    	LinkedHashSet<Regra> lRegras = (LinkedHashSet<Regra>)regras.get(i.next());
	    	String cauda = "", cabeca = "";
	    	for (Regra r : lRegras) {
	    		cabeca = r.variavel;
				String elemento = "";
				ArrayList<Elemento> elementos = (ArrayList<Elemento>) r.direita;
				for(Elemento e : elementos)
						elemento += " " + e.valor;						
				cauda += " |" + elemento;
			}
	    	if(cauda.length()>0)
	    		listaGramaticaFinal.add(cabeca + " -> " + cauda.substring(3));
	    }

	    
		return listaGramaticaFinal;
	}

	private static void salvarGramaticaEmArquivo(
			ArrayList<String> listaGramaticaFinal) throws IOException {
		// escreve regras no arquivo regras.txt
		BufferedWriter bw = new BufferedWriter(new FileWriter("regras-corpus.txt"));
		String escreveLinha = "";
		for (int j = 0; j < listaGramaticaFinal.size(); j++) {
			escreveLinha = listaGramaticaFinal.get(j);
			bw.write(escreveLinha + "\n");
		}
		bw.close();
	}

	/**
	 * agrupa as sentencas em uma lista de regras do tipo Lexico
	 */
	private static ArrayList<ArrayList<Regra>> extrairSentencas(ArrayList<DefaultMutableTreeNode> listaArvoresSintaticas) {
		ArrayList<ArrayList<Regra>> listaSentencas = new ArrayList<ArrayList<Regra>>();
		for(DefaultMutableTreeNode df2 : listaArvoresSintaticas){
			// converte uma arvore de regras passada como parametro para uma pilha de regras (mantendo a ordem delas)
		    Stack<Regra> aux = new Stack<Regra>();
		    aux = converteArvoreParaPilha(df2, aux);
		    ArrayList<Regra> lexico = new ArrayList<Regra>();
		    while(!aux.isEmpty()){
		        Regra regra = aux.lastElement();
		        if(regra.tipo == Regra.LEXICO)
		        	lexico.add(regra);
		        aux.remove(regra);
		    }
		    listaSentencas.add(lexico);
		}
		return listaSentencas;
	}

	/**	 
	 * enquanto houver caracteres no texto
	 * 		   analisa caracter por caracter, ao encontrar '(', 
	 * 		      remove do texto e adiciona na pilhaChaves
	 * 		      cria adiciona nova arvore em (pilhaArvore) 
	 * 		   enquanto não achar outro '(' reconhece variáveis e terminais e os remove do texto
	 * 		   ao achar ')' desempilha a pilhaChaves, remove ')' do texto, adiciona arvore do topo da pilha à árvore anterior
	 * 		   com pilhaChaves vazia adiciona arvore sintatica completa na (listaArvoresSintaticas)
	 */
	private static ArrayList<DefaultMutableTreeNode> converterCorpusEmArvore(StringBuffer sb) {
		ArrayList<DefaultMutableTreeNode> listaArvoresSintaticas = new ArrayList<DefaultMutableTreeNode>();
		Stack<DefaultMutableTreeNode> pilhaArvores = new Stack<DefaultMutableTreeNode>();
		Stack<Character> pilhaChaves = new Stack<Character>();
		int i = 0;
		char abre = '(', fecha = ')';
		
		while (sb.length() > 0) {
			if (sb.charAt(i) == abre) {
				while (sb.charAt(i) == abre) {
					pilhaArvores.push(new DefaultMutableTreeNode());
					pilhaChaves.push(abre);
					sb.deleteCharAt(i);
				}
			} else if (sb.charAt(i) != fecha) {
				while (sb.charAt(i) != fecha && sb.charAt(i) != abre) {
					i++;
				}
				String var = sb.substring(0, i).trim();
				sb.delete(0, i);
				i = 0;
				String[] s = var.split("\\ ");
				if(!var.equals("")){
					DefaultMutableTreeNode arvore;
					if(pilhaArvores.isEmpty()){
						arvore = new DefaultMutableTreeNode();
						pilhaArvores.push(arvore);
					}else{
						arvore = pilhaArvores.peek();
					}
					String variavel = s[0].trim();
					variavel = variavel.equals(".")?"PONT":variavel.equals(",")?"VIRG":variavel;
					if (s.length > 1) {
						String terminal = s[1].trim().toLowerCase();
						arvore.setUserObject(variavel.length()==1?variavel+"X":variavel);
						arvore.add(new DefaultMutableTreeNode(terminal));
					} else if (s.length > 0 && !variavel.equalsIgnoreCase("")) {
						arvore.setUserObject(variavel);
					}
				}
			} else if (sb.charAt(i) == fecha) {
				while (!pilhaChaves.isEmpty() && sb.charAt(i) == fecha) {
					pilhaChaves.pop();
					sb.deleteCharAt(i);
					DefaultMutableTreeNode aux = null;
					if(!pilhaArvores.isEmpty()){
						aux = pilhaArvores.pop();
						if(!pilhaArvores.isEmpty())
							pilhaArvores.peek().add(aux);
						else
							listaArvoresSintaticas.add(aux);
					}
				}
			}
		}
		return listaArvoresSintaticas;
	}

	/**
	 * suavisa o corpus para uma unica linha com um unico espaco em branco separando os termos
	 */
	private static StringBuffer suavizarCorpus(String nomeArquivo) throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(nomeArquivo));
		String linha = br.readLine();
		while (linha != null) {
			sb.append(linha);
			linha = br.readLine();
		}
		String corpus = sb.toString().replaceAll("\\s+", " ");
		corpus = corpus.replaceAll("\\)\\ \\)","\\)\\)");
		corpus = corpus.replaceAll("(\\$|\\+|\\/)","X");
		sb = new StringBuffer(corpus);
		return sb;
	}

}
