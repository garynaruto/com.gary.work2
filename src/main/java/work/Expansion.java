package work;
import java.util.ArrayList;
import java.util.List;
import preproess.Edge;
import preproess.Line;
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
			next = getavalibleTimeEdge(this.s.edgeList, this.time);
			//walk
			next.addAll(this.s.walkedgeList);
		}
		else {
			Edge lastEdge = this.edgeList.get(edgeList.size()-1);  //last Edge
			next = getavalibleTimeEdge(((Station)lastEdge.b).edgeList, time);
			//walk 
			next.addAll(((Station)lastEdge.b).walkedgeList);
			
		}
		//System.out.println("NEXT");
		//next.forEach(a->System.out.println(a));
		for(Edge tmp : next) {
			if(((Station)tmp.b).visit == true) continue;
			((Station)tmp.b).visit = true;
			Expansion e = new Expansion(this);
			if(tmp.l.name == "walk") {
				e.time += tmp.costTime;
			}
			else {
				e.time += tmp.costTime + (tmp.starTime - time);
			}
			
			e.edgeList.add(tmp);
			out.add(e);
		}
		//System.out.println("out");
		//out.forEach(a->System.out.println(a));
		return out;
	}
	//Pruning the next Edge with time limit
	public List<Edge> getavalibleTimeEdge(List<Edge> edgeList, int time){
		//System.out.println("getavalibleTimeEdge()");
		//System.out.println("this.e.edgelist "+edgeList);
		List<String> name = new ArrayList<>();
		List<Edge> out = new ArrayList<Edge>();
		for(Edge e : edgeList ) {
			if(e.starTime >= time && !name.contains(e.l.name) && e.starTime <=(time+60)) {// e.starTime <=(time+100)
				out.add(e);
				name.add(e.l.name);
			}
		}
		//out.forEach(t->System.out.println(">"+t));
		//System.exit(1);
		return out;
	}
	
	@Override
	public int compareTo(Expansion b) {
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
