package us.lsi.hypergraphs;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import us.lsi.common.Set2;
import us.lsi.graphs.alg.PD.Sp;

public sealed interface GraphTree<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
	permits Gtb, Gtr {

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		GraphTree<V, E, A, S> tb(V v) {
		return new Gtb<V, E, A, S>(v);
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		GraphTree<V, E, A, S> tr(V v,List<GraphTree<V, E, A, S>> targets) {
		return new Gtr<V, E, A, S>(v, targets);
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		GraphTree<V, E, A, S> graphTree(V vertex, Map<V, Sp<A, E>> tree) {
		GraphTree<V, E, A, S> r = null;
		Sp<A, E> sp = tree.get(vertex);
		if (vertex.isBaseCase()) {
			r = tb(vertex);
		} else {
			r = tr(vertex, sp.edge().targets().stream()
					.map(vt -> graphTree(vt, tree))
					.collect(Collectors.toList()));
		}
		return r;
	}

	public default S solution() {
		return switch (this) {
		case Gtb<V, E, A, S> tb -> tb.vertex().baseCaseSolution();
		case Gtr<V, E, A, S> tr -> tr.vertex().solution(
				tr.targets().stream().map(t -> t.solution()).collect(Collectors.toList()));
		};
	}

	public default Set<V> vertices() {
		return switch (this) {
		case Gtb<V, E, A, S> tb -> Set2.of(tb.vertex());
		case Gtr<V, E, A, S> tr -> Set2.<V>union(Set2.of(
					tr.vertex()),tr.targets().stream()
					.flatMap(t -> t.vertices().stream())
					.collect(Collectors.toSet()));             
		};
	}
	
	public default String toString2() {
		return switch (this) {
        case Gtb<V, E, A, S> tb -> String.format("(%s)", tb.vertex());
        case Gtr<V, E, A, S> tr -> {
            String ts = tr.targets().stream()
                    .map(t -> t.toString())
                    .collect(Collectors.joining(","));
            yield String.format("(%s --> %s)", tr.vertex(), ts);
        }
        };
	}

}
