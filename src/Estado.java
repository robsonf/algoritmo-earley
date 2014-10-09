public class Estado{
		Regra regra;
		int entrada;
		int ponto;
		boolean incompleto;
		String acao;

		public Estado(Regra regra, int entrada, int ponto, String acao) {
			super();
			try {
				this.regra = (Regra)regra.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			this.entrada = entrada;
			this.ponto = ponto;
			if(this.ponto == regra.direita.size())
				this.incompleto = false;
			else
				this.incompleto = true;
			this.acao = acao;
		}
		
		public Elemento getElemento(){
			Elemento retorno = null;
			if(entrada < regra.direita.size())
				retorno = regra.direita.get(entrada);
			return retorno;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return new Estado((Regra) this.regra.clone(), this.entrada, this.ponto, this.acao);
		}
		
		@Override
		public String toString() {
			String retorno = "";
			if(regra!=null)
				retorno = regra + " ["+entrada+"]["+ponto+"]"+ ", Acao: " + acao+  ", Elemento: " + regra.direita.get(entrada).valor + ", Incompleto: " + Boolean.toString(incompleto); 
			return retorno+"\n";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ponto;
			result = prime * result + (incompleto ? 1231 : 1237);
			result = prime * result + entrada;
			result = prime * result + ((regra == null) ? 0 : regra.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Estado other = (Estado) obj;
			if (ponto != other.ponto)
				return false;
			if (incompleto != other.incompleto)
				return false;
			if (entrada != other.entrada)
				return false;
			if (regra == null) {
				if (other.regra != null)
					return false;
			} else if (!regra.equals(other.regra))
				return false;
			return true;
		}
}