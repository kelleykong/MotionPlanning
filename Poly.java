package assignment_robots;

// This class declares a polygon;
// Each obstacle in the World is a polygon;


public class Poly {

	// polygon is a sequence of (x, y) coordinates;
	protected double[][] poly;

	// Construct the polygon with the number of vertices;
	public Poly(int num) {
		poly = new double[num][2];
	}
	
	public Poly(double[][] polygon) {
		poly = new double[polygon.length][2];
		set(polygon);
	}

	// Set the configuration of the polygon;
	public void set(double[][] points){
		for (int i = 0; i < poly.length; i++) {
			poly[i][0] = points[i][0];
			poly[i][1] = points[i][1];
		}
		
	}

	// get the configuration of the polygon;
	public double[][] get(){
		return poly;
	}

}