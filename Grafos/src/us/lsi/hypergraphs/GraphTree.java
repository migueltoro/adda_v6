package us.lsi.hypergraphs;

import java.util.Map;
import java.util.Set;

import us.lsi.graphs.alg.PD.Sp;

public sealed interface GraphTree<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
	permits Gtb, Gtr {

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		GraphTree<V, E, A, S> tb(V v, Double weight) {
		return new Gtb<V, E, A, S>(v);
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		GraphTree<V, E, A, S> tr(V v, A a) {
		return new Gtr<V, E, A, S>(v, a);
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		GraphTree<V,E,A,S> optimalTree(V vertex,Map<V, Sp<A,E>> solutionsTree) {
		// TODO Auto-generated method stub
		return null;
	}

	public default S solution() {
		return null;
	}

	public default Set<V> vertices() {
		return null;
	}

}
