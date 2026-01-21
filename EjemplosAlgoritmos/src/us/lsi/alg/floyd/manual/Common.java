package us.lsi.alg.floyd.manual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import us.lsi.alg.floyd.FloydEdge;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.graphs.alg.PD.Sp;


public class Common {
	
	public static Sp<Boolean, FloydEdge> search(FloydVertex actual, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		return FloydPD.search(actual, memory);
	}
	
	public static Sp<Boolean,FloydEdge> edgeSp(FloydVertex actual, Boolean a, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		List<FloydVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (FloydVertex v : neighbors) {
			Sp<Boolean,FloydEdge> s = search(v, memory);
			if (s == null) break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Boolean,FloydEdge> vertexSp(FloydVertex actual,Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		List<Sp<Boolean,FloydEdge>> sps = new ArrayList<>();			
		for (Boolean a : actual.actions()) {
			Sp<Boolean,FloydEdge> spa = edgeSp(actual,a, memory);
			sps.add(spa);
		}
		Sp<Boolean,FloydEdge> r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}
	
	public static Sp<Boolean,FloydEdge> edgeSpBU(FloydVertex actual, Boolean a, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		List<FloydVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (FloydVertex v : neighbors) {
			Sp<Boolean,FloydEdge> sp = memory.get(v);
			if (sp == null) break;
			nbWeights.add(sp.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Boolean,FloydEdge> vertexSpBU(FloydVertex actual,Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		List<Sp<Boolean,FloydEdge>> sps = new ArrayList<>();			
		for (Boolean a : actual.actions()) {
			Sp<Boolean,FloydEdge> spa = edgeSp(actual,a, memory);
			sps.add(spa);
		}
		Sp<Boolean,FloydEdge> r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}
	
	public static Sp<Boolean,FloydEdge> edgeSpF(FloydVertex actual, Boolean a, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		List<FloydVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> search(v, memory))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue())
				.toList();
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Boolean,FloydEdge> vertexSpF(FloydVertex actual,Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		return actual.actions().stream()
				.map(a -> edgeSpF(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}
	
	public static Sp<Boolean,FloydEdge> edgeSpFBU(FloydVertex actual, Boolean a, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		List<FloydVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> memory.get(v))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue())
				.toList();
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Boolean,FloydEdge> vertexSpFBU(FloydVertex actual,Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		return actual.actions().stream()
				.map(a -> edgeSpFBU(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}


}
