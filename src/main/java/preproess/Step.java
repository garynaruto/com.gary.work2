package preproess;

public class Step {
	public Position a;
	public Position b;
	public int starTime;
	public int costTime;
	public int waitTime;
	public Action way;
	
	
	
	public enum Action {
	    walk,
	    bus
	}
}