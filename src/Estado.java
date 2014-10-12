public class Estado{
		Regra regra;
		int i;
		int j;
		boolean incompleto;
		String acao;

		public Estado(Regra regra, int i, int j, String acao) {
			super();
			try {
				this.regra = (Regra)regra.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			this.i = i;
			this.j = j;
			if(this.j == regra.direita.size())
				this.incompleto = false;
			else
				this.incompleto = true;
			this.acao = acao;
		}
		
		public Elemento getElemento(){
			Elemento retorno = null;
			if(i < regra.direita.size())
				retorno = regra.direita.get(i);
			return retorno;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			return new Estado((Regra) this.regra.clone(), this.i, this.j, this.acao);
		}
		
		@Override
		public String toString() {
			String retorno = "";
			if(regra!=null)
				retorno = String.format("%-30s [%d][%d], Acao: %s, Elemento: %s, Incompleto: %s", regra,i,j,acao,regra.direita.get(i).valor, Boolean.toString(incompleto)); 
			return retorno+"\n";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + j;
			result = prime * result + (incompleto ? 1231 : 1237);
			result = prime * result + i;
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
			if (j != other.j)
				return false;
			if (incompleto != other.incompleto)
				return false;
			if (i != other.i)
				return false;
			if (regra == null) {
				if (other.regra != null)
					return false;
			} else if (!regra.equals(other.regra))
				return false;
			return true;
		}
}