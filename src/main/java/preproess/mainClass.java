package preproess;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap; 

public class mainClass {	
	public static void main(String[] args) {
		System.out.println(args);
		List<Station> stationList = readStationData("src/main/resources/s_loc.txt");
		List<POI> POIList = readPOIData("src/main/resources/p_loc.txt");
		Map<String, Line> map = readbusData("src/main/resources/small_公車站牌資料.csv", stationList);
		//POIList.forEach(POI -> System.out.println(POI));
		//map.values().forEach(Line -> System.out.println(Line));
		
		List<Edge> elist = readStarTimeData("src/main/resources/發車.csv",stationList,map);
		writeStarTimeData("src/main/resources/busTableData.csv",stationList);
		Edge.writeEdgeData("src/main/resources/edge.txt", elist);
		elist.forEach(tmp->System.out.println(tmp));
		
		Map<String, Double> m =  distance(stationList, POIList);
		m.forEach((s,d) -> System.out.println(s+"> "+d));
		
		
		
		//1. find NN POI to Station
		//query.findNN(stationList, POIList, 2);
		//findNN(POIList,stationList);
		
		//2. Calculate walk time and sorting
		
		//3.find real-word paths
		System.out.println("done");
		System.out.println(stationList);
		System.out.println(POIList);
	}
	public static Map<String, Double> distance(List<Station> stationList, List<POI> POIList){
		Map<String, Double> out = new HashMap<String, Double>();
		for(Station s : stationList) {
			for(POI p : POIList) {
				out.put(s.name+p.name, Math.hypot(s.x-p.x, s.y-p.y));
			}
		}
		for(Station s : stationList) {
			for(Station s2 : stationList) {
				// need update
				out.put(s.name+s2.name, Math.hypot(s.x-s2.x, s.y-s2.y));
			}
		}
		return out;
	}
	public static boolean writeStarTimeData(String file,List<Station> stationList) {
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			for(Station s : stationList) {
				for(BusTime b:s.busTable) {
					writer.println(s.name+","+b.line.name+","+ timeConverter(b.time));
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
	public static List<Edge> readStarTimeData(String csvFile, List<Station> stationList, Map<String,Line> map) {
		String line = "";
        String cvsSplitBy = ",";
        List<String[]> data = new ArrayList<String[]>();
        List<Edge> output = new ArrayList<Edge>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
            	data.add(line.split(cvsSplitBy));                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for(int i=0; i<data.size(); i++) {
        	String[] tmpLine = data.get(i);
        	Line l = map.get(tmpLine[0]);
        	for(int j=1; j<tmpLine.length; j++) {
        		//l.SList.size() == l.timeList.size()
        		int startTime = timeConverter(tmpLine[j]);
        		int countTime = startTime;
        		for(int k=0; k<l.SList.size(); k++) {
        			Station s = l.SList.get(k);        			
        			int t = l.timeList.get(k);
        			countTime += t;        			
        			s.busTable.add(new BusTime(l,startTime+countTime));
        			// new edge
        			if(k+1<l.SList.size()) {
        				Station nextS = l.SList.get(k+1);
        				Edge etmp = new Edge(l,s,nextS,countTime,l.timeList.get(k+1));
        				s.edgeList.add(etmp);
            			output.add(etmp);
        			}        			
        		}
        	}
        }
        
        return output;
	}
	public static int timeConverter(String time) {		
		String[] s = time.split(":");
		return Integer.parseInt(s[0])*60 +Integer.parseInt(s[1]);
	}
	public static String timeConverter(int i) {
		
		return String.format("%02d",i/60)+":"+String.format("%02d", i%60);
	}	
	
	public static Map<String, Line> readbusData(String csvFile,List<Station> stationList) {
		String line = "";
        String cvsSplitBy = ",";

        Map<String, Line> map = new HashMap<String, Line>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	br.readLine();  // csv-file header**
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                if(!map.containsKey(country[0])) {
                	Line l = new Line(country[0]);
                	l.addStation(country[1],country[2],stationList);
                	map.put(country[0], l);
                }
                else {
                	map.get(country[0]).addStation(country[1],country[2],stationList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //check
        for (Line l : map.values()){
        	if(l.SList.size()!=l.timeList.size()) {
    			try {
    				System.out.println("l.SList.size()!=l.timeList.size()");
    				throw new Exception("l.SList.size()!=l.timeList.size()");
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
        }

        return map;
	}
	public static List<Station> readStationData(String csvFile) {
		String line = "";
        String cvsSplitBy = ",";
        List<Station> sList = new ArrayList<Station>();
        Station stmp = null;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                stmp = new Station(country);
                sList.add(stmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sList;
	}
	public static List<POI> readPOIData(String csvFile) {
		String line = "";
        String cvsSplitBy = ",";
        List<POI> pList = new ArrayList<POI>();
        POI stmp = null;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                stmp = new POI(country);
                pList.add(stmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pList;
	}
}
