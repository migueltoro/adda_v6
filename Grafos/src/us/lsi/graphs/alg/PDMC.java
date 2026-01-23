package us.lsi.graphs.alg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import us.lsi.graphs.alg.PD.PDType;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.SimpleHyperEdge;
import us.lsi.hypergraphs.VirtualHyperVertex;

/**
 * Clase que aglutina elementos comunes a las implementaciones manuales de Programación Dinámica.

 */
public class PDMC<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S>{
	
	public static <V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> PDMC<V, E, A, S> of(
			Search<V, E, A, S> search,PDType type) {
		return new PDMC<V, E, A, S>(search,type);
	}
	
	private Search<V, E, A, S> search;
	private Comparator<Sp<A, E>> comparator = null;
	
	
	private PDMC(Search<V, E, A, S> search,PDType type) {
		this.search = search;
		if (type == PDType.Min) {
			this.comparator = Comparator.reverseOrder();
		} else {
			this.comparator = Comparator.naturalOrder();
		}
	}

	public static interface Search<V extends VirtualHyperVertex<V, E, A, S>, E extends SimpleHyperEdge<V, E, A>, A, S> {
		Sp<A, E> search(V actual, Map<V, Sp<A, E>> memory);
	}

	public Sp<A, E> search(V actual, Map<V, Sp<A, E>> memory) {
	    return search.search(actual, memory);
	}

	public Sp<A, E> edgeSp(V actual, A a, Map<V, Sp<A, E>> memory) {		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (V v : neighbors) {
			Sp<A, E> s = search(v, memory);
			if (s == null) break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public Sp<A, E> vertexSp(V actual, Map<V, Sp<A, E>> memory) {
		
		List<Sp<A, E>> sps = new ArrayList<>();
		for (A a : actual.actions()) {
			Sp<A, E> spa = edgeSp(actual, a, memory);
			sps.add(spa);
		}
		Sp<A, E> r = sps.stream().filter(s -> s != null).max(this.comparator).orElse(null);
		return r;
	}

	public Sp<A, E> edgeSpBU(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (V v : neighbors) {
			Sp<A, E> s = memory.get(v);
			if (s == null)break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public Sp<A, E> vertexSpBU(V actual, Map<V, Sp<A, E>> memory) {
		
		List<Sp<A, E>> sps = new ArrayList<>();
		for (A a : actual.actions()) {
			Sp<A, E> spa = edgeSpBU(actual, a, memory);
			sps.add(spa);
		}
		Sp<A, E> r = sps.stream().filter(s -> s != null).max(this.comparator).orElse(null);
		return r;
	}

	public Sp<A, E> edgeSpF(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> search(v, memory))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue()).toList();
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public Sp<A, E> vertexSpF(V actual, Map<V, Sp<A, E>> memory) {
		
		return actual.actions().stream()
				.map(a -> edgeSpF(actual, a, memory))
				.filter(sp -> sp != null)
				.max(this.comparator)
				.orElse(null);
	}

	public Sp<A, E> edgeSpFBU(V actual, A a, Map<V, Sp<A, E>> memory) {
		
		List<V> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> memory.get(v))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue()).toList();
		return nbWeights.size() == nbn ? Sp.of(a, actual.edge(a).weight(nbWeights), actual.edge(a)) : null;

	}

	public Sp<A, E> vertexSpFBU(V actual, Map<V, Sp<A, E>> memory) {
		
		return actual.actions().stream()
				.map(a -> edgeSpFBU(actual, a, memory))
				.filter(sp -> sp != null)
				.max(this.comparator)
				.orElse(null);
	}

	

}
