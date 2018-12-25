package preproess;

public class BusTime {
	
	
	int time;
	Line line;
	public BusTime(Line line,int time) {
		this.time = time;
		this.line = line;
	}
	
	@Override
	public String toString() {
		return "BusTime ["+line+":" +mainClass.timeConverter(time)+ "]\n";
	}
	
}
