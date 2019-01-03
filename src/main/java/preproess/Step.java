package preproess;

import java.util.List;
import java.util.PriorityQueue;

import work.Expansion;

public class Step {
	
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
		System.out.println("findrealPath " +src.a.name +"-"+ src.b.name);
		PriorityQueue<Expansion> queue = new PriorityQueue<Expansion>();
		
		queue.add(new Expansion((Station)src.a, (Station)src.b, src.starTime));
		while(!queue.isEmpty()) {
			Expansion e = queue.poll();
			System.out.println("poll :" +e.edgeList);
			if(!e.edgeList.isEmpty() && e.edgeList.get(e.edgeList.size()-1).b.equals(src.b)) {
				System.out.println("realPath :" +e.edgeList);
				ans = e.edgeList;
				return e.time;
			}
			List<Expansion> tmp = e.simExpandPath();
			queue.addAll(tmp);
			//tmp.forEach(t->System.out.println("->"+t));
		}
		ans = null;
		return 0;//no ans
	}
	public enum Action {
	    walk,
	    bus,
	    POI
	}
}