package Ver2;
import java.util.*;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import preproess.*;
import work.*;

public class Query1ver2 {
	//RTree<Position, Geometry> tree = RTree.star().minChildren(3).maxChildren(6).create();//RTree.asString()
	
		public static int min = 1;// minChildren
		public static int max = 6;// maxChildren
		public static int k = 4;  //k of KNN
		public static int disTotime = 1;
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
			
			long startTime = System.currentTimeMillis();
			/* enumerate combination */
			List<Path> combinations = eum(start, end, queryPOI ,stree);
			
			/* calculate combination Score */
			Queue<Path> queue = calScore(combinations, queryTime);
			System.out.println("queryTime : "+queryTime );
			//queue.forEach(a->System.out.println( queryTime +a.time));
			
			/* find real-path with pruning */
			//System.out.println("find real-path :");
			Path ans = new Path(Integer.MAX_VALUE);
			int i=0;
			while(!queue.isEmpty()) {
				//System.out.println(i);
				Path tmp = queue.poll();
				//System.out.println(" tmp :"+ tmp);
				if(queryTime + tmp.time > ans.time) {
					System.out.println("break : "+ queryTime+"/"+ tmp.time+"/"+ans.time);
					System.out.println("break : "+ i);
					break;
				}
				//System.out.println(" open :");
				tmp = openCom(tmp, elist,stationList,queryTime, ans.time);
				if(tmp != null && tmp.time < ans.time) {
					ans = tmp;
				}
				i++;
			}
			
			long endTime = System.currentTimeMillis();
			System.out.println("ans>"+ans);
			System.out.println("Time : "+ (endTime-startTime));
			System.out.println("done"); 
		}
		//展開組合成為一條real-path
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
		
		private static Queue<Path> calScore(List<Path> combinations, int queryTime ) {
			Queue<Path> out = new LinkedList<Path>();
			PriorityQueue<Path> queue = new PriorityQueue<Path>();
			combinations.forEach(tmp->tmp.getTime());
			queue.addAll(combinations);
			
			while(!queue.isEmpty()) {
				out.add(queue.poll());
			}
			return out;
		}
		
		public static List<Path> eum(Point start, Point end, List<POI> queryPOI , RTree<Position, Geometry> stree) {
			List<Station> startStation = KNN.KNNQuery2(stree, start, k);
			List<Station> endStation = KNN.KNNQuery2(stree, start, k);
			queryPOI.forEach(p->{
				p.NNstation = KNN.KNNQuery2(stree, Geometries.point(p.x, p.y), k);
			});
			
			List<Path> pathList = new ArrayList<Path>();
			for(int i=0; i<startStation.size();i++) {
				pathList.add(new Path(startStation.get(i)));
			}
			
			List<List<POI>> POIsPermutation = getPOIPermutation(queryPOI);
			//System.out.println(">"+p(queryPOI.size())*queryPOI.size()*Math.pow(k, queryPOI.size()));
			//Station[][] table = new Station[(int) (p(queryPOI.size())*queryPOI.size()*Math.pow(k, queryPOI.size()))][queryPOI.size()];
			List<Path> newPathList = new ArrayList<Path>();
			for(Path paths : pathList) {
				for(List<POI> Permutation : POIsPermutation) {
					List<List<Station>> ans = new ArrayList<List<Station>>();
					List<List<Station>> slist = new ArrayList<List<Station>>();
					for(POI p : Permutation) {
						slist.add(p.NNstation);
					}
					//System.out.println(slist);
					mPermutation(ans, slist, Permutation.get(0).NNstation,new ArrayList<Station>());
					for(List<Station> a : ans){
						//Path tmp = new Path(paths.s);//new path without copy attribute
						Path tmp = new Path(paths);//new path without copy attribute
						tmp.Stationlist.addAll(a);
						tmp.POIlist = new ArrayList<POI>(Permutation);
						tmp.target = new ArrayList<POI>(Permutation);
						newPathList.add(tmp);
					}
				}
			}
			pathList = newPathList;
			newPathList = new ArrayList<Path>();
			for(Path a : pathList){
				for(Station ends : endStation) {
					//Path tmp = new Path(a.s);//new path without copy attribute
					Path tmp = new Path(a);//new path without copy attribute
					//tmp.Stationlist.addAll(a.Stationlist);
					tmp.d = ends;
					tmp.POIlist = new ArrayList<POI>(a.POIlist);
					newPathList.add(tmp);
				}
			}
			pathList = newPathList;
			return pathList;
		}
		
		public static int p(int num) {
			int i=num;
			while(i!=1) {
				i *= (i-1);
				i--;
			}
			return  i;
		}
		public static List<List<POI>> getPOIPermutation(List<POI> POIslist) {
			List<List<POI>> out = new ArrayList<List<POI>>();
			for(List<POI> t : Permutation.perm(POIslist)) {
				//System.out.println(">"+t);
				out.add(t);
	        }
			return out;
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
							ans.add(str2);
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
