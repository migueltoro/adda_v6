package us.lsi.hypergraphs;

public record Gtr<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S>
	(V vertex, A action) implements GraphTree<V, E, A, S> {
}
