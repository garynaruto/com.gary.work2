package work;
import java.util.Comparator;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;

import preproess.Position;

class NodeComparator implements Comparator<Node<Position, Geometry>> {
	public static Point p; 
	@Override
    public int compare(Node<Position, Geometry> s1, Node<Position, Geometry> s2) { 
        if (s1.geometry().distance(p) > s2.geometry().distance(p)) 
            return 1; 
        else if (s1.geometry().distance(p) < s2.geometry().distance(p)) 
            return -1; 
        return 0; 
    } 
}