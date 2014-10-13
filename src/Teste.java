import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Teste implements Serializable{
	TesteLista t;

	public Teste(ArrayList a) {
		super();
		this.t = new TesteLista(a);
	}

	public static void main(String[] args) {
		try {
			String nomeArquivo = "teste";
			File arquivo = new File(nomeArquivo+".dat");
			if(arquivo.exists()){
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo));
				Teste objetoSerializado = (Teste) ois.readObject();
				System.out.println(objetoSerializado.t.a.toString());
				ois.close();
			}else{
				ArrayList<String> a = new ArrayList<String>();
				a.add("asdf");
				Teste manipula = new Teste(a);
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo));
				oos.writeObject(manipula);
				oos.close();		
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
