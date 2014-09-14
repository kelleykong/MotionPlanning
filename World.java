package assignment_robots;

import java.util.ArrayList;

// This class declares the world;
// Contains a list of polygon obstacles;
// can run collision detection between robots and obstacles;

public class World {

	// Obstacles in the world
	protected ArrayList<Poly> obstacles;
	

	private static double TOL = 0.00001;

	// Initialize the world;
	public World() {
		obstacles = new ArrayList<Poly>();
		
	}
	
	// return the list of polygon obstacles;
	public ArrayList<Poly> getObstacles() {
		return obstacles;
	}
	
	// get the number of obstacles;
	public int getNumOfObstacles() {
		return obstacles.size();
	}
	
	// get the configuration of the ith obstacle;
	public double[][] getObstacle(int i) {
		if (i > obstacles.size()) {
			System.exit(1);
		}
		
		return obstacles.get(i).get();
		
	}

	// Add an obstacle;
	public void addObstacle(Poly p) {
		obstacles.add(p);
	}

	// Check if two polygon is in collision;
	
	public boolean isCollision(Poly p1, Poly p2) {
		double result;
		result = CollisionChecker.trial(p1.get(), p2.get());
		if (Math.abs(result) < TOL) {
			return false;
		}
		else {
			return true;
		}
	}

	// Check if a car and a polygon is in collision;
	public boolean isCollision(Poly p1, CarRobot p2) {
		double result;
		
		result = CollisionChecker.trial(p1.get(), p2.get());
		if (Math.abs(result) < TOL) {
			return false;
		}
		else {
			return true;
		}
	}
	
	// Check if a car is in collision with any obstacle in the world;
	public boolean carCollision(CarRobot p) {
		double result;
		for (Poly g : obstacles) {
			result = CollisionChecker.trial(g.get(), p.get());
			if (result > TOL) {
				return true;
			}
		}
		
		return false;
	}
	
	// Check if an arm is in collision with any obstacle in the world;
	public boolean armCollision(ArmRobot p) {
		double result;
		double[][] link_i;
		for (Poly g : obstacles) {
			for (int j = 1; j <= p.getLinks(); j++) {
				link_i = p.getLinkBox(j);
				result = CollisionChecker.trial(g.get(), link_i);
				if (result > TOL) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Check if a robot arm path between two configurations collides with any 
	// obstacle;
	// main collision detection you should call for PRM;
	public boolean armCollisionPath(ArmRobot p, double[] config1, double[] config2) {
		
		double[] config = new double[config1.length];
		
		ArmLocalPlanner ap = new ArmLocalPlanner();
		double time = ap.moveInParallel(config1, config2);
		double[] path = ap.getPath(config1, config2);
		double step = 0.5;
		for (int i = 0; i < config1.length; i++) {
			config[i] = config1[i];
		}
		double current = 0;
		boolean result;
		while (current < time) {
			for (int i = 0; i < config1.length; i++) {
				config[i] = config[i] + path[i] * step;
			}
			p.set(config);
			result = armCollision(p);
			if (result) {
				return true;
			}
			current = current + step;
		}
		
		return false;
	}
	
	
	// check if a steered car path move from state s1 using control ctrl
	// for duration time is in collision with any obstacle;
	// Main collision function you should call for RRT;
	public boolean carCollisionPath(CarRobot p, CarState s1, int ctrl, double time) {
		double step = 0.5;
		double[][] path = p.getPath(s1, ctrl, time, step);
		int i = 0;
		CarState current = new CarState();
		
		boolean result;
		
		while (i < path.length) {
			current.set(path[i][0], path[i][1], path[i][2]);
			p.set(current);
			result = carCollision(p);
			if (result) {
				return true;
			}
			i += 1;
		}
		
		return false;
	}

}