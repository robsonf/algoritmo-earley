import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Teste {
	public static void main(String[] args) {
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
		
		JTree tree = new JTree(s);
		JFrame j = new JFrame();
		JScrollPane scrollTree = new JScrollPane(tree);
		scrollTree.setViewportView(tree);
		j.add(scrollTree);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setTitle("Arvores Sintaticas");
        j.setSize(800,600);
        j.setVisible(true);
	}
}
