package assignment_robots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class SearchProblem {

	// used to store performance information about search runs.
	// these should be updated during the process of searches

	// see methods later in this class to update these values
	protected int nodesExplored;
	protected int maxMemory;

	protected SearchNode startNode;

	protected interface SearchNode extends Comparable<SearchNode> {
		
		public ArrayList<SearchNode> getSuccessors();

		public boolean goalTest();
		
		// cost needed for uniform cost and a* searches.  Also useful for debugging
		//  bfs.
		public double getCost();
		
		// get parent of this node --------------modify by M.K. 
		public SearchNode getParent();

		// heuristic and priority are only relevant for informed searches, but
		//  for coding simplicity, I chose to have only one type of node.
		//  heuristic is ignored for uninformed searches
		public double heuristic();
		public double priority();
	}

	// breadthFirstSearch: return a list of connecting Nodes, or null
	// no parameters, since start and goal descriptions are problem-dependent.
	// therefore, constructor of specific problems should set up start
	// and goal conditions, etc.

	public List<SearchNode> breadthFirstSearch() {
//		System.out.println("bfs begin!");

		resetStats();

		Queue<SearchNode> fringe = new LinkedList<SearchNode>();

		// map to store backchaining information
		HashMap<SearchNode, SearchNode> reachedFrom = new HashMap<SearchNode, SearchNode>();

		// startNode must be set by the constructor of the particular
		// search problem, since a UUSearchNode is an interface and can't
		// be instantiated directly by the search

		reachedFrom.put(startNode, null); // startNode was not reached from any
											// other node
		fringe.add(startNode);

		// current depth of the search; useful for debugging.
		int currentDepth = 0;

		while (!fringe.isEmpty()) {
			incrementNodeCount();

			updateMemory(fringe.size() + reachedFrom.size());

			SearchNode currentNode = fringe.remove();

			if (currentNode.goalTest()) {
//				System.out.println("goalTest " + currentNode);
				return backchain(currentNode, reachedFrom);
			}

			ArrayList<SearchNode> successors = currentNode.getSuccessors();

//			 System.out.println("successors " + successors);

			for (SearchNode node : successors) {
				// if not visited
				if (!reachedFrom.containsKey(node)) {
					reachedFrom.put(node, currentNode);
					fringe.add(node);
				}
			}
		}

		return null;
	}

	// backchain should only be used by bfs, not the recursive dfs
	protected List<SearchNode> backchain(SearchNode node,
			HashMap<SearchNode, SearchNode> visited) {

		LinkedList<SearchNode> solution = new LinkedList<SearchNode>();

		// chain through the visited hashmap to find each previous node,
		// add to the solution
		while (node != null) {
			solution.addFirst(node);
			node = visited.get(node);
		}

		return solution;
	}

	public List<SearchNode> depthFirstMemoizingSearch(int maxDepth) {
		resetStats();

		// keep depth in visited. If we find a shorter way to a node,
		// we need to keep exploring through that node,
		// since otherwise a path within the permitted depth may exist to some
		// successor of that node
		HashMap<SearchNode, Integer> visited = new HashMap<SearchNode, Integer>();

		// in the recursion, we add children to visited, but not the
		// current node. So we need to add the startNode to visited before the
		// recursion starts.
		visited.put(startNode, 0);
		return dfsrm(startNode, visited, 0, maxDepth);

	}

	// recursive memoizing dfs. Private, because it has the extra
	// parameters needed for recursion.
	private List<SearchNode> dfsrm(SearchNode currentNode,
			HashMap<SearchNode, Integer> visited, int depth, int maxDepth) {

		updateMemory(visited.size());
		incrementNodeCount();

		// System.out.println(currentNode);

		// System.out.println("visited size " + visited.size());

		// base case: goal found
		if (currentNode.goalTest()) {
			// we found the goal! Add this node to the path
			LinkedList<SearchNode> path = new LinkedList<SearchNode>();
			path.add(currentNode);
			return path;
		}

		// base case: maximum depth reached
		if (depth == maxDepth) {
			return null;
		}

		// recursive case
		ArrayList<SearchNode> successors = currentNode.getSuccessors();

		for (SearchNode s : successors) {
			// only visit the successor if we haven't already
			if (!visited.containsKey(s) || depth < visited.get(s)) {
				visited.put(s, depth);
				List<SearchNode> path = dfsrm(s, visited, depth + 1, maxDepth);
				if (path != null) {
					// one of the successors of this node is on a path the to
					// a goal, so this node is also on this path to the goal.
					path.add(0, currentNode);
					return path;
				}
			}
		}

		// didn't find a path to goal through any successors of this node
		return null;
	}

	public List<SearchNode> IDSearch(int maxDepth) {
		resetStats();

		for (int i = 0; i < maxDepth; i++) {
			resetStats();

			HashSet<SearchNode> currentPath = new HashSet<SearchNode>();
			List<SearchNode> path = dfsrpc(startNode, currentPath, 0, i);

			if (path != null) {
				return path;
			}
		}
		return null;
	}

	public List<SearchNode> depthFirstPathCheckingSearch(int maxDepth) {
		resetStats();
		HashSet<SearchNode> currentPath = new HashSet<SearchNode>();
		return dfsrpc(startNode, currentPath, 0, maxDepth);

	}

	// recursive path-checking dfs. Private, because it has the extra
	// parameters needed for recursion.
	private List<SearchNode> dfsrpc(SearchNode currentNode,
			HashSet<SearchNode> currentPath, int depth, int maxDepth) {

		currentPath.add(currentNode);

		// System.out.println("current path size: " + currentPath.size());

		updateMemory(currentPath.size());
		incrementNodeCount();

		// System.out.println(currentNode);

		// base case: goal found
		if (currentNode.goalTest()) {
			// we found the goal! Add this node to the path
			LinkedList<SearchNode> path = new LinkedList<SearchNode>();
			path.add(currentNode);
			// currentPath.remove(currentNode);
			return path;
		}

		// base case: maximum depth reached
		if (depth == maxDepth) {
			currentPath.remove(currentNode);
			return null;
		}

		// recursive case
		ArrayList<SearchNode> successors = currentNode.getSuccessors();

		for (SearchNode s : successors) {
			// only visit the successor if it isn't on the current path

			if (!currentPath.contains(s)) {

				List<SearchNode> path = dfsrpc(s, currentPath, depth + 1,
						maxDepth);
				if (path != null) {
					// one of the successors of this node is on a path the to
					// a goal, so this node is also on this path to the goal.
					path.add(0, currentNode);
					// currentPath.remove(currentNode);
					return path;
				}
			}
		}

		// didn't find a path to goal through any successors of this node
		currentPath.remove(currentNode);
		return null;
	}

	protected void resetStats() {
		nodesExplored = 0;
		maxMemory = 0;
	}

	protected void printStats() {
		System.out.println("  Nodes explored during search:  "
				+ nodesExplored);
		System.out.println("  Maximum space usage during search "
				+ maxMemory);
	}

	protected void updateMemory(int currentMemory) {
		maxMemory = Math.max(currentMemory, maxMemory);
	}

	protected void incrementNodeCount() {
		nodesExplored++;
	}

}

