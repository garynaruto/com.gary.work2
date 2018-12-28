package preproess;

public class Step {
	
	@Override
	public String toString() {
		if(this.way == Action.POI) {
			return "[" + a + "(" + costTime + ")]";
		}
		return "[" + a + "-" + b +"]";
	}


	public Position a;
	public Position b;
	public int starTime;
	public int costTime;
	//public int waitTime;
	public Action way;
	public Step() {
		super();
	}
	public Step(Position a, Position b, int costTime, Action way) {
		super();
		this.a = a;
		this.b = b;
		this.starTime = -1;
		this.costTime = costTime; 
		//this.waitTime = -1;
		this.way = way;
	}
	
	
	public enum Action {
	    walk,
	    bus,
	    POI
	}
}