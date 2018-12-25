package Ver2;

import java.util.*;
import preproess.*;
public class Combination {
	
	public Station s;
	public Station d;
	public List<Position> pList;
	public List<Edge> edgeList;
	public int time; // ideal time
	
	public Combination(Station s) {
		super();
		this.s = s;
		pList = new ArrayList<>();
		edgeList = new ArrayList<>();
		time = -1;
		pList.add(s);
		
	}
	public Combination(Combination c) {
		super();
		this.s = c.s;
		this.d = c.d;
		pList = new ArrayList<>(c.pList);
		edgeList = new ArrayList<>(c.edgeList);
		time = c.time;
	}
	@Override
	public String toString() {
		String s = "";
		for(Position p : pList) {
			s+="->"+p;
		}
		
		return s;
	}
	
	//¥¼§¹¦¨
	
	public int getTime(){
		if(time > 0) {
			return time;
		}
		
		
		
		
		
		return time;
	}
}
