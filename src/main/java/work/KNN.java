package work;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.internal.*;

import preproess.Position;
import preproess.Station;

public class KNN {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<int[]> data = readData("src/main/resources/bbs-test-m-input.txt");
		
		RTree<Position, Geometry> tree = RTree.star().minChildren(3).maxChildren(6).create();
		for(int[] tmp : data) {
			tree = tree.add(new Position(tmp[0]+","+tmp[1]) , Geometries.point(tmp[0],tmp[1]));
		}
		//tree.visualize(500, 500).save("src/main/resources/rtree.png");
		
		//Iterable<Entry<String, Geometry>> results = tree.search(Geometries.point(50000000,50000000), 20000000).toBlocking().toIterable();
		//results.forEach(t->System.out.println(t+" "+distance(((Point)t.geometry()).x(),((Point)t.geometry()).x())));
		//results.forEach(t->System.out.println(distance((Point)t.geometry(), Geometries.point(50000000,50000000))+" "+t));
		//System.out.println(tree.root().or(null).getClass().getName());
		//com.github.davidmoten.rtree.internal.NonLeafDefault m =null;
		/*
		System.out.print(tree.root().or(null).geometry().mbr().x1()+" , ");
		System.out.println(tree.root().or(null).geometry().mbr().x2());
		System.out.print(tree.root().or(null).geometry().mbr().y1()+" , ");	
		System.out.println(tree.root().or(null).geometry().mbr().y2());
		*/
		
		PriorityQueue<Entry<Position, Geometry>> a = nn(tree, Geometries.point(50000000,50000000) , 1);
		
		a.forEach(t->System.out.println(t.geometry().distance(Geometries.point(50000000,50000000))+"¡G"+t));
		
		
	}
	public static List<Entry<Position, Geometry>> KNNQuery(RTree<Position, Geometry> tree, Point p, int k) {
		List<Entry<Position, Geometry>> output = new ArrayList<Entry<Position, Geometry>>(); 
		PriorityQueue<Entry<Position, Geometry>> a = nn(tree, p , k);
		//a.forEach(t ->output.add(t));
		//a.forEach(t ->System.out.println(t));
		while(!a.isEmpty()) {
			output.add(a.poll());
		}
		return output;
	}
	public static List<Station> KNNQuery2(RTree<Position, Geometry> tree, Point p, int k) {
		List<Station> output = new ArrayList<Station>(); 
		PriorityQueue<Entry<Position, Geometry>> a = nn(tree, p , k);
		//a.forEach(t ->output.add(t));
		//a.forEach(t ->System.out.println(t));
		for(int i=0; i<k && !a.isEmpty(); i++) {
			output.add((Station)a.poll().value());
		}
//		while(!a.isEmpty()) {
//			output.add((Station)a.poll().value());
//		}
		return output; 
	}
	public static PriorityQueue<Entry<Position, Geometry>> nn(RTree<Position, Geometry> r, Point p, int k) {
		//Queue<Node<String, Geometry>> queue = new LinkedList<Node<String, Geometry>>();
		PriorityQueue<Node<Position, Geometry>> queue = new PriorityQueue<Node<Position, Geometry>>(10000, new NodeComparator());
		PriorityQueue<Entry<Position, Geometry>> Ans = new PriorityQueue<Entry<Position, Geometry>>(k, new EntryComparator());
		//PriorityQueue<Entry<Position, Geometry>> Ans = new PriorityQueue<Entry<Position, Geometry>>((Entry<Position, Geometry> s1, Entry<Position, Geometry> s2)-> s1.geometry().distance(p) - s2.geometry().distance(p));
		
		NodeComparator.p = p;
		EntryComparator.p = p;
		//System.out.println("NodeComparator.p ="+NodeComparator.p );
		queue.add(r.root().or(null));
		while(!queue.isEmpty()) {
			Node<Position, Geometry> tmp = queue.poll();
			//System.out.println(tmp.getClass().getName());
			if(tmp instanceof NonLeaf) {
				//((NonLeaf<Position, Geometry>) tmp).getClass().getName();
				queue.addAll(((NonLeaf<Position, Geometry>) tmp).children());
			}
			else if(tmp instanceof LeafDefault) {
				//System.out.println(((LeafDefault<String, Geometry>)tmp).entries());
				
				Ans.addAll(((LeafDefault<Position, Geometry>)tmp).entries());
				//queue.addAll(((LeafDefault<String, Geometry>) tmp).children());
				
				
				if(Ans.size() >= k && !queue.isEmpty()) {
					@SuppressWarnings("unchecked")
					Entry<Position, Geometry> e = (Entry<Position, Geometry>)Ans.toArray()[k-1];
					if(e.geometry().distance(NodeComparator.p) < queue.element().geometry().distance( NodeComparator.p)){
						break;
					}
				}
			}
		}
		return Ans;
	}
	public static  double distance(Point p1,Point p2) {
		return  Math.hypot(p1.x()-p2.x(), p1.y()-p2.y());
    }
	public static double distance(double x1, double y1, double x2, double y2) {
        return  Math.hypot(x1-x2, y1-y2);
    }
	public static List<int[]> readData(String csvFile) {
		List<int[]> data = new ArrayList<int[]>();
		String line = "";
        String cvsSplitBy = " ";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	while ((line = br.readLine()) != null) {
            	String[] country = line.split(cvsSplitBy);
            	int[] tmp = new int[2];
            	
            	tmp[0] = Integer.parseInt(country[0]);
            	tmp[1] = Integer.parseInt(country[1]);
                data.add(tmp);
                //System.out.println(country[0] + " , " + country[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
	}
}



