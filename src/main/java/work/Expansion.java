package work;
import java.util.ArrayList;
import java.util.List;
import preproess.Edge;
import preproess.Station;

public class Expansion implements Comparable<Expansion>{
	
	public Station s;
	public Station d;
	public int time;
	public List<Edge> edgeList;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	public List<Expansion> simExpandPath(){ //擴展未成熟路徑 (Station to Station), time = 當前時間
		//System.out.println("simExpandPath()");
		List<Expansion> out = new ArrayList<Expansion>();
		List<Edge> next = null;
		if(this.edgeList.isEmpty()) {
			//System.out.println("edgeList.isEmpty()");
			next = getavalibleTimeEdge(this.s.edgeList, time);
		}
		else {
			Edge lastEdge = this.edgeList.get(edgeList.size()-1);  //last Edge
			next = getavalibleTimeEdge(((Station)lastEdge.b).edgeList, time);
		}
		
		for(Edge tmp : next) {
			Expansion e = new Expansion(this);
			e.time += tmp.costTime + (tmp.starTime - time);
			e.edgeList.add(tmp);
			out.add(e);
		}
		return out;
	}
	//Pruning the next Edge with time limit
	public List<Edge> getavalibleTimeEdge(List<Edge> edgeList,int time){
		//System.out.println("getavalibleTimeEdge()");
		List<Edge> out = new ArrayList<Edge>();
		for(Edge e : edgeList ) {
			if(e.starTime >= time) {
				out.add(e);
			}
		}
		//out.forEach(t->System.out.println(">"+t));
		return out;
	}
	
	@Override
	public int compareTo( Expansion b) {
		if (this.time > b.time) 
            return 1;
        else if (this.time < b.time) 
            return -1;
        return 0;
	}
	public Expansion(Station s, Station d, int time, List<Edge> edgeList) {
		super();
		this.s = s;
		this.d = d;
		this.time = time;
		this.edgeList = new ArrayList<Edge>(edgeList);
	}
	public Expansion(Station s, Station d,int time) {
		super();
		this.s = s;
		this.d = d;
		this.time = time;
		this.edgeList = new ArrayList<Edge>();
	}
	@Override
	public String toString() {
		return "Expansion [s=" + s + ", d=" + d + ", time=" + time + ", edgeList=" + edgeList + "]";
	}
	public Expansion(Expansion e) {
		super();
		this.s = e.s;
		this.d = e.d;
		this.time = e.time;
		this.edgeList = new ArrayList<Edge>(e.edgeList);
	}
}
