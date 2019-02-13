package Ver2;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import preproess.*;

public class TreeNode {
	public Position p;
	public List<Position> Nexts;
	public List<TreeNode> child;
	public List<List<List<Position>>> StepTable;
	
	public TreeNode() {
		this.child = new ArrayList<TreeNode>();
		this.Nexts = new ArrayList<Position>();
		this.StepTable = new ArrayList<List<List<Position>>>();
	}
	public TreeNode(Position p) {
		this.p = p;
		this.Nexts = new ArrayList<Position>();
		this.child = new ArrayList<TreeNode>();
		this.StepTable = new ArrayList<List<List<Position>>>();
	}
	public boolean insert(Combination com) {
		//if ( com == null )return false;
		//System.out.println("insert :"+com.pList);
		
		TreeNode node = this;
		List<Position> buf = new LinkedList<Position>();
		buf.add(com.pList.get(0));
		//System.out.println("com.pList.get(0) :"+com.pList.get(0));
		for(int i=1; i<com.pList.size(); i++) {
			Position tmp = com.pList.get(i);
			//System.out.println(i+" : "+tmp);
			if(tmp instanceof POI || i == com.pList.size()-1) {
				buf.add(tmp);
				int index = find(node, tmp);
				//System.out.println("POI index : " + index);
				if(index == -1) {
					//add position
					TreeNode newChild = new TreeNode(tmp);
					node.child.add(newChild);
					node.Nexts.add(tmp);
					List<List<Position>> tmpList = new ArrayList<List<Position>>();
					tmpList.add(new LinkedList<Position>(buf));
					node.StepTable.add(tmpList);
					node = newChild;
				}
				else {
					//add [s-s]
					//check the same List of Position
					List<List<Position>> stepList = node.StepTable.get(index);
					boolean flg = false;
					for(List<Position> l : stepList) {
						
						if(l.toString().equals(buf.toString())) {
							//System.out.println("f");
							flg = true;
							break;
						}
					}
					if(!flg) {
						//System.out.println("!f");
						//System.out.println("buf:"+buf.t);
						stepList.add(new LinkedList<Position>(buf));
					}
					node = node.child.get(index);
				}
				buf.clear();
				buf.add(tmp);
			}
			else {
				buf.add(tmp);
			}
		}
		return false;
	}
	public static int find(TreeNode node, Position p) {
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


