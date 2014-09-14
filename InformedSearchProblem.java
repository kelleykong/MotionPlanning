package assignment_robots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class InformedSearchProblem extends SearchProblem {
	
	public List<SearchNode> astarSearch() {
		
			resetStats();
			
			// visited = explored + frontier
			// use visited to get priority of some nodes in frontier
			Queue<SearchNode> frontier = new PriorityQueue<SearchNode>();
			HashMap<SearchNode, Double> visited = new HashMap<SearchNode, Double>();
			
//			HashSet<SearchNode> explored = new HashSet<SearchNode>();

			frontier.add(startNode);
			visited.put(startNode, startNode.priority());
				
			while (!frontier.isEmpty()) {
				incrementNodeCount();

				updateMemory(frontier.size() + visited.size());

				SearchNode currentNode = frontier.remove();
				
				// node has been explored or there is a node in frontier has less priority
				if (visited.containsKey(currentNode) && currentNode.priority() > visited.get(currentNode)) {
					System.out.println("shouldn't print " + currentNode);
					continue;
				}
					
				if (currentNode.goalTest()) {
					System.out.println("goalTest");

					return backchain(currentNode);
				}
				
//				explored.add(currentNode);

				ArrayList<SearchNode> successors = currentNode.getSuccessors();

//				 System.out.println("\ncurrent " + currentNode + "successors " + successors);
				System.out.println("\ncurrent" + currentNode);
				
				for (SearchNode node : successors) {
					// if not visited
//					if (!explored.contains(node) && !frontier.contains(node)) {
					if (!visited.containsKey(node)) {
//						System.out.print(node);
						frontier.add(node);
						visited.put(node, node.priority());
					}
					// if in frontier with less priority, replace it with node
					else if (frontier.contains(node)) {
						double priority = visited.get(node);
						if (node.priority() < priority) {
							System.out.println(node);
							frontier.remove(node);
							frontier.add(node);
							visited.remove(node);
							visited.put(node, node.priority());
						}
							
					}
				}
			}

			return null;
		}
	
	// backchain should only be used by bfs, not the recursive dfs
	protected List<SearchNode> backchain(SearchNode node) {

		LinkedList<SearchNode> solution = new LinkedList<SearchNode>();

		// chain through the visited hashmap to find each previous node,
		// add to the solution
		while (node != null) {
			solution.addFirst(node);
			node = node.getParent();
		}

		return solution;
	}
}
