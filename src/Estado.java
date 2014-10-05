public class Estado{
		Regra regra;
		int linha;
		int coluna;
		boolean incompleto;

		public Estado(Regra regra, int linha, int coluna) {
			super();
			try {
				this.regra = (Regra)regra.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			this.linha = linha;
			this.coluna = coluna;
			this.incompleto = true;
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return new Estado((Regra) this.regra.clone(), this.linha, this.coluna);
		}
		
		@Override
		public String toString() {
			String retorno = "";
			if(regra!=null)
				retorno = "Chart: ["+linha+"]["+coluna+"]"+", Regra: "+ regra + ", Elemento: " + regra.direita.get(linha).valor + ", Incompleto: " + Boolean.toString(incompleto); 
			return retorno;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + coluna;
			result = prime * result + (incompleto ? 1231 : 1237);
			result = prime * result + linha;
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
			if (coluna != other.coluna)
				return false;
			if (incompleto != other.incompleto)
				return false;
			if (linha != other.linha)
				return false;
			if (regra == null) {
				if (other.regra != null)
					return false;
			} else if (!regra.equals(other.regra))
				return false;
			return true;
		}
}