import java.io.Serializable;

public class Estado implements Serializable{
	private static final long serialVersionUID = -7049169438096781668L;
	Regra regra;
	int i;
	int j;
	int ponto;
	boolean incompleto;
	String acao;

	public Estado(Regra regra, int ponto, int i, int j, String acao) {
		super();
		try {
			this.regra = (Regra)regra.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		this.i = i;
		this.j = j;
		this.ponto = ponto;
		if(this.ponto == regra.direita.size())
			this.incompleto = false;
		else
			this.incompleto = true;
		this.acao = acao;
	}
	
	public Elemento getElemento(){
		Elemento retorno = null;
		if(ponto < regra.direita.size())
			retorno = regra.direita.get(ponto);
		return retorno;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Estado((Regra) this.regra.clone(), this.ponto, this.i, this.j, this.acao);
	}
	
	@Override
	public String toString() {
		String retorno = "";
		if(regra!=null && regra.direita!=null)
			retorno = String.format("%-30s [%d][%d], Acao: %s, Ponto: %s, %s", regra,i,j,acao,ponto, incompleto?"Incompleto":"Completo"); 
		return retorno+"\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acao == null) ? 0 : acao.hashCode());
		result = prime * result + i;
		result = prime * result + (incompleto ? 1231 : 1237);
		result = prime * result + j;
		result = prime * result + ponto;
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
		if (acao == null) {
			if (other.acao != null)
				return false;
		} else if (!acao.equals(other.acao))
			return false;
		if (i != other.i)
			return false;
		if (incompleto != other.incompleto)
			return false;
		if (j != other.j)
			return false;
		if (ponto != other.ponto)
			return false;
		if (regra == null) {
			if (other.regra != null)
				return false;
		} else if (!regra.equals(other.regra))
			return false;
		return true;
	}
}