package assignment_robots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


public class PRM extends InformedSearchProblem {
	private World w;
	ArmLocalPlanner ap;

	private final int numofVertices = 500;
	// number of neighbors
	private final int numofNeighbors = 50;
	// this is the initial config;
	private double[] startConfig;
	private double[] goalConfig;
	
	ArrayList<double[]> configs;
	HashMap<double[], ArrayList<Edge>> roadMap;
	
	public PRM(double[] c1, double[] c2, World wld) {
		startConfig = c1;
		goalConfig = c2;
		w = wld;
		ap = new ArmLocalPlanner();
		
		configs = new ArrayList<double[]>();
		configs.add(c1);
		configs.add(c2);
		
		roadMap = new HashMap<double[], ArrayList<Edge>>();
		roadMap.put(c1, new ArrayList<Edge>());
		roadMap.put(c2, new ArrayList<Edge>());
	}
	
	public List<SearchNode> PRMPlanner() {
		build_RoadMap();
		
		startNode = new ConfigNode(startConfig, 0, null);
		return astarSearch();
	}
	
	public void build_RoadMap() {
		System.out.println("build_RoadMap!!!");

		int i = 2;
		ArmRobot tmpArm = new ArmRobot(startConfig.length/2-1);
		
		while (i < numofVertices) {
			//generate a random vertex
			double[] v = genVertex();
			tmpArm.set(v);
			//if the vertex is not collided with obstacles.
			//                 and not already in roadMap
			if (!w.armCollision(tmpArm) && !roadMap.containsKey(v)) {
				configs.add(v);
				i++;
				PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
				for (double[] cfg: configs) {
					//cannot add a circle v-v
					if (Arrays.equals(cfg, v)) 
						continue;
					if (!w.armCollisionPath(tmpArm, v, cfg)) {
						double cost = ap.moveInParallel(v, cfg);
						pq.add(new Edge(cfg, cost));
					}
				}
				ArrayList<Edge> list = new ArrayList<Edge>();
				while(list.size() <= numofNeighbors && !pq.isEmpty())
					list.add(pq.remove());
				roadMap.put(v, list);
				
				//add symmetrical edge
				for (Edge e: list) {
					Edge eN = new Edge(v, e.time);
					ArrayList<Edge> tmpPQ = roadMap.get(e.config);
					if (!tmpPQ.contains(eN)) {
						tmpPQ.add(eN);
					}					
				}
			}
			
		}
		
	}
	
	private double[] genVertex() {
		System.out.print("gen a new random vertex: ");
		double[] vertex = new double[startConfig.length];
		vertex[0] = startConfig[0];
		vertex[1] = startConfig[1];
		System.out.print(vertex[0] + ", " + vertex[1] + ", ");
		
		for (int i = 2; i < startConfig.length; i++) {
			if (i % 2 == 0)
				vertex[i] = startConfig[i];
			else
				vertex[i] = Math.random()*2*Math.PI;
			System.out.print(vertex[i] + ", ");
		}
		
		System.out.println("!!!");
		return vertex;
	}
	
	public ArrayList<double[]> getRoadMap() {
		return configs;
	}
	
	class Edge implements Comparable<Object> {
		double[] config;
		// time when moving through the Edge
		double time;
		
		public Edge(double[] cfg, double t) {
			config = cfg;
			time = t;
		}
		

		@Override
		public int compareTo(Object o) {
			return (int) Math.signum(time - ((Edge)o).time);
		}
		
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(config, ((Edge) other).config);
		}
		
		@Override
		public int hashCode() {
			int hash = 0;
			for (int i = 3; i < config.length; i = i+2)
				hash = hash* 1000 + (int)(config[i]*100);
			return hash; 
		}
		
	}
	
	
	class ConfigNode implements SearchNode{
		double[] config;
		double cost;
		// for backchain  
		private SearchNode parent;
		
		ConfigNode(double[] cfg, double c, SearchNode pa) {
			config = cfg;
			cost = c;
			parent = pa;
		}
		
		public double[] getCFG() {
			return config;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - ((ConfigNode)o).priority());
		}

		@Override
		public ArrayList<SearchNode> getSuccessors() {
			
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			for (Edge succ: roadMap.get(config)) {
				SearchNode node = new ConfigNode(succ.config, 
						this.cost + succ.time, this);
				successors.add(node);
				
			}
			return successors;
		}

		@Override
		public boolean goalTest() {
			return Arrays.equals(config, goalConfig);
		}
		
		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(config, ((ConfigNode) other).config);
		}

		@Override
		public int hashCode() {
			int hash = 0;
			for (int i = 3; i < config.length; i = i+2)
				hash = hash* 1000 + (int)(config[i]*100);
			return hash; 
		}

		@Override
		public String toString() {
			return new String("Config " + config[3] + ", " + config[5] + " "
					+ " prior " + priority());
		}

		@Override
		public double getCost() {
			return cost;
		}

		@Override
		public SearchNode getParent() {
			return parent;
		}

		@Override
		public double heuristic() {
			
			return 0;
		}

		@Override
		public double priority() {
			return heuristic() + getCost();
		}
		
	}
	
}
