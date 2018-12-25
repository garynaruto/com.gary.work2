package preproess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class text {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String csvFile = "C:\\Users\\ADS\\Desktop\\paper\\work\\data\\1.臺中市市區公車站牌資料0.csv";
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String> l = new  ArrayList<String>();
        double lon = 0;
        double lat = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                String s = country[0]+country[1]+country[2];
                if(!l.contains(s)) {
                	l.add(s);
                	lon = Double.parseDouble(country[4]);
                	lat = Double.parseDouble(country[5]);
                	System.out.println(s + "," + country[3]+ "," +0);
                }else {
                	
                	System.out.println(s + "," + country[3]+ "," +0);

                }
                

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
