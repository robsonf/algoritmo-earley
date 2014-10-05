import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

	public class Chart{
		public ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Estado>> tabela = null;
		public Chart() {
			super();
			ConcurrentLinkedQueue<Estado> estados = new ConcurrentLinkedQueue<Estado>();
			this.tabela = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Estado>>();
			this.tabela.put(new Integer(0), estados);
		}
		
		public ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Estado>> getTabela() {
			return tabela;
		}

		public void adicionarEstado(Estado estado, int linha){
			if(this.tabela.containsKey(new Integer(linha))){
				try {
					Estado e = (Estado)estado.clone();
					this.tabela.get(new Integer(linha)).add(e);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
		@Override
		public String toString() {
			String retorno = "";
			Set<Integer> chaves = tabela.keySet();
			Iterator it = chaves.iterator();
			while (it.hasNext()) {
				Integer chave = (Integer) it.next();
				retorno += "["+ chave.intValue() + "] : "+ tabela.get(chave.intValue()) + "\n";	
			}
			return retorno;
		}
}