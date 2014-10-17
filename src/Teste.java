import java.awt.Point;
import java.util.LinkedHashSet;

public class Teste {
	public static void main(String[] args) {
		LinkedHashSet<String> a = new LinkedHashSet<String>();
		a.add("asdf1");
		a.add("asdf2");
		a.add("asdf2");
		a.add("asdf3");
		LinkedHashSet<String> b =  new LinkedHashSet<String>();
		b.addAll(a);
		System.out.println(b);
	}
}
