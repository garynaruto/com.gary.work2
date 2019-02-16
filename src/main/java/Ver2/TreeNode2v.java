package Ver2;
import java.util.ArrayList;
import java.util.List;
import preproess.*;

public class TreeNode2v {
	public Position p;
	public List<Position> Nexts;
	public List<TreeNode2v> child;
	public List<List<List<Position>>> StepTable;
	
	public TreeNode2v() {
		this.child = new ArrayList<TreeNode2v>();
		this.Nexts = new ArrayList<Position>();
		this.StepTable = new ArrayList<List<List<Position>>>();
	}
	public TreeNode2v(Position p) {
		this.p = p;
		this.Nexts = new ArrayList<Position>();
		this.child = new ArrayList<TreeNode2v>();
		this.StepTable = new ArrayList<List<List<Position>>>();
	}
	public boolean insert(List<Position> com) {
		//if ( com == null )return false;
		//System.out.println("insert :"+com.pList);
		System.out.println("insert");
		TreeNode2v node = this;
		TreeNode2v end = new TreeNode2v(com.get(com.size()-1));
		//System.out.println("com.pList.get(0) :"+com.pList.get(0));
		for(int i=1; i<com.size(); i++) {
			Position tmp = com.get(i);
			//System.out.println(i+" : "+tmp);
			if(tmp instanceof POI ) {
				int index = find(node, tmp);
				//System.out.println("POI index : " + index);
				if(index == -1) {
					//add position
					TreeNode2v newChild = new TreeNode2v(tmp);
					node.child.add(newChild);
					node.Nexts.add(tmp);
					
					node = newChild;
				}
				else {
					node = node.child.get(index);
				}
			}
			else {
				// POI - end
				node.child.add(end);
				node.Nexts.add(end.p);
			}
		}
		return false;
	}
	public static int find(TreeNode2v node, Position p) {
		for(int i=0; i<node.Nexts.size(); i++) {
			if(node.Nexts.get(i) == p) {
				return i;
			}
		}
		return -1;
	}
	public void traversal(String s) {
		System.out.println( s+ "<"+this.p+">");
		System.out.println(s+"next :"+this.Nexts);
		StepTable.forEach(a->a.forEach(b->System.out.println(s+b)));
		this.child.forEach(a->a.traversal(s+"--"));
	}
}


