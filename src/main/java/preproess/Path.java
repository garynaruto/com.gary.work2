package preproess;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.github.davidmoten.rtree.geometry.Point;
import work.Query1;

public class Path implements Comparable<Path>{
	
	/* 初始query 起始點與終點與POIs final*/
	public static  Point sLoc;
	public static Point dLoc;
	public static Map<String, Double> map;
	
	public List<POI> target;  
	
	/* 起始點與終點與POIs Station  列舉階段後填入*/
	public Station s;
	public Station d;
	public ArrayList<Station> Stationlist; 
	public int nextStop = 0; // the index of next station , Expand station(nextStop, nextStop+1), 
	
	public List<POI> POIlist; //unvisit poi , POIlist.get(0) is next destination	
	public List<Edge> edgeList;
	public int time;
	public int distime;
	public int getTime() {
		int out = 0;
		out += Math.hypot(sLoc.x()- this.s.x,sLoc.y()- this.s.y);
		out += Math.hypot(dLoc.x()- this.d.x,dLoc.y()- this.d.y);
		distime = disToTime(out);
		out += map.get(s.name+Stationlist.get(0).name);
		for(int i=1; i<Stationlist.size()-1; i++) {
			out += map.get(Stationlist.get(i).name+Stationlist.get(i+1).name);
		}
		out += map.get(d.name+Stationlist.get(Stationlist.size()-1).name);
		
		for(int i=0; i<target.size(); i++) {
			out += (map.get(Stationlist.get(i).name+target.get(i).name))*2;
			out += target.get(i).stayTime;
		}
		
		this.time =  disToTime(out);
		return this.time;
	}
	public static int disToTime(int time) {
		return time/Query1.disTotime;
	}
	public Path(Station s, Station d, List<Edge> edgeList, List<POI> target, List<POI> pOIlist, ArrayList<Station> Stationlist ) {
		super();
		this.s = s;
		this.d = d;
		this.edgeList = edgeList;
		this.target = target;
		this.POIlist = pOIlist;
		this.Stationlist = Stationlist;
		time = -1;
	}
	public Path(Station s, Station d) {
		super();
		this.s = s;
		this.d = d;
		this.edgeList = new ArrayList<Edge>();
		this.target = new ArrayList<POI>();
		this.POIlist = new ArrayList<POI>();
		this.Stationlist = new ArrayList<Station>();
		time = -1;
	}
	
	public Path(Station s) {
		super();
		this.s = s;
		this.edgeList = new ArrayList<Edge>();
		this.target = new ArrayList<POI>();
		this.POIlist = new ArrayList<POI>();
		this.Stationlist = new ArrayList<Station>();
		time = -1;
	}
	
	/*
	public List<Path> simExpandPath(int time){ //擴展未成熟路徑 (Station to Station)
		//System.out.println("simExpandPath()");
		List<Path> out = new ArrayList<Path>();
		List<Edge> next = null;
		if(this.edgeList.isEmpty()) {
			//System.out.println("edgeList.isEmpty()");
			next = getavalibleTimeEdge(this.s.edgeList, time);
		}
		else {
			Edge e = this.edgeList.get(edgeList.size()-1);  //last Edge
			next = getavalibleTimeEdge(e.b.edgeList, time);
		}
		
		for(Edge tmp : next) {
			Path p = new Path(this.s,this.d);
			p.time = time + tmp.costTime + (tmp.starTime - time);
			p.edgeList.addAll(this.edgeList);
			p.edgeList.add(tmp);
			p.target.addAll(this.target);
			p.POIlist.addAll(this.POIlist);
			p.Stationlist.addAll(this.Stationlist);
			out.add(p);
			//if() meet poi sim(s2s) don't need to check
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
	public int compareTo(Path b) {
		if (this.time > b.time) 
            return 1;
        else if (this.time < b.time) 
            return -1;
        return 0;
	}*/
	
	@Override
	public int compareTo(Path b) {
		if (this.time > b.time) 
            return 1;
        else if (this.time < b.time) 
            return -1;
        return 0;
	}
	@Override
	public String toString() {
		return "Path [target=" + target + ", s=" + s + ", d=" + d
				+ ", Stationlist=" + Stationlist + ", nextStop=" + nextStop + ", POIlist=" + POIlist + ", edgeList="
				+ edgeList + ", time=" + time + "]";
	}
	public Path(Path p) {
		super();
		this.s = p.s;
		this.d = p.d;
		this.edgeList = new ArrayList<Edge>(p.edgeList);
		this.target = new ArrayList<POI>(p.target);
		this.POIlist = new ArrayList<POI>(p.POIlist);
		this.Stationlist = new ArrayList<Station>(p.Stationlist);
		this.nextStop = p.nextStop;
		this.time = p.time;
	}
	public Path(int i) {
		super();
		
		this.time = i;
	}
}
