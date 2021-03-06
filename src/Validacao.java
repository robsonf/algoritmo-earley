import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;


public class Validacao {

	public static void main(String[] args) {
		String log = "";
		Earley earley = new Earley();

		long tempoInicialTotal = System.currentTimeMillis();
		long tempoInicial = System.currentTimeMillis();  
	    earley.extrairGramaticaESentencas("aires-treino.parsed", 0.9, 0.1);
//	    earley.obterGramatica("corpus-pequeno");
//	    earley.extrairGramaticaESentencas("corpus-livro", 0.8, 0.2);

	    long tempoFinal = System.currentTimeMillis();  
	    System.out.println(String.format("Gramatica: %d segundos.", (tempoFinal - tempoInicial)/1000));
		log += ("\n" + String.format("Gramatica: %d segundos.", (tempoFinal - tempoInicial)/1000));

		
		int contadorCobertura = 0, contadorPrecisao = 0;
		int quantidadeSentencas = (int)(earley.listaSentencasCorpus.size()*0.1);

		log += ("\n Quantidade de sentencas: " + quantidadeSentencas);
		gravarExperimento(log, "inicio");
		
		for (int i = 0; i < quantidadeSentencas; i++) {
			boolean validaParser = false, validaArvore = false;
	    	DefaultMutableTreeNode arvore = earley.listaSentencasCorpus.get(i);
		    ArrayList<Regra> sentenca = earley.sentencas.get(i);
		    log = ("\n" + String.format("Sentenca [%d] = %s" ,i , sentenca));
		    System.out.println("Sentenca = " + sentenca);
		    tempoInicial = System.currentTimeMillis(); 
		    System.out.println("Sentenca reconhecida pelo parser = " + earley.parser(earley.gramatica, sentenca));
		    if(earley.parser(earley.gramatica, sentenca)){
		    	validaParser = true;
		    	contadorCobertura++;
		    }
		    log += ("\n" + "Sentenca reconhecida pelo parser = " + validaParser);
			log += ("\n" + "contadorCobertura = " + contadorCobertura);
			tempoFinal = System.currentTimeMillis();  
			System.out.println(String.format("Parser Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			log += ("\n" + String.format("Parser Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			tempoInicial = System.currentTimeMillis(); 
		    System.out.println("Arvore validada = " + earley.validarArvoreSintatica(arvore, sentenca.size()));
			if(validaParser){
				if(earley.validarArvoreSintatica(arvore, sentenca.size())){
			    	validaArvore = true;
			    	contadorPrecisao++;
			    }
			}
			log += ("\n" + "Arvore validada = " + validaArvore);
			log += ("\n" + "contadorPrecisao = " + contadorPrecisao);
			tempoFinal = System.currentTimeMillis();  
			System.out.println(String.format("Arvore Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			log += ("\n" + String.format("Arvore Tempo: %d segundos.", (tempoFinal - tempoInicial)/1000));
			
			gravarExperimento(log, i);
	    }

	    double cobertura = 0, precisao = 0;
		if(quantidadeSentencas!=0)
	    	cobertura = (double)contadorCobertura / quantidadeSentencas;
		if(contadorCobertura!=0)
		    precisao = (double)contadorPrecisao / contadorCobertura;
	    
	    log = ("PRECISAO : " + precisao);
	    log += ("\nCOBERTURA : " + cobertura);
	    log += ("\nContador Cobertura: " + contadorCobertura);
	    log += ("\nContador Precisao: " + contadorPrecisao);
	    log += ("\nQuandidade de Sentencas avaliadas: " + quantidadeSentencas);

	    long tempoFinalTotal = System.currentTimeMillis();  
	    System.out.println(String.format("Tempo total: %d segundos.", (tempoFinalTotal - tempoInicialTotal)/1000));

	    System.out.println(log);
	    gravarExperimento(log, "fim");
	}
	
	/**
	 * Salva o resultado dos experimentos em arquivo 
	 */
	public static void gravarExperimento(String log, int contador){
		try {
			File diretorio = new File("experimentos");
			if(!diretorio.exists()){
				diretorio.mkdir();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("experimentos"+File.separator+String.valueOf(contador)+".txt"));
			bw.write(log);	
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Salva o resultado dos experimentos em arquivo 
	 */
	public static void gravarExperimento(String log, String arquivo){
		try {
			File diretorio = new File("experimentos");
			if(!diretorio.exists()){
				diretorio.mkdir();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("experimentos"+File.separator+arquivo+".txt"));
			bw.write(log);	
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
