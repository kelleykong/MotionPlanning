package assignment_robots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;



public class RRT extends InformedSearchProblem{
	private World w;
	private SteeredCar scar;
	private CarRobot cr;
	
	private final int numOfVertices = 500;
	private final int MIN = 50;
	private ArrayList<CarState> carSts;
	private HashMap<CarState, ArrayList<CarState>> randomTree;
	
	private CarState startSt, goalSt;
	
	public RRT(CarState sCSt, CarState gCSt, World wld) {
		w = wld;
		scar = new SteeredCar();
		cr = new CarRobot();
		startSt = sCSt;
		goalSt = gCSt;
		carSts = new ArrayList<CarState>();
		carSts.add(sCSt);
		randomTree = new HashMap<CarState, ArrayList<CarState>>();
		randomTree.put(startSt, new ArrayList<CarState>());
	}
	
	public List<SearchNode> RRTPlanner() {
		Build_RRT();
		
		startNode = new CarStNode(startSt, 0, null);
		return astarSearch();
	}
	
	public ArrayList<CarState> getRRT() {
		return carSts;
	}
	
	public void Build_RRT() {
		for(int i = 0; i < numOfVertices; i++) {
			CarState carst = genCarSt();
			CarState nearest = findMin(carst, carSts);
			
			// 6 controls
			ArrayList<CarState> list = new ArrayList<CarState>();
			double time = 1;
			for (int j = 0; j < 6; j++) {
				CarState carstNew = scar.move(nearest, j, time);
				cr.set(carstNew);
				if (!w.carCollision(cr) && !w.carCollisionPath(cr, nearest, j, time))
					list.add(carstNew);
			}
			CarState nearestNew = findMin(carst, list);
			carSts.add(nearestNew);
			System.out.println("new vertex " + nearestNew.getX() + ", " + nearestNew.getY());
			
			//add edge
			list = randomTree.get(nearest);
			if (!list.contains(nearestNew))
				list.add(nearestNew);
			if (randomTree.containsKey(nearestNew)) {
				list = randomTree.get(nearestNew);
				if (!list.contains(nearest))
					list.add(nearest);
			}
			else {
				list = new ArrayList<CarState>();
				list.add(nearest);
				randomTree.put(nearestNew, list);
			}
		}
	}
	
	private CarState genCarSt() {
		System.out.print("gen a new random carstate: ");
		CarRobot cr = new CarRobot();
		CarState carst = new CarState();
		
		while(w.carCollision(cr)) {
			double x = Math.random()*600;
			double y = Math.random()*400;
			double tt = Math.random()*Math.PI;
		
			carst.set(x, y, tt);
			cr.set(carst);
		}
		
		System.out.println(carst.getX() + ", " +carst.getY() + ", " + carst.getTheta()+"\n");
		return carst;
	}
	
	private CarState findMin(CarState cSt, ArrayList<CarState> list) {
		double minD = 10000;
		CarState minCST = null;
		for (CarState cst: list) {
			double tmpD = Math.sqrt(Math.pow(cSt.getX() - cst.getX(),2) + Math.pow(cSt.getY() - cst.getY(),2));
			if (tmpD < minD ) {
				minD = tmpD;
				minCST = cst;
			}
		}
		return minCST;
	}
	
	
	class CarStNode implements SearchNode{
		CarState cSt;
		double cost;
		// for backchain  
		private SearchNode parent;
		
		CarStNode(CarState cst, double c, SearchNode pa) {
			cSt = new CarState(cst.getX(), cst.getY(), cst.getTheta());
			cost = c;
			parent = pa;
		}
		
		public CarState getCST() {
			return cSt;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - ((CarStNode)o).priority());
		}

		@Override
		public ArrayList<SearchNode> getSuccessors() {
			
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			for (CarState cst: randomTree.get(cSt)) {
				double tmpD = Math.sqrt(Math.pow(cSt.getX() - cst.getX(),2) + Math.pow(cSt.getY() - cst.getY(),2));
				SearchNode node = new CarStNode(cst, cost + tmpD, this);
				successors.add(node);
				
			}
			return successors;
		}

		@Override
		public boolean goalTest() {
			double d = Math.sqrt(Math.pow(cSt.getX() - goalSt.getX(),2) + Math.pow(cSt.getY() - goalSt.getY(),2));
			return (d <= MIN);
		}
		
		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(cSt.get(), ((CarStNode) other).cSt.get());
		}

		@Override
		public int hashCode() {
			return cSt.hashCode(); 
		}

		@Override
		public String toString() {
			return new String("CarSt " + cSt.getX() + ", " + cSt.getY() + ", " + cSt.getTheta()
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
