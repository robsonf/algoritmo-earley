

public class Elemento {
	public static final int TERMINAL = 1;
	public static final int NAO_TERMINAL = 0;
	int tipo;
	String valor;
	public Elemento(String valor) {
		super();
		this.valor = valor;
		if(!eTerminal(valor)){
			this.tipo = Elemento.NAO_TERMINAL;
		}else{
			this.tipo = Elemento.TERMINAL;
		}
	}
	
	public static boolean eTerminal(String variavel) {
		return variavel.equals(variavel.toLowerCase()) || (variavel.toUpperCase().equals(variavel.toLowerCase()));
	}

	@Override
	public String toString() {
		return valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tipo;
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
		Elemento other = (Elemento) obj;
		if (tipo != other.tipo)
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}
}
