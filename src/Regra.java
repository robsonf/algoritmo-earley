


import java.util.ArrayList;

public class Regra {
	public static final int LEXICO = 1;
	public static final int NAO_LEXICO = 0;
	int tipo;
	String variavel;
	ArrayList<Elemento> direita;
	
	public Regra(int tipo, String variavel) {
		super();
		this.tipo = tipo;
		this.variavel = variavel;
		this.direita = null;
	}
	
	public Regra(int tipo, String variavel, String elemento) {
		this(tipo, variavel);
		adicionarElemento(elemento);
	}
	
	public void adicionarElemento(String elemento){
		Elemento e = new Elemento(elemento);
		if(direita == null)
			direita = new ArrayList<Elemento>();
		direita.add(e);
	}

	public void adicionarElementos(Elemento valor){
		adicionarElemento(valor.valor);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Regra regra = new Regra(this.tipo, this.variavel);
		ArrayList<Elemento> direitaClone = new ArrayList<Elemento>();
		for (Elemento e : direita) {
			direitaClone.add((Elemento)e.clone());
		}
		regra.direita = direitaClone;
		return regra;
	}
	@Override
	public String toString() {
		if(direita != null)
			return variavel + " -> " + direita.toString();
		else
			return variavel; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direita == null) ? 0 : direita.hashCode());
		result = prime * result + tipo;
		result = prime * result
				+ ((variavel == null) ? 0 : variavel.hashCode());
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
		Regra other = (Regra) obj;
		if (direita == null) {
			if (other.direita != null)
				return false;
		} else if (!direita.equals(other.direita))
			return false;
		if (tipo != other.tipo)
			return false;
		if (variavel == null) {
			if (other.variavel != null)
				return false;
		} else if (!variavel.equals(other.variavel))
			return false;
		return true;
	}
	
}
