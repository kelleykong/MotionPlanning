package assignment_robots;

import java.util.ArrayList;
import java.util.List;

import assignment_robots.PRM.ConfigNode;
import assignment_robots.SearchProblem.SearchNode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class ArmDriver extends Application {
	// default window size
	protected int window_width = 600;
	protected int window_height = 400;
	
	public void addPolygon(Group g, Double[] points) {
		Polygon p = new Polygon();
	    p.getPoints().addAll(points);
	    
	    g.getChildren().add(p);
	}
	
	// plot a ArmRobot;
	public void plotArmRobot(Group g, ArmRobot arm, double[] config, Color c1, Color c2) {
		arm.set(config);
		double[][] current;
		Double[] to_add;
		Polygon p;
		for (int i = 1; i <= arm.getLinks(); i++) {
			current = arm.getLinkBox(i);
			
			
			to_add = new Double[2*current.length];
			for (int j = 0; j < current.length; j++) {
				System.out.println(current[j][0] + ", " + current[j][1]);
				to_add[2*j] = current[j][0];
				//to_add[2*j+1] = current[j][1];
				to_add[2*j+1] = window_height - current[j][1];
			}
			p = new Polygon();
			p.getPoints().addAll(to_add);
			p.setStroke(c1);
			p.setFill(c2);
			g.getChildren().add(p);
		}
		
	}
	
	public void plotRoadMap(Group g, ArmRobot arm, ArrayList<double[]> list) {
		System.out.println("RoadMap: " + list.size());
		for (double[] config : list) {
			arm.set(config);
			double[][] current;
			Double[] to_add;
			Polygon p;
			for (int i = 1; i <= arm.getLinks(); i++) {
				current = arm.getLinkBox(i);
				
				
				to_add = new Double[2*current.length];
				for (int j = 0; j < current.length; j++) {
					System.out.println(current[j][0] + ", " + current[j][1]);
					to_add[2*j] = current[j][0];
					//to_add[2*j+1] = current[j][1];
					to_add[2*j+1] = window_height - current[j][1];
				}
				p = new Polygon();
				p.getPoints().addAll(to_add);
				p.setStroke(Color.RED);
				p.setFill(Color.ORANGE);
				g.getChildren().add(p);
			}
		}
		
	}
	
	public void plotWorld(Group g, World w) {
		int len = w.getNumOfObstacles();
		double[][] current;
		Double[] to_add;
		Polygon p;
		for (int i = 0; i < len; i++) {
			current = w.getObstacle(i);
			to_add = new Double[2*current.length];
			for (int j = 0; j < current.length; j++) {
				to_add[2*j] = current[j][0];
				//to_add[2*j+1] = current[j][1];
				to_add[2*j+1] = window_height - current[j][1];
			}
			p = new Polygon();
			p.getPoints().addAll(to_add);
			g.getChildren().add(p);
		}
	}
	
	// The start function; will call the drawing;
	// You can run your PRM or RRT to find the path; 
	// call them in start; then plot the entire path using
	// interfaces provided;
	@Override
	public void start(Stage primaryStage) {
		
		
		// setting up javafx graphics environments;
		primaryStage.setTitle("CS 76 2D world");

		Group root = new Group();
		Scene scene = new Scene(root, window_width, window_height);

		primaryStage.setScene(scene);
		
		Group g = new Group();

		// setting up the world;
		
		// creating polygon as obstacles;
		

/*		double a[][] = {{10, 400}, {150, 300}, {100, 210}};
		Poly obstacle1 = new Poly(a);
		
		double b[][] = {{350, 30}, {300, 200}, {430, 125}};
		Poly obstacle2 = new Poly(b);
		
		double c[][] = {{110, 220}, {250, 380}, {320, 220}};
		Poly obstacle3 = new Poly(c);
*/
		double a[][] = {{10, 400}, {100, 350}, {80, 310}};
		Poly obstacle1 = new Poly(a);
		
		double b[][] = {{50, 250}, {100, 330}, {130, 325}};
		Poly obstacle2 = new Poly(b);
		
		double c[][] = {{200, 220}, {250, 280}, {220, 150}};
		Poly obstacle3 = new Poly(c);		

		double d[][] = {{170, 60}, {160, 50}, {120, 40}};
		Poly obstacle4 = new Poly(d);
		
		// Declaring a world; 
		World w = new World();
		// Add obstacles to the world;
		w.addObstacle(obstacle1);
		w.addObstacle(obstacle2);
//		w.addObstacle(obstacle3);
//		w.addObstacle(obstacle4);

		
		plotWorld(g, w);
		
		ArmRobot arm = new ArmRobot(2);
		
		double[] config1 = {100, 100, 80, Math.PI/2, 80, .1};
		double[] config2 = {100, 100, 80, Math.PI*3/2, 80, .2};
		
		arm.set(config2);
		
		PRM prm = new PRM(config1, config2, w);
		List<SearchNode> path = prm.PRMPlanner();
		plotRoadMap(g, arm, prm.getRoadMap());
		
		System.out.println("ResultPath: size " + path.size());
		for(SearchNode n: path) {
			plotArmRobot(g, arm, ((ConfigNode)n).getCFG(), Color.GREEN, Color.GREENYELLOW);
		}
		// Plan path between two configurations;
/*		ArmLocalPlanner ap = new ArmLocalPlanner();
		
		// get the time to move from config1 to config2;
		double time = ap.moveInParallel(config1, config2);
		System.out.println(time);
		
		arm.set(config2);
		
		boolean result;
		result = w.armCollisionPath(arm, config1, config2);
		System.out.println(result);
		// plot robot arm
*/		
		plotArmRobot(g, arm, config2, Color.BLUE, Color.LIGHTBLUE);
		plotArmRobot(g, arm, config1, Color.BLUE, Color.LIGHTBLUE);    
		
	    scene.setRoot(g);
	    primaryStage.show();
		

	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
