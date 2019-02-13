package preproess;
import java.util.ArrayList;
import java.util.List;

public class Station extends Position{
	
	public List<Line> lList;
	public List<BusTime> busTable;
	public List<Edge> edgeList;
	public List<Edge> walkedgeList;
	public boolean visit = false;
	public Station(String[] s) throws Exception {
		if(s.length!=3) {
			System.out.println("Station constructor : Error length != 3");
			throw new Exception("Station constructor : Error length != 3");
		}
		this.name = s[0];
		this.x = Double.parseDouble(s[1]);
		this.y = Double.parseDouble(s[2]);
		lList = new ArrayList<Line>();
		busTable = new ArrayList<BusTime>();
		edgeList = new ArrayList<Edge>();
		walkedgeList = new ArrayList<Edge>();
		
	}
	
	@Override
	public String toString() {
		String s = "";
		for(Line n : lList) {
			s=s+n.name+"-";
		}
		return "s"+this.name;
		//return id+":"+s;
		//return "s("+id+","+x+","+y+")";
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	public boolean addLine(Line s) {// throws Exception
		if(lList.indexOf(s)==-1) {
			lList.add(s);
			return true;
		}
		else {
			lList.add(s);
			return true;
			//System.out.println("Station add Line : Error repeated ");
			//throw new Exception("Station add Line : Error repeated ");
		}
	}
	/*public boolean addBusLine (Line s) throws Exception {
		if(lList.indexOf(s)==-1) {
			lList.add(s);
			return true;
		}
		else {
			System.out.println("Station add Line : Error repeated ");
			throw new Exception("Station add Line : Error repeated ");
		}
	}*/
	
}
