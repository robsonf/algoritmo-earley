import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract public class Teste {
	public static void main(String[] args) {
		
		ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Estado>> tabela = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Estado>>();
		System.out.println(tabela);
		
		Regra novaRegra = new Regra(Regra.NAO_LEXICO, "S'");
		novaRegra.adicionarElemento("S");
		Estado novoEstado = new Estado(novaRegra, 0, 0);
		ConcurrentLinkedQueue<Estado> novo = new ConcurrentLinkedQueue<Estado>();
		novo.add(novoEstado);
		tabela.put(new Integer(0), novo);
		
		System.out.println(tabela);
		
		// para cada linha do chart, verifica cada elemento de um estado
		Iterator it = tabela.values().iterator();
		int i = 1;
		for(ConcurrentLinkedQueue<Estado> estados: tabela.values()){
			for (Estado estado : estados) {
				ConcurrentLinkedQueue<Estado> novaFila = new ConcurrentLinkedQueue<Estado>();
				novo.add(estado);
				tabela.put(new Integer(i), novaFila);
			}
		}
		
		System.out.println(tabela);
		
		
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
