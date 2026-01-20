package us.lsi.hypergraphs;

import java.util.List;

public record Gtr<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S>
	(V vertex, A action, List<GraphTree<V, E, A, S>> targets) implements GraphTree<V, E, A, S> {

}
