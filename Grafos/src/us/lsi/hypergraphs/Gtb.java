package us.lsi.hypergraphs;

import java.util.Set;

public record Gtb<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S>
		(V vertex) implements GraphTree<V, E, A, S> {

	@Override
	public String toString() {
		return String.format("(%s)", this.vertex());
	}

	public Set<E> allEdges() {
		return Set.of();
	}
}
