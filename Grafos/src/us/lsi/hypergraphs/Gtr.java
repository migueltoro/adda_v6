package us.lsi.hypergraphs;

import java.util.List;

public record Gtr<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S>
	(V vertex, A action, List<GraphTree<V, E, A, S>> targets) implements GraphTree<V, E, A, S> {

	
	@Override
	public String toString() {
		String ts = targets.stream().map(t -> t.toString()).reduce("", (a, b) -> a + "," + b);
		if (ts.length() > 0)
			ts = ts.substring(1);
		return String.format("(%s,%s)-->[%s]", this.vertex(), this.action(),this.targets());
	}
}
