package Ver2;
import java.util.ArrayList;
import java.util.List;
import preproess.Step;

public class Path2 {
	public int time;
	public List<Step> stepList;
	
	public Path2(int i) {
		super();
		stepList = new ArrayList<Step>();
		this.time = i;
	}
	public Path2() {
		super();
		stepList = new ArrayList<Step>();
	}
}
