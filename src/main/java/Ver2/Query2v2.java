package Ver2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import preproess.*;
import work.*;

public class Query2v2 {
	//RTree<Position, Geometry> tree = RTree.star().minChildren(3).maxChildren(6).create();//RTree.asString()
	//	String POIfile = "src/main/resources/p_loc.txt";
	//	String stationfile = "src/main/resources/s_loc.txt";
	//	String stopfile = "src/main/resources/small_公車站牌資料.csv";
	//	String lineTimeFile = "src/main/resources/發車.csv";
	public static List<Station> startNNStation;
	//public static List<Station> endNNStation;
	public static void main(String[] args) {
		int min = 1;// minChildren
		int max = 6;// maxChildren
		int k = 3;  //k of KNN
		double disTotime = 0.377; //walk Parameter
		Combination.disTotime = disTotime;
		
		String POIfile = "src/main/resources/realData/sample 250.csv";
		String stationfile = "src/main/resources/realData/STATION.csv";
		String stopfile = "src/main/resources/realData/路線UTF-8.csv";
		String lineTimeFile = "src/main/resources/realData/路線 time.csv";
		
		
		/* read POI data & build Rtree */
		//System.out.println("read POI data");
		List<POI> POIList = mainClass.readPOIData(POIfile);
		RTree<Position, Geometry> ptree = RTree.star().minChildren(min).maxChildren(max).create();
		ptree = rtreeInsertPOI(ptree ,POIList);
		//System.out.println("read Station data");
		/* read station data & build Rtree */
		List<Station> stationList = mainClass.readStationData(stationfile);
		RTree<Position, Geometry> stree = RTree.star().minChildren(min).maxChildren(max).create();
		stree = rtreeInsertStation(stree ,stationList);
		Step.stationList = stationList;
		
		//System.out.println("build Edge");
		/* read Line data build Edge  */
		Map<String, Line> map = mainClass.readbusData(stopfile, stationList);
		List<Edge> elist = mainClass.readStarTimeData( lineTimeFile,stationList,map);
		
/*undone 	Calculate the distance of the station to POI or Station  */
		Map<String, Double> m =  mainClass.distance(stationList, POIList);

		/********  query data  ***********/
		//int star1 = (int) (Math.random() * POIList.size());
		//int star2 = (int) (Math.random() * POIList.size());
		//POI p1 = POIList.get(star1);
		//POI p2 = POIList.get(star2);
	    POI p1 = POIList.get(237);
	  	POI p2 = POIList.get(237);
		Point start = Geometries.point(p1.x,p1.y);
		Point end = Geometries.point(p2.x,p2.y);
		System.out.println("start"+p1.name);
		System.out.println("end"+p2.name);
		
		int queryTime = mainClass.timeConverter("09:00");

		//int randomPOI3 = 133;
		//POI(193),(193, 233),(230, 199, 118),(230, 199, 118,233),(230,199,118,233,193)
		int[] POIIndex = {230, 199, 118,233};
		List<POI> queryPOI = new ArrayList<POI>();
		
		for(int i=0; i<POIIndex.length; i++) {
			System.out.println("p"+(i+1)+POIList.get(POIIndex[i]).name);
			POIList.get(POIIndex[i]).stayTime = 20;
			queryPOI.add(POIList.get(POIIndex[i]));
		}

		Combination.map = m;
		
		
		
//-----------------k 1-6 ---------------------------		
//		String[] value =  new String[5];
//		for(int i=0; i<5;i++ ) {
//			System.out.println("K="+(i+2));
//			value[i] = query(start,end,queryPOI,stree,i+2,queryTime,elist, stationList);
//		}
//		for(int i=0; i<value.length;i++ ) {
//			System.out.println("---------------");
//			System.out.println(value[i]);
//		}
//---------------------------------------------------
		
		System.out.println(query(start,end,queryPOI,stree,k,queryTime,elist, stationList));
	}
	public static Map<String, List<List<Position>>> Smap;
	public static String query(Point start,Point end,List<POI> queryPOI,RTree<Position, Geometry> stree,int k,
			int queryTime,List<Edge> elist,List<Station> stationList) {
		
		
		/********** query start **************/
		System.out.println("Start");
		long startTime = System.currentTimeMillis();
		long endTime;
		/* enumerate combination */
		System.out.println("eum");
		List<List<Position>> com = eumPosition(start, end, queryPOI);
		
		//com.forEach(a->{a.forEach(s->System.out.print(s+" "));System.out.println();});
		
		
		//List<Combination> combinations = eum(start, end, queryPOI ,stree, k);
		System.out.println("size : "+ com.size());
		/*   times filter    */
		
		System.out.println("tree build");
		TreeNode2v root = buildTree2(com);
		root.traversal(" ");
		/* calculate combination Score */
		
		//root.traversal("");
		/*  find path  */
		System.out.println("find path");
		Path2 ans = findPath(start,end,root, queryTime,queryPOI,stree, k);
		
		
		endTime = System.currentTimeMillis();
		System.out.println("ans>"+ans);
		System.out.println("CPU Time : "+ (endTime-startTime));
		System.out.println("done");
		return "k="+k+"\n"+"ans>"+ans +"\n"+"CPU Time : "+ (endTime-startTime);
		//return "done";
	}
	public static TreeNode2v buildTree2(List<List<Position>> combinations) {
		
		System.out.println("buildTree");
		TreeNode2v root = new TreeNode2v(combinations.get(0).get(0));
		for(int i=0; i<combinations.size(); i++) {
			root.insert(combinations.get(i));
		}
		//root.traversal("");

		
		return root;
		
	}
	public static void stepTable(TreeNode2v node) {
		for(int i=0; i<node.Nexts.size(); i++) {
			Position p = node.Nexts.get(i);
			if( p instanceof POI) {
				POI poi = (POI)p;
				
				
				node.StepTable.get(i).addAll(null);
			}
		}
		node.child.forEach(a->stepTable(a));
	}
	
	public static List<List<Position>> eumPosition(Point start, Point end, List<POI> queryPOI){
		List<List<Position>> out = new ArrayList<List<Position>>();
		Position startPosition  = new Position(start,"start");
		Position endPosition  = new Position(end,"end");
		
		List<Position> tmp = new ArrayList<Position>(queryPOI);
		
		out = Permutation.perm(tmp);
		out.forEach(a->{a.add(0,startPosition);a.add(endPosition);});
		
		
		return out;
	}
	public static List<Station> startStation;
	public static List<Station> endStation;
	public static Path2 findPath(Point start,Point end,TreeNode2v root, int time, List<POI> queryPOI,RTree<Position, Geometry> stree, int k) {
		Path2 out = new Path2();
		out.time = time;
		TreeNode2v node = root;
		
		/* KNN for all position */
		startStation = KNN.KNNQuery2(stree,start, k);
		endStation = KNN.KNNQuery2(stree, end, k);
		queryPOI.forEach(p->{
			p.NNstation = KNN.KNNQuery2(stree, Geometries.point(p.x, p.y), k);
		});
		
		
		
		while(!node.p.name.equals("end")){
			//System.out.println("!equals end");
			int index = chosePOI(node, time);
			
			time = choseStep(node.StepTable.get(index),out);
			node = node.child.get(index);
			//out.stepList.forEach(a->System.out.println("<"+a+">"));
			//break;
		}
		
		return out;
	}
	public static int chosePOI(TreeNode2v node, int time) {
		List<Position> nexts = node.Nexts;
		List<List<List<Position>>> table = node.StepTable;
		if(nexts.get(0) instanceof POI) {
			
			for(int i=0; i<node.Nexts.size(); i++) {
				node.StepTable.addAll(setTable(node.p, node.Nexts.get(i)));
			}
			
			int index = -1;
			//List<POI> result = nexts.stream().map(a->(POI)a).collect(Collectors.toList());
			int minTime = Integer.MAX_VALUE;
			
			for(int i=0; i<table.size(); i++) {
				List<List<Position>> POIiWayTable = table.get(i);
				for(int j=0; j<POIiWayTable.size(); j++) {
					List<Position> pair = POIiWayTable.get(j);
					int tmptime = Combination.idealTimes(pair.get(1),pair.get(2));
					if(minTime > tmptime) {
						minTime = tmptime;
						index = i;
					}
				}
			}
			
			return index;
		}
		else {
			// end position
			return 0;
		}
	}
	public static int choseStep(List<List<Position>> table, Path2 out) {
		List<Step> ans = null;
		int ansTime = Integer.MAX_VALUE;

		System.out.println("table.size() : "+table.size());
		for(int i=0; i<table.size();i++) {
			
			List<Position> pair = table.get(i);
			int cost = out.time;
			List<Step> tmp = new ArrayList<Step>();
			for(int j=0; j<pair.size()-1;j++) {
				Position a = pair.get(j);
				Position b = pair.get(j+1);
				if(a instanceof Station && b instanceof Station) {
					//open station a2b
					Step s = new Step(a, b, 0, Step.Action.bus);
					s.starTime = out.time;
					List<Edge> realPath = new ArrayList<Edge>();
					cost += Step.findrealPath(realPath, s);
					if(realPath.isEmpty()) {
						//System.out.println(s+" realPath == null");
						continue;
					}
					s.Edges = new ArrayList<Edge>(realPath);
					tmp.addAll(realPath);
				}
				else {
					int t = Combination.walkParameter((Double)Math.hypot(a.x-b.x, a.y-b.y));
					cost += t;
					tmp.add(new Step(a, b, t, Step.Action.walk));
					
					if(b instanceof POI) {
						Step s = new Step(b, b,((POI) b).stayTime, Step.Action.POI);
						cost += s.costTime;
						tmp.add(s);
					}
				}
			}
			if(cost < ansTime) {
				ansTime = cost;
				ans = tmp;
			}
		}
		out.stepList.addAll(ans);
		return ansTime;
	}
	public static List<Combination> eum(Point start, Point end, List<POI> queryPOI , RTree<Position, Geometry> stree, int k) {
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
