package nl.markv.tdcl.visualize;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Dependency;
import nl.markv.tdcl.data.Node;
import nl.markv.tdcl.parse.NodeGroup;

public class GraphVizGenerator {

	public static final String UP = "△";
	public static final String DOWN = "▽";

	//TODO @mark: http://viz-js.com/

	@Nonnull
	public static String generateGraphViz(
			@Nonnull Set<Node> allNodes,
			@Nonnull Set<NodeGroup> groups,
			@Nonnull Set<Node> finalNodes
	) {
		var graphViz = new GraphVizCodeBuilder();

		// Set up mapping from node to group
		Map<Node, NodeGroup> nodeGroups = new HashMap<>();
		for (NodeGroup group : groups) {
			for (Node node : group.nodes()) {
				if (nodeGroups.containsKey(node)) {
					throw new IllegalStateException("Duplicate node");
				}
				nodeGroups.put(node, group);
			}
		}

		graphViz.comment("Groups with more than one member.");
		for (NodeGroup group : groups) {
			if (group.size() <= 1) {
				continue;
			}
			if (group.order().equals(NodeGroup.Order.Up)) {
				graphViz.startGroup(UP, false);
			} else if (group.order().equals(NodeGroup.Order.Down)) {
				graphViz.startGroup(DOWN, false);
			} else if (group.order().equals(NodeGroup.Order.Any)) {
				graphViz.startGroup("?", false);
			} else if (group.order().equals(NodeGroup.Order.Conflict) || group.hasConflict()) {
				graphViz.startGroup("!!!", true);
			}
			for (Node node : group.nodes()) {
				graphViz.node(node.name, finalNodes.contains(node), null);
			}
			graphViz.endGroup();
		}

		graphViz.comment("Self-referential nodes (groups with one member).");
		for (NodeGroup group : groups) {
			if (group.size() > 1) {
				continue;
			}
			Node node = group.nodes().iterator().next();
			if (group.order().equals(NodeGroup.Order.Up)) {
				graphViz.node(node.name, finalNodes.contains(node), UP);
			} else if (group.order().equals(NodeGroup.Order.Down)) {
				graphViz.node(node.name, finalNodes.contains(node), DOWN);
			} else if (group.order().equals(NodeGroup.Order.Conflict) || group.hasConflict()) {
				graphViz.node(node.name, finalNodes.contains(node), "!!!", "red");
			}
		}

		graphViz.comment("Ungrouped nodes that are not self-referential.");
		for (NodeGroup group : groups) {
			if (group.size() > 1) {
				continue;
			}
			Node node = group.nodes().iterator().next();
			if (group.order().equals(NodeGroup.Order.Any)) {
				graphViz.node(node.name, finalNodes.contains(node), null);
			}
		}

		graphViz.comment("Redundant nodes that are not needed for the final result.");
		for (Node extraNode : allNodes) {
			if (nodeGroups.containsKey(extraNode)) {
				continue;
			}
			graphViz.node(extraNode.name, finalNodes.contains(extraNode), null, "gray");
		}

		graphViz.comment("References to previous rows.");
		for (Node toNode : allNodes) {
			for (Dependency fromDependency : toNode.directDependencies) {
				if (fromDependency.direction == Dependency.Direction.Previous) {
					Node fromNode = fromDependency.node;
					graphViz.arrow(fromNode.name, toNode.name, "blue");
				}
			}
		}

		graphViz.comment("References to current rows.");
		for (Node toNode : allNodes) {
			for (Dependency fromDependency : toNode.directDependencies) {
				if (fromDependency.direction == Dependency.Direction.Current) {
					Node fromNode = fromDependency.node;
					graphViz.arrow(fromNode.name, toNode.name, "black");
				}
			}
		}

		graphViz.comment("References to next rows.");
		for (Node toNode : allNodes) {
			for (Dependency fromDependency : toNode.directDependencies) {
				if (fromDependency.direction == Dependency.Direction.Next) {
					Node fromNode = fromDependency.node;
					graphViz.arrow(fromNode.name, toNode.name, "orange");
				}
			}
		}

		//TODO @mark: legend?
//		graphViz.comment("Redundant nodes that are not needed for the final result.");

		//TODO @mark: TEMPORARY! REMOVE THIS!
		return graphViz.build();
	}
}
