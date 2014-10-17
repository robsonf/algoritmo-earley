import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Estado implements Serializable{
	private static final long serialVersionUID = -7049169438096781668L;
	Regra regra;
	int i;
	int j;
	int ponto;
	boolean incompleto;
	String acao;
	ArrayList<LinkedHashSet<Point>> backPointers;

	public Estado(Regra regra, int ponto, int i, int j, String acao, ArrayList<LinkedHashSet<Point>> backPointer) {
		this(regra, ponto, i, j, acao);
		this.backPointers = backPointer;
	}
	
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
		ArrayList<LinkedHashSet<Point>> lista = new ArrayList<LinkedHashSet<Point>>();
		lista.add(new LinkedHashSet<Point>());
		this.backPointers = lista;
	}
	
	public Elemento getElemento(){
		Elemento retorno = null;
		if(ponto < regra.direita.size())
			retorno = regra.direita.get(ponto);
		return retorno;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ArrayList<LinkedHashSet<Point>> listaClonada = new ArrayList<LinkedHashSet<Point>>();
		for (LinkedHashSet<Point> backPointer : backPointers) {
			LinkedHashSet<Point> lista = new LinkedHashSet<Point>();
			for (Point point : backPointer) {
				lista.add(new Point(point.x, point.y));	
			}
			listaClonada.add(lista);
		}
		return new Estado((Regra) this.regra.clone(), this.ponto, this.i, this.j, this.acao, listaClonada);
	}
	
	@Override
	public String toString() {
		String retorno = "";
		if(regra!=null && regra.direita!=null){
			ArrayList<LinkedHashSet<String>> lista = new ArrayList<LinkedHashSet<String>>();
			for (LinkedHashSet<Point> backPointer : backPointers) {
				LinkedHashSet<String> tracker = new LinkedHashSet<String>();
				for (Point point : backPointer) {
					tracker.add(String.format("[%d,%d] ", point.x, point.y));
				}
				lista.add(tracker);
			}
			retorno = String.format("%-30s [%d][%d], Acao: %s, Ponto: %s, %s, BackPointer: %s", regra,i,j,acao,ponto, incompleto?"Incompleto":"Completo", lista);
		}
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