package Ver2;
import java.util.*;
import preproess.*;
import preproess.Step.Action;
import work.Query1;
public class Combination implements Comparable<Combination>{
	
	public List<Position> pList;
	public List<Step> stepList;
	public int time; // ideal time
	public static Map<String, Double> map;
	public Combination() {
		super();
		pList = new ArrayList<>();
		stepList = new ArrayList<>();
		time = -1;
		
	}
	public Combination(Combination c) {
		super();
		pList = new ArrayList<>(c.pList);
		stepList = new ArrayList<>(c.stepList);
		time = c.time;
	}
	@Override
	public String toString() {
		String s = "";
		for(Step p : stepList) {
			s+="["+p.a+"-"+p.b+"]";
		}
		
		return s;
	}
	
	//¥¼§¹¦¨
	
	public int getTime(){
//		if(time > 0)return time;
		for(int i=0; i<pList.size()-1; i++) {
			Position a = pList.get(i);
			Position b = pList.get(i+1);
			if(a.equals(b) && a instanceof Station) {
				this.time -= stepList.get(stepList.size()-1).costTime;
				stepList.remove(stepList.size()-1);
				this.time += idealTime(pList.get(i-1), pList.get(i+2));
				i = i+1;
				continue;
			}
			this.time += idealTime(a, b);
		}
		
		return time;
	}
	// new step 
	public int idealTime(Position a, Position b) {
		//1.p2s 2.s2s 3.p2p 
		double out = 0;
		if(a instanceof Station) {
			if(b instanceof Station) {
				//s2s
				//idealTime map
				out = map.get(a.name+b.name);
				stepList.add(new Step(a, b, (int)out, Step.Action.bus));
				
				return (int)out;
			}
		}
		
		out = walkParameter(Math.hypot(a.x-b.x, a.y-b.y));
		stepList.add(new Step(a, b, (int)out, Step.Action.walk));
		
		if(b instanceof POI) {
			Step s = new Step(b, b,((POI) b).stayTime, Step.Action.POI);
			out += s.costTime;
			stepList.add(s);
		}
		
		return (int)out;
	}
	@Override
	public int compareTo(Combination arg0) {

		return this.time - arg0.time;
		
	}
	public int walkParameter(double d) {

		return (int)(d*Query1.disTotime);
		
	}
}
