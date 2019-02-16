package Ver2;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import work.Query1;


public class Auto {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int[][] ls = new int[100][];
		for(int i=0; i<ls.length; i++) {
			int[] a = new int[3];
			for(int j=0; j<a.length; j++) {
				a[j] = -1;
			}
			do{
				Random r = new Random();
				int num = r.nextInt(250);
				//int num = ThreadLocalRandom.current().nextInt(0, 249 + 1);
				for(int j=0; j<a.length; j++) {
					if(num == a[j]) {
						continue;
					}
					else if(a[j] == -1){
						a[j] = num;
						break;
					}
				}
			}while(check(a));
			ls[i] = a;
		}
		PrintWriter writer = new PrintWriter("Logg.txt","utf-8");
		
		for(int i=0; i<ls.length; i++) {
			
			writer.println("---------------");
			List<Integer> list = Arrays.stream(ls[i]).boxed().collect(Collectors.toList());
			
			writer.println(list);
			writer.println(Query1.mainn(ls[i]));
			writer.println("---------------");
			writer.flush();
			
			
			
			System.out.println(i);
		}
		writer.close();	
		
	}
	public static boolean check(int[] a ) {
		for(int a1 :a) {
			if(a1 == -1)return true;
		}
		return false;
		
	}
}
