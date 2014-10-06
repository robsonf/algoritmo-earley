import java.util.concurrent.ConcurrentLinkedQueue;

	public class Chart{
		public ConcurrentLinkedQueue<Estado> [] tabela  = null;

		public Chart(int tamanho) {
			super();
			this.tabela = new ConcurrentLinkedQueue[tamanho];
			for (int i = 0; i < tamanho; i++) {
				tabela[i] = new ConcurrentLinkedQueue<Estado>();
			}
		}
		
		public ConcurrentLinkedQueue<Estado> getEstados(int linha) {
			ConcurrentLinkedQueue<Estado> retorno = null;
			if(tabela!=null)
				retorno = tabela[linha];
			return retorno;
		}

		public void adicionarEstado(Estado estado, ConcurrentLinkedQueue<Estado> estados){
			try {
				Estado e = (Estado)estado.clone();
				estados.add(e);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
		
		@Override
		public String toString() {
			String retorno = "";
			if(tabela!=null)
			for (int i = 0; i < tabela.length; i++) {
				retorno += "["+ i + "] : "+ tabela[i] + "\n";	
			}
			return retorno;
		}
}