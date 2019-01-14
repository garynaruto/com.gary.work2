package Ver2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableTest implements java.io.Serializable{
	public String name;
	public SerializableTest(String name) {
		this.name = name;
	}
	public String getname() {
		return "my name is "+this.name;
	}
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SerializableTest s = new SerializableTest("bb");
		
		try{
	        FileOutputStream fileOut = new FileOutputStream("src/main/resources/tmp/SerializableTest.ser");
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(s);
	        out.close();
	        fileOut.close();
         
	    }catch(Exception i){
	        i.printStackTrace();
	    }
			
		SerializableTest e = null;
	    try{
	    	FileInputStream fileIn = new FileInputStream("src/main/resources/tmp/SerializableTest.ser");
	    	ObjectInputStream in = new ObjectInputStream(fileIn);
	    	e = (SerializableTest) in.readObject();
	    	in.close();
	    	fileIn.close();
	    }catch(Exception i){
	    	i.printStackTrace();
	    }
		System.out.println(e.getname());
	}

}
