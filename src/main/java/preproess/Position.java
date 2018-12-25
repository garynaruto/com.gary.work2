package preproess;

import com.github.davidmoten.rtree.geometry.Point;

public  class Position {
	public String name;
	public double x;
	public double y;
	
	public Position(String name) {
		super();
		this.name = name;
		
	}
	public Position() {
		super();		
	}
	public Position(Point p,String s) {
		super();
		this.x = p.x();
		this.y = p.y();
		this.name = s;
	}
	
	@Override
	public String toString() {
		return name;
		//return "Position [name=" + name + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
