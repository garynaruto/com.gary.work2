package preproess;

import java.util.List;

public class POI extends Position{
	public int stayTime; // set with query
	public int starTime;
	public int endTime;
	public List<Station> NNstation; // set with KNN
	public List<List<Position>> POIiStation;// set with Q1.eum()
	public POI(String[] s) throws Exception {
		if(s.length!=5) {
			System.out.println("POI constructor : Error length != 5");
			throw new Exception("POI constructor : Error length != 5");
		}
		this.name = s[0];
		this.x = Double.parseDouble(s[1]);
		this.y = Double.parseDouble(s[2]);
		this.starTime = mainClass.timeConverter(s[3]);
		this.endTime = mainClass.timeConverter(s[4]);
		
	}
	@Override
	public String toString() {
		return "p"+this.name;
		//return "p("+id+","+x+","+y+")";
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
}
