import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class Teste {
	class MyComparator implements Comparator<DefaultMutableTreeNode>{

	    @Override
	    public int compare(DefaultMutableTreeNode s1, DefaultMutableTreeNode s2) {
	    return s1.getLeafCount() - s2.getLeafCount();
	    }
	    
	}
	public static void main(String[] args) {
		Teste t = new Teste();
		MyComparator mc = t.new MyComparator();
        
		List<DefaultMutableTreeNode> lista = new ArrayList<DefaultMutableTreeNode>();

		DefaultMutableTreeNode s = new DefaultMutableTreeNode("S");
		DefaultMutableTreeNode vp = new DefaultMutableTreeNode("VP");
		DefaultMutableTreeNode np = new DefaultMutableTreeNode("NP");
		DefaultMutableTreeNode verb = new DefaultMutableTreeNode("Verb");
		DefaultMutableTreeNode book = new DefaultMutableTreeNode("book");
		DefaultMutableTreeNode det = new DefaultMutableTreeNode("Det");
		DefaultMutableTreeNode that = new DefaultMutableTreeNode("that");
		DefaultMutableTreeNode nominal = new DefaultMutableTreeNode("Nominal");
		DefaultMutableTreeNode noun = new DefaultMutableTreeNode("Noun");
		DefaultMutableTreeNode flight = new DefaultMutableTreeNode("flight");
		
		s.add(vp);
		vp.add(verb);
		vp.add(np);
		verb.add(book);
		np.add(det);
		det.add(that);
		np.add(nominal);
		nominal.add(noun);
		noun.add(flight);
		noun.add(book);
		noun.add(that);

		System.out.println(s.getLeafCount());
		lista.add(s);
		
		DefaultMutableTreeNode s2 = new DefaultMutableTreeNode("S2");
		DefaultMutableTreeNode a = new DefaultMutableTreeNode("flight");
		
		s2.add(a);

		System.out.println(s2.getLeafCount());
		lista.add(s2);
		
		java.util.Collections.sort(lista, mc);
		System.out.println(lista);
		
		ManipulaCorpus m = new ManipulaCorpus();
		m.imprimirArvore(s);
		
	}
}
