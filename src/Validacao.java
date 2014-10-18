import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;


public class Validacao {

	public static void main(String[] args) {
		String log = "teste";
		Earley earley = new Earley();
	    long tempoInicial = System.currentTimeMillis();  
	    earley.obterGramatica("aires-treino.parsed");
//	    earley.obterGramatica("corpus-pequeno");
//	    earley.obterGramatica("corpus-livro");
		long tempoFinal = System.currentTimeMillis();  
//	    System.out.println(String.format("Gramatica: %d segundos.", (tempoFinal - tempoInicial)/1000));
		log += ("\n" + String.format("Gramatica: %d segundos.", (tempoFinal - tempoInicial)/1000));

		int contadorCobertura = 0, contadorPrecisao = 0;
		boolean validaParser = false, validaArvore = false;
		int quantidadeSentencas = (int)(earley.listaArvoresSintaticas.size()*0.2);
	    for (int i = 0; i < quantidadeSentencas; i++) {
		    DefaultMutableTreeNode arvore = earley.listaArvoresSintaticas.get(i);
		    ArrayList<Regra> sentenca = earley.sentencas.get(i);
		    log += ("\n" + String.format("Sentenca [%d] = %s" ,i , sentenca));
//		    System.out.println("Sentenca = " + sentenca);
		    tempoInicial = System.currentTimeMillis(); 
//		    System.out.println("Sentenca reconhecida pelo parser = " + earley.parser(earley.gramatica, sentenca));
		    if(earley.parser(earley.gramatica, sentenca)){
		    	validaParser = true;
		    	contadorCobertura++;
		    }
		    log += ("\n" + "Sentenca reconhecida pelo parser = " + validaParser);
			tempoFinal = System.currentTimeMillis();  
//			System.out.println(String.format("Parser Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			log += ("\n" + String.format("Parser Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			tempoInicial = System.currentTimeMillis(); 
//		    System.out.println("Arvore validada = " + earley.validarArvoreSintatica(arvore, sentenca.size()));
			if(validaParser){
				if(earley.validarArvoreSintatica(arvore, sentenca.size())){
			    	validaArvore = true;
			    	contadorPrecisao++;
			    }
				log += ("\n" + "Arvore validada = " + validaArvore);
				tempoFinal = System.currentTimeMillis();  
	//			System.out.println(String.format("Arvore Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
				log += ("\n" + String.format("Arvore Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			}
	    }
	    
	    double cobertura = contadorCobertura / quantidadeSentencas;
	    double precisao = contadorPrecisao / contadorCobertura;
	    
	    log += ("\n" + "PRECISAO : " + precisao);
	    log += ("\n" + "COBERTURA : " + cobertura);
	    
	    gravarExperimento(log);
	}
	
	/**
	 * Salva o resultado dos experimentos em arquivo 
	 */
	public static void gravarExperimento(String log){
		try {
			File diretorio = new File("experimentos");
			if(!diretorio.exists()){
				diretorio.mkdir();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("experimentos"+File.separator+"1.txt"));
			bw.write(log);	
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
