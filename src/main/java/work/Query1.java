package work;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;

import Ver2.*;

import preproess.*;

public class Query1 {
	//RTree<Position, Geometry> tree = RTree.star().minChildren(3).maxChildren(6).create();//RTree.asString()
	
	public static int min = 1;// minChildren
	public static int max = 6;// maxChildren
	public static int k = 4;  //k of KNN
	public static int disTotime = 12; //walk Parameter
	public static void main(String[] args) {
		
		/* read POI data & build Rtree */
		List<POI> POIList = mainClass.readPOIData("src/main/resources/p_loc.txt");
		RTree<Position, Geometry> ptree = RTree.star().minChildren(min).maxChildren(max).create();
		ptree = rtreeInsertPOI(ptree ,POIList);
		
		/* read station data & build Rtree */
		List<Station> stationList = mainClass.readStationData("src/main/resources/s_loc.txt");
		RTree<Position, Geometry> stree = RTree.star().minChildren(min).maxChildren(max).create();
		stree = rtreeInsertStation(stree ,stationList);
		
		/* read Line data build Edge  */
		Map<String, Line> map = mainClass.readbusData("src/main/resources/small_公車站牌資料.csv", stationList);
		List<Edge> elist = mainClass.readStarTimeData("src/main/resources/發車.csv",stationList,map);
		
		/* Calculate the distance of the station to POI or Station  */
		Map<String, Double> m =  mainClass.distance(stationList, POIList);
		//m.forEach((s,d) -> System.out.println(s+"> "+d));
		
		/*********  query start  ******* loc(5,3) - p1 - p2 - loc  */
		
		int queryTime = mainClass.timeConverter("09:00");
		Point start = Geometries.point(6,2);
		Point end = Geometries.point(6,2);
		List<POI> queryPOI = new ArrayList<POI>();
		POIList.get(0).stayTime = 20;
		POIList.get(1).stayTime = 20;
		queryPOI.add(POIList.get(0));
		queryPOI.add(POIList.get(1));
		Path.sLoc = start;
		Path.dLoc = end;
		Path.map = m;
		Combination.map = m;
		long startTime = System.currentTimeMillis();
		/* enumerate combination */
		List<Combination> combinations = eum(start, end, queryPOI ,stree);
		
		System.out.println("combinations : " + combinations.size() );
		//combinations.forEach(a->System.out.println(a));
//		System.out.println("start : "+start );
//		System.out.println("end : "+end );
//		System.out.println("queryPOI : "+queryPOI );
//		System.out.println("combinations : "+combinations );
//		System.out.println("combinations : "+combinations.size() );
//		System.out.println("queryTime : "+queryTime );
		
		/* calculate combination Score */
		Queue<Combination> queue = calScore(combinations);
		
		//queue.forEach(a->{System.out.println(a);});
		
		for(int i=0; i<100; i++) {
			System.out.println(queue.poll());
		}
		
		
		/* find real-path with pruning */
		System.out.println("find real-path :");
		Path2 ans = new Path2(Integer.MAX_VALUE);
		int i=0;
		while(!queue.isEmpty()) {
			//System.out.println(i);
			Combination tmp = queue.poll();
			//System.out.println("queue.poll"+tmp);
			//System.out.println(" tmp :"+ tmp);
			if(queryTime + tmp.time > ans.time) {
				//System.out.println("break : "+ queryTime+"/"+ tmp.time+"/"+ans.time);
				System.out.println("break : "+ i);
				break;
			}
			//System.out.println(" open :");
			Path2 p = openCom2(tmp, elist, stationList, queryTime, ans.time);
			if(p != null && p.time < ans.time) {
				ans = p;
				System.out.println("ans update ["+i+"] :"+ ans);
				
			}
			i++;
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("ans>"+ans);
		System.out.println("CPU Time : "+ (endTime-startTime));
		System.out.println("done");
	}
	public static Path2 openCom2(Combination com, List<Edge> elist, List<Station> stationList, int queryTime, int bestTime) {
		Path2 out = new Path2();
		List<Edge> realPath = null;
		System.out.println("openCom2 : "+com);
		out.time = queryTime;
		for(Step s :com.stepList) {
			if(s.way == Step.Action.bus) {
				System.out.println("Step.Action.bus");
				s.starTime = out.time;
				out.time += Step.findrealPath(realPath, s);
				if(realPath == null) {
					return null;
				}
				s.Edges = new ArrayList<Edge>(realPath);
				out.stepList.add(s);
				
			}
			else {
				if(s.way == Step.Action.POI) {
					System.out.println("Step.Action.POI");
					//check wait or not or can't 
					s.starTime = out.time;
					POI target = (POI)s.a;
					if (s.starTime+target.stayTime > target.endTime ){
						return null;
					}
					if (s.starTime < target.starTime) {//wait POI open
						s.waitTime = target.starTime - s.starTime;
						out.time += s.waitTime;
					}
					out.time += target.stayTime;
					out.stepList.add(s);
					
				}else if(s.way == Step.Action.walk) {
					System.out.println("Step.Action.walk");
					s.starTime = out.time;
					out.time += s.costTime;
					out.stepList.add(s);
				}
			}
		}
		return out;//min time real-path in this combination
	}
	
	//展開組合成為一條real-path       p to Combination
	public static Path openCom(Path p, List<Edge> elist,List<Station> stationList, int queryTime, int bestTime) {
		
		/* 起點  to POI1*/
		Expansion e = Dijkstra.ExpandStation(elist,stationList,p.s,p.Stationlist.get(0),p.POIlist.get(0), queryTime);
		p.edgeList.addAll(e.edgeList);
		p.time = e.time;
		
		for(int i=0;i<p.Stationlist.size()-1; i++) {
			e = Dijkstra.ExpandStation(elist,stationList,p.Stationlist.get(i), p.Stationlist.get(i+1),p.POIlist.get(i+1), p.time);
			p.time = e.time;
			p.edgeList.addAll(e.edgeList);
			//if(p.time + p.distime > bestTime) {
			//	return null;
			//}
		}
		
		/* 終點  to POIn */
		e = Dijkstra.ExpandStation(elist,stationList,p.Stationlist.get(p.Stationlist.size()-1),p.d, null,p.time);
		p.time = p.distime + e.time;
		p.edgeList.addAll(e.edgeList);
		
		return p;//min time real-path in this combination
	}
	
	private static Queue<Combination> calScore(List<Combination> combinations) {
		Queue<Combination> out = new LinkedList<>();
		PriorityQueue<Combination> queue = new PriorityQueue<>();
		combinations.forEach(tmp->tmp.getTime());
		queue.addAll(combinations);
		
		while(!queue.isEmpty()) {
			out.add(queue.poll());
		}
		return out;
	}
	
	public static List<Combination> eum(Point start, Point end, List<POI> queryPOI , RTree<Position, Geometry> stree) {
		
		Position startPosition  = new Position(start,"start");
		Position endPosition  = new Position(end,"end");
		
		/* KNN for all position */
		List<Station> startStation = KNN.KNNQuery2(stree, start, k);
		List<Station> endStation = KNN.KNNQuery2(stree, end, k);
		queryPOI.forEach(p->{
			p.NNstation = KNN.KNNQuery2(stree, Geometries.point(p.x, p.y), k);
		});
		
		
		/*new path*/
		//add start Station
		List<Combination> comList = new ArrayList<Combination>();
		for(int i=0; i<startStation.size();i++) {
			Combination c = new Combination();
			c.pList.add(startPosition);
			c.pList.add(startStation.get(i));
			comList.add(c);
		}
		
		/*add queryPOI Station*/
		List<List<POI>> POIsPermutation = getPOIPermutation(queryPOI);/* ex: input [p1,p2] return [[p1,p2], [p2,p1]] */
		List<Combination> newComList = new ArrayList<>();  //storage new Combination
		
		for(POI p : queryPOI) {
			List<List<Station>> Stationlist = new ArrayList<List<Station>>();
			List<List<Station>> ans = new ArrayList<List<Station>>();
			Stationlist.add(p.NNstation);
			List<Station> s = new ArrayList<Station>(p.NNstation);
			s.add(null);
			Stationlist.add(s);
			mPermutation(ans, Stationlist, p.NNstation ,new ArrayList<Station>());
			List<List<Position>> POIiStation = new ArrayList<List<Position>>(); 
			for(List<Station> tmp :ans) {
				List<Position> tmp1 = new ArrayList<Position>(); 
				tmp1.add(tmp.get(0));
				tmp1.add(p);
				tmp1.add(tmp.get(1));
				POIiStation.add(tmp1);
			}
			p.POIiStation = POIiStation;
			//System.out.println(POIiStation);
		}		
		//foreach start Station add foreach POIs Permutation
		for(Combination paths : comList) {
			for(List<POI> Permutation : POIsPermutation) {
				List<List<List<Position>>> ans = new ArrayList<List<List<Position>>>();
				List<List<List<Position>>> slist = new ArrayList<List<List<Position>>>();
				for(POI p : Permutation) {
					slist.add(p.POIiStation);
				}
				//System.out.println(slist);
				nPermutation(ans, slist, Permutation.get(0).POIiStation,new ArrayList<List<Position>>());
				for(List<List<Position>> a : ans){
					Combination tmp = new Combination (paths);
					/* c is <s,p,s> for a Permutation  */
					for(List<Position> c : a) {
						tmp.pList.addAll(c);
					}
					newComList.add(tmp);
				}
			}
		}
		
		/*add ends Stations*/
		comList = newComList;
		newComList = new ArrayList<Combination>();
		for(Combination  a : comList){
			for(Station ends : endStation) {
				Combination tmp = new Combination(a);//new path without copy attribute
				tmp.pList.add(ends);
				tmp.pList.add(endPosition);
				newComList.add(tmp);
			}
		}
		comList = null;
		return newComList;
	}
	
	public static int p(int num) {
		int i=num;
		while(i!=1) {
			i *= (i-1);
			i--;
		}
		return  i;
	}
	/* ex: input [p1,p2] return [[p1,p2], [p2,p1]] */
	public static List<List<POI>> getPOIPermutation(List<POI> POIslist) {
		List<List<POI>> out = new ArrayList<List<POI>>();
		for(List<POI> t : Permutation.perm(POIslist)) {
			//System.out.println(">"+t);
			out.add(t);
        }
		
		return out;
	}
	public static void nPermutation(List<List<List<Position>>> ans, List<List<List<Position>>> list, List<List<Position>> arr, List<List<Position>> str) {
		for (int i = 0; i < list.size(); i++) {
			if (i == list.indexOf(arr)) {
				for (List<Position> st : arr) {
					List<List<Position>> str2 = new ArrayList<List<Position>>(str);
					str2.add(st);
					if (i < list.size() - 1) {
						nPermutation(ans, list, list.get(i + 1), str2);
					} else if (i == list.size() - 1) {
						//System.out.println(str2);
						if(!str2.contains(null)) {
							ans.add(str2);
						}
					}
				}
			}
		}
	}
	public static void mPermutation(List<List<Station>> ans, List<List<Station>> list, List<Station> arr, List<Station> str) {
		for (int i = 0; i < list.size(); i++) {
			if (i == list.indexOf(arr)) {
				for (Station st : arr) {
					List<Station> str2 = new ArrayList<Station>(str);
					str2.add(st);
					if (i < list.size() - 1) {
						mPermutation(ans, list, list.get(i + 1), str2);
					} else if (i == list.size() - 1) {
						//System.out.println(str2);
						if(!str2.contains(null)) {
							ans.add(str2);
						}
					}
				}
			}
		}
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
