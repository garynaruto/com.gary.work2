package Ver2;
import java.util.ArrayList;
import java.util.List;
import preproess.*;

public class TreeNode {
	public Position p;
	public List<Position> Nexts;
	public List<List<Step>> StepTable;
	
	public TreeNode() {
		Nexts = new ArrayList<Position>();
		StepTable = new ArrayList<List<Step>>();
	}

}
