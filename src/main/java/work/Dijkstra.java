package work;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import preproess.Edge;
import preproess.Line;
import preproess.POI;
import preproess.Path;
import preproess.Position;
import preproess.Station;
import preproess.mainClass;

public class Dijkstra {
	//RTree<Position, Geometry> tree = RTree.star().minChildren(3).maxChildren(6).create();
	//tree.visualize(500, 500).save("src/main/resources/rtree.png");
	public static void main(String[] args) {
		
		/* read POI data & build Rtree */
		List<POI> POIList = mainClass.readPOIData("src/main/resources/p_loc.txt");
		RTree<Position, Geometry> ptree = RTree.star().minChildren(1).maxChildren(6).create();
		ptree = rtreeInsertPOI(ptree ,POIList);
		
		/* read station data & build Rtree */
		List<Station> stationList = mainClass.readStationData("src/main/resources/s_loc.txt");
		RTree<Position, Geometry> stree = RTree.star().minChildren(1).maxChildren(6).create();
		stree = rtreeInsertStation(stree ,stationList);
		//tree.visualize(500, 500).save("src/main/resources/rtree.png");
		
		/* read Line data build Edge  */
		Map<String, Line> map = mainClass.readbusData("src/main/resources/small_公車站牌資料.csv", stationList);
		List<Edge> elist = mainClass.readStarTimeData("src/main/resources/發車.csv",stationList,map);
		
		Station s1 = stationList.get(3);
		Station s2 = stationList.get(0);
		
		//Path p = simDijkstraAlg(elist,stationList,s1,s2, 0);
		//System.out.println(">> "+p);
		//Expansion e = ExpansionDijkstraAlg(elist,stationList,s1,s2, 0);
		//System.out.println(">> "+e);
		/*
		for(Station s : stationList) {
			System.out.println(s.name);
			s.edgeList.forEach(e->System.out.println(e));
		}
		for(Edge tmp : elist) {
			System.out.println(tmp.l.name);
			tmp.l.SList.forEach(t->System.out.println(t));
		}*/
		
		System.out.println("done");
	}
	// Expand Path to find the shortest path from s1 to s2 without check POI or target
	public static Expansion ExpandStation(List<Edge> elist, List<Station> stationList,Station s,Station d,POI target, int time) {
		
//		if(s.equals(d)) {
//			Expansion e = new Expansion(s, d, time);
//			if(target != null) {
//				if(e.time+target.stayTime < target.endTime) {
//					e.time += (Path.map.get(d.name+target.name))*2;
//					if(e.time > target.starTime) {
//						e.time += target.stayTime;
//					}
//					else {
//						e.time += target.stayTime + (target.starTime-e.time);
//					}
//					return e;
//				}
//			}
//			return e;
//		}
		
		PriorityQueue<Expansion> queue = new PriorityQueue<Expansion>();
		queue.add(new Expansion(s, d, time));
		while(!queue.isEmpty()) {
			Expansion e = queue.poll();
			if(!e.edgeList.isEmpty() && e.edgeList.get(e.edgeList.size()-1).b.equals(d)) {
				//check POI time : e.time in poi.rang
				if(target != null) {
					if(e.time+target.stayTime < target.endTime) {
						e.time += (Path.map.get(d.name+target.name))*2;
						if(e.time > target.starTime) {
							e.time += target.stayTime;
						}
						else {
							e.time += target.stayTime + (target.starTime-e.time);
						}
						return e;
					}
				}
				else {//target == null 
					return e;
				}
			}
			else {
				List<Expansion> tmp = e.simExpandPath();
				queue.addAll(tmp);
				//tmp.forEach(t->System.out.println("->"+t));
			}
		}
		return null;//no ans
	}
	public static RTree<Position, Geometry> rtreeInsertPOI(RTree<Position, Geometry> tree ,List<POI> POIList ) {
		for(POI p : POIList) {
			tree = tree.add(p, Geometries.point(p.x,p.y));
		}
		return tree;
	}
	public static RTree<Position, Geometry> rtreeInsertStation(RTree<Position, Geometry> tree ,List<Station> sList) {
		for(Station s : sList) {
			tree = tree.add(s, Geometries.point(s.x,s.y));
		}
		return tree;
	}
	
}
