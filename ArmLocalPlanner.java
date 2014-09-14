package assignment_robots;

// This is the local planner of the robot arms;
// Can return time between two configurations;
// can get the path between configurations;

public class ArmLocalPlanner {

	// Get the time to move from configuration 1 to configuration 2;
	// two configurations must be valid configurations for the arm; 
	public double moveInParallel(double[] config1, double[] config2) {
		if (config1.length != config2.length) {
			System.exit(1);
		}
		if (config1.length % 2 != 0) {
			System.exit(1);
		}
		
		double d = 0;
		double maxt = 0;
		
		for (int i = 0; i < (config1.length/2); i++) {
			if (i == 0) {
				d = Math.sqrt(Math.pow(config1[0]-config2[0], 2)+Math.pow(config1[1]-config2[1], 2));
				
			}
			else {
				d = Math.abs(config1[2*i+1]-config2[2*i+1]);
			}
			if (d > maxt) {
				maxt = d;
			}
			
		}
		
		
		return maxt;
	}
	
	// Given two configurations, get the "path" between configurations;
	// return is a double array with the same length as configurations;
	// path[i] is the velocity of component config[i];
	// basically, given certain time duration: step, path[i]*step 
	// is the movement of component config[i] during step;
	public double[] getPath (double[] config1, double[] config2) {
		double time = moveInParallel(config1, config2);
		double[] path = new double[config1.length];
		
		for (int i = 0; i < config1.length; i++) {
			path[i] = (config2[i] - config1[i]) / time;
		}
		
		return path;
	}
	
	
 }
