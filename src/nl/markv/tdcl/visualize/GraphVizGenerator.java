package nl.markv.tdcl.visualize;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import nl.markv.tdcl.data.Node;
import nl.markv.tdcl.parse.NodeGroup;

public class GraphVizGenerator {

	public static final String UP = "△";
	public static final String DOWN = "▽";

	//TODO @mark: http://viz-js.com/

	private static final class GraphBuilder {
		@Nonnull
		private StringBuilder text = new StringBuilder("");
		private int indent = 1;
		private int uniqueNameNr = 1;

		// [color="red:blue"]

		private void writeIndent() {
			for (int i = 0; i < indent; i++) {
				text.append("\t");
			}
		}

		@Nonnull
		String build() {
			return "digraph G {\n" + text.toString() + "}\n\n";
		}

		void startGroup(
				String label,
				boolean specialColor
		) {
			writeIndent();
			text.append("subgraph cluster_")
					.append(uniqueNameNr++)
					.append(" {\n");
			indent++;
			if (specialColor) {
				writeIndent();
				text.append("color=red;\n");
			}
			writeIndent();
			text.append("label = \"")
					.append(label)
					.append("\";\n");
		}

		void endGroup() {
			indent--;
			writeIndent();
			text.append("}\n\n");
		}

		void node(@Nonnull String name) {
			writeIndent();
			text.append(name)
					.append(";\n");
		}

	}


	@Nonnull
	public static String generateGraphViz(
			@Nonnull Set<Node> allNodes,
			@Nonnull Set<NodeGroup> groups
	) {
		var graphViz = new GraphBuilder();

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

		// Plot all the nodes that are in a group of more than one node.
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
				graphViz.node(node.name);
			}
			graphViz.endGroup();
		}

		//TODO @mark: TEMPORARY! REMOVE THIS!
		return graphViz.build();
	}

}
