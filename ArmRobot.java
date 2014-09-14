package assignment_robots;

// This is the class of the generalized arm robot; 
// Each arm have a moving base, and k links;
// each link has a length, and an angle relative to previous link;

public class ArmRobot {
	//config, first 2 elements are x, y of base;
	//then, length and angle for each link;
	protected double[] config;
	// this is the number of links;
	protected int links;
	// the width of each link;
	protected double width;
	
	// get the number of links;
	public int getLinks() {
		return links;
	}
	
	// constructor, input the number of links of this arm;
	// initialize all the coordinates to 0; the initial width is 10;
	public ArmRobot(int num) {
		links = num;
		config = new double[2*num+2];
		int i = 0;
		for ( i = 0; i < config.length; i++) {
			config[i] = 0;
		}
		width = 10;
	}
	
	// set the width of each link;
	public void setWidth(double width) {
		this.width = width;
	}

	// set the base of the arm;
	public void setBase(double x, double y) {
		config[0] = x;
		config[1] = y;
	}
	
	// set the length and the angle of each link;
	public void setLink(int i, double len, double ang) {
		config[2*i] = len;
		config[2*i+1] = ang;
	}

	// set the entire configuration of the arm;
	public void set(double[] configuration) {
		config[0] = configuration[0];
		config[1] = configuration[1];
		for (int i = 1; i <= links; i++) {
			setLink(i, configuration[2*i], configuration[2*i+1]);
		}
	}

	// get the base coordinates of the arm;
	public double[] getBase() {
		double[] base = new double[2];
		base[0] = config[0];
		base[1] = config[1];
		return base;
	}
	
	// get the length and angle of the ith link;
	public double[] getLink(int i) {
		double[] link_i = new double[2];
		link_i[0] = config[2*i];
		link_i[1] = config[2*i+1];
		return link_i;
	}
	
	// get the configuration of the arm;
	public double[] get() {
		return config;
	}
	
	// get the rectangular coordinates of the ith link;
	// need to use the rectangular to check for collision;
	public double[][] getLinkBox(int i) {
		double[][] rect = new double[4][2];
		
		
		double x = config[0]; 
		double y = config[1];
		double ang = 0;
		for (int j = 0; j < i-1; j++) {
			ang = (ang + config[j*2+3]) % (2*Math.PI);
			x = x + config[j*2+2] * Math.cos(ang);
			y = y + config[j*2+2] * Math.sin(ang);
		}
		double xp = x;
		double yp = y;
		ang = (ang + config[2*i+1]) % (2*Math.PI);
		x = xp + config[2*i] * Math.cos(ang);
		y = yp + config[2*i] * Math.sin(ang);
		System.out.println(config[2*i] + ", " + i);
		
		rect[0][0] = xp + width*Math.cos(ang + Math.PI / 2);
		rect[0][1] = yp + width*Math.sin(ang + Math.PI / 2);
		
		rect[1][0] = x + width*Math.cos(ang + Math.PI / 2);
		rect[1][1] = y + width*Math.sin(ang + Math.PI / 2);
		
		rect[2][0] = x + width*Math.cos(ang - Math.PI / 2);
		rect[2][1] = y + width*Math.sin(ang - Math.PI / 2);
		
		rect[3][0] = xp + width*Math.cos(ang - Math.PI / 2);
		rect[3][1] = yp + width*Math.sin(ang - Math.PI / 2);
		
		return rect;
	}

}