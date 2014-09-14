package assignment_robots;

// This is a SteeredCar simulator;
// Totally 6 controls available; 
// The car can execute one control at a time;
// To simulate, you have to give it a control (an integer) and a time;
// so the car will execute the control for given time;

class SteeredCar {

	// The control set;
	protected double[][] control = {{10, 0, 0}, {-10, 0, 0}, {10, 0, 10}, {10, 0, -10}, {-10, 0, 10}, {-10, 0, -10}};

	// Get a control;
	public double[] getControl(int i) {
		double[] ctrl = new double[3];
		ctrl[0] = control[i][0];
		ctrl[1] = control[i][1];
		ctrl[2] = control[i][2];
		return ctrl;
	}
	
	// Main simulation, move from a CarState s, apply control C for time t;
	// returned is a new CarState after the execution;
	public CarState move(CarState s, int c, double t) {
		CarState g = new CarState();

		double[] ctrl = new double[3];
		ctrl[0] = control[c][0];
		ctrl[1] = control[c][1];
		ctrl[2] = control[c][2];

		if (c == 0 || c == 1) {
			g.set(s.getX() + ctrl[0] * Math.cos(s.getTheta()) * t, s.getY() + ctrl[0] * Math.sin(s.getTheta
				()) * t, s.getTheta());
		}
		else if (c == 2 || c == 5) {
			double[] center = new double[2];
			center[0] = s.getX() - Math.sin(s.getTheta());
			center[1] = s.getY() + Math.cos(s.getTheta());
			double angle = s.getTheta() + ctrl[0] * t - Math.PI / 2;
			g.set(center[0] + Math.cos(angle), center[1] + Math.sin(angle), angle+Math.PI/2);
 		}
		else if (c == 3 || c == 4) {
			double[] center = new double[2];

			center[0] = s.getX() + Math.sin(s.getTheta());
			center[1] = s.getY() - Math.cos(s.getTheta());
			double angle = s.getTheta() + ctrl[0] * t + Math.PI / 2;
			g.set(center[0] + Math.cos(angle), center[1] + Math.sin(angle), angle-Math.PI/2);

		}
		return g;

	}

}