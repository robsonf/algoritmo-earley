import java.awt.Point;

public class Teste {
	public static void main(String[] args) {
		Regra r1 = new Regra(Regra.LEXICO, "A", "asdf");
		Regra r2 = new Regra(Regra.LEXICO, "B", "asdf");
		Estado e1 = new Estado(r1, 0, 0, 0, "Completer");
		e1.backPointers.add(new Point(1,1));
		e1.backPointers.add(new Point(1,1));
		Estado e2 = null;
		try {
			e2 = new Estado((Regra)r1.clone(), 0, 0, 0, "Completer");
			e2.backPointers.add(new Point(0,0));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		System.out.println(e1);
	}
}
