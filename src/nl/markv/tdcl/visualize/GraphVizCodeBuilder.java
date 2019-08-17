package nl.markv.tdcl.visualize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A builder for GraphViz code.
 *
 * @see GraphVizGenerator
 */
public final class GraphVizCodeBuilder {

	@Nonnull
	private StringBuilder text = new StringBuilder("");
	private int indent = 1;
	private int uniqueNameNr = 1;

	private void writeIndent() {
		text.append("\t".repeat(indent));
	}

	@Nonnull
	String build() {
		return "digraph G {\n" + text.toString() + "}\n";
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

	void comment(@Nonnull String msg) {
		text.append("\n");
		writeIndent();
		text.append("/* ")
				.append(msg)
				.append(" */\n");
	}

	void node(@Nonnull String name, @Nullable String annotation) {
		node(name, annotation, null);
	}

	void node(@Nonnull String name, @Nullable String annotation, @Nullable String color) {
		writeIndent();
		text.append(name);
		if (annotation != null || color != null) {
			text.append(" [");
		}
		if (annotation != null) {
			text.append("label=\"").append(name).append(" ").append(annotation).append("\"");
		}
		if (annotation != null && color != null) {
			text.append(", ");
		}
		if (color != null) {
			text.append("color=").append(color);
		}
		if (annotation != null || color != null) {
			text.append("]");
		}
		text.append(";\n");
	}

	void arrow(@Nonnull String from, @Nonnull String to, @Nonnull String color) {
		writeIndent();
		text.append(from)
				.append(" -> ")
				.append(to)
				.append(" [color=")
				.append(color)
				.append("];\n");
	}
}
