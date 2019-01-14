package preproess;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class Edge extends Step implements Comparable<Edge>{
	public Line l;
	
	public Edge(Line l,Station a, Station b, int starTime, int costTime) {
		super();
		this.l = l;
		this.a = a;
		this.b = b;
		this.starTime = starTime;
		this.costTime = costTime;
	}
	@Override
	public String toString() {
		return "Edge ["+l.name+" "+a+ "-" +b+ "/"+mainClass.timeConverter(starTime)+"/"+costTime+"]";
	}
	public static boolean writeEdgeData(String file,List<Edge> elist) {
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			for(Edge e : elist) {
				writer.println(e.l.name+","+e.a.name+","+e.b.name+","+ mainClass.timeConverter(e.starTime)+","+e.costTime);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
	@Override
	public int compareTo(Edge obj) {
		
		return this.costTime - obj.costTime;
	}
}
