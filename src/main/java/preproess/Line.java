package preproess;

import java.util.ArrayList;
import java.util.List;

public class Line {
	public static Line walk;
	public String name;
	public List<Station> SList;
	public List<Integer> timeList;
	public Line(String name) {
		super();
		this.name = name;
		SList = new ArrayList<Station>();
		timeList = new ArrayList<Integer>();;
	}
	public boolean addStation(String s,String time,List<Station> stationList) throws Exception {
		Station tmp = null;
		boolean flg = false;
		for(int i=0;i<stationList.size(); i++) {
			if(stationList.get(i).name.equals(s)) {
				tmp = stationList.get(i);
				flg = true;
				break;
			}
		}
		
		if(flg) {
			if(SList.indexOf(tmp)==-1) {
				SList.add(tmp);
				timeList.add(Integer.parseInt(time));
				tmp.addLine(this);
				return true;
			}
			else {
				//System.out.println("Line addStation : Error repeated ");
				//System.out.println("tmp : "+tmp);
				//throw new Exception("Line addStation : Error repeated ");
				SList.add(tmp);
				timeList.add(Integer.parseInt(time));
				tmp.addLine(this);
				return true;
			}		
		}
		else {
			System.out.println("Line addStation : s not in stationList ");
			System.out.println(" s : "+s);
			throw new Exception("Line addStation : s not in stationList ");
		}
	}
	@Override
	public String toString() {
		String s = "";
		for(Station n:SList) {
			s=s+n+"-";
		}
		//return "[L:"+name+s+"]";
		//return "[L:"+s+"]";
		return "L:"+name+" ";
	}
	public static Line getWalk() {
		if(Line.walk != null) {
			return Line.walk;
		}
		Line.walk = new Line("walk");
		return Line.walk;
	}
}
