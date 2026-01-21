package us.lsi.graphs.alg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.SimpleHyperEdge;
import us.lsi.hypergraphs.VirtualHyperVertex;

public class PDManualCommon {

	public static interface Search<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> {
		Sp<A, E> search(V actual, Map<V, Sp<A, E>> memory);
	}
	
	public static PDManualCommon.Search<?,?,?,?> fSearch;

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S>
		Sp<A, E> search(V actual, Map<V, Sp<A, E>> memory) {
	    @SuppressWarnings("unchecked")
	    Search<V, E, A, S> searchImpl = (Search<V, E, A, S>) fSearch;
	    return searchImpl.search(actual, memory);
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> edgeSp(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (V v : neighbors) {
			Sp<A, E> s = search(v, memory);
			if (s == null)
				break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> vertexSp(V actual, Map<V, Sp<A, E>> memory) {
		
		List<Sp<A, E>> sps = new ArrayList<>();
		for (A a : actual.actions()) {
			Sp<A, E> spa = edgeSp(actual, a, memory);
			sps.add(spa);
		}
		Sp<A, E> r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> edgeSpBU(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (V v : neighbors) {
			Sp<A, E> s = memory.get(v);
			if (s == null)
				break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> vertexSpBU(V actual, Map<V, Sp<A, E>> memory) {
		
		List<Sp<A, E>> sps = new ArrayList<>();
		for (A a : actual.actions()) {
			Sp<A, E> spa = edgeSpBU(actual, a, memory);
			sps.add(spa);
		}
		Sp<A, E> r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> edgeSpF(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> search(v, memory))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue()).toList();
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> vertexSpF(V actual, Map<V, Sp<A, E>> memory) {
		
		return actual.actions().stream()
				.map(a -> edgeSpF(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> edgeSpFBU(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> memory.get(v))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue()).toList();
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> 
		Sp<A, E> vertexSpFBU(V actual, Map<V, Sp<A, E>> memory) {
		
		return actual.actions().stream()
				.map(a -> edgeSpFBU(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}

}
