import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract public class Teste {
	public static void main(String[] args) {
		
		ConcurrentLinkedQueue<Estado> [] tabela = new ConcurrentLinkedQueue[2];
		System.out.println(Arrays.toString(tabela));
		
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, "S'");
		novaRegra.adicionarElemento("S");
		Estado novoEstado = new Estado(novaRegra, 0, 0);
		ConcurrentLinkedQueue<Estado> novo = new ConcurrentLinkedQueue<Estado>();
		novo.add(novoEstado);
		tabela[0] = novo;
		
		System.out.println(Arrays.toString(tabela));
		
		// para cada linha do chart, verifica cada elemento de um estado
		
		
//		CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
//		list.add("vivek");
//		list.add("kumar");
//		for (String string : list) {
//			System.out.println(string);
//			list.add("abhishek");
//			System.out.println(list.size());
//		}
//		System.out.println("After modification:");
//		Iterator i2 = list.iterator();
//		while (i2.hasNext()) {
//			System.out.println(i2.next());
//		}
	}
}
