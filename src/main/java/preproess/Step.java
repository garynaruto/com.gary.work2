package preproess;
import java.util.List;
import java.util.PriorityQueue;

import work.Expansion;

public class Step {
	public static List<Station> stationList;
	public Position a;
	public Position b;
	public int starTime;
	public int costTime;
	public int waitTime;
	public List<Edge> Edges;
	public Action way;
	public Step() {
		super();
	}
	public Step(Position a, Position b, int costTime, Action way) {
		super();
		this.a = a;
		this.b = b;
		this.starTime = -1;
		this.costTime = costTime; 
		//this.waitTime = -1;
		this.way = way;
	}
	
	@Override
	public String toString() {
		if(this.way == Action.POI) {
			return "[" + a + "(" + costTime + ")]";
		}
		return "[" + a + "-" + b +"]";
	}
	public static int findrealPath(List<Edge> ans, Step src){
		stationList.forEach(a->a.visit=false);
		//System.out.println("findrealPath [" +src.a.name +"-"+ src.b.name+"]");
		PriorityQueue<Expansion> queue = new PriorityQueue<Expansion>();
		
		queue.add(new Expansion((Station)src.a, (Station)src.b, src.starTime));
		//int i=0;
		while(!queue.isEmpty()) {
//			
			Expansion e = queue.poll();
			//System.out.println("poll :" +e.edgeList);
			if(!e.edgeList.isEmpty() && e.edgeList.get(e.edgeList.size()-1).b.name.equals(src.b.name)) {
				//System.out.println("realPath :" +e.edgeList);
				ans.addAll(e.edgeList);
				return e.time;
			}
			List<Expansion> tmp = e.simExpandPath();
			//System.out.println("e :" +e);
			//System.out.println("ADD :" +tmp);
			queue.addAll(tmp);
			//tmp.forEach(t->System.out.println("->"+t));
		}
		//System.out.println("No realPath");
		ans = null;
		return -1;//no ans
	}
	public enum Action {
	    walk,
	    bus,
	    POI
	}
}