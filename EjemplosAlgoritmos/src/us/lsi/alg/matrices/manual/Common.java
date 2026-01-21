package us.lsi.alg.matrices.manual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import us.lsi.alg.matrices.MatrixEdge;
import us.lsi.alg.matrices.MatrixVertex;
import us.lsi.graphs.alg.PD.Sp;


public class Common {
	
	public static Sp<Integer, MatrixEdge> search(MatrixVertex actual, Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		return MatricesPD.search(actual, memory);
	}
	
	public static Sp<Integer, MatrixEdge> edgeSp(MatrixVertex actual, Integer a, Map<MatrixVertex, Sp<Integer, MatrixEdge>> memory) {
		List<MatrixVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (MatrixVertex v : neighbors) {
			Sp<Integer, MatrixEdge> s = search(v, memory);
			if (s == null) break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Integer,MatrixEdge> vertexSp(MatrixVertex actual,Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		List<Sp<Integer,MatrixEdge>> sps = new ArrayList<>();			
		for (Integer a : actual.actions()) {
			Sp<Integer,MatrixEdge> spa = edgeSp(actual,a, memory);
			sps.add(spa);
		}
		Sp<Integer,MatrixEdge> r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}
	
	public static Sp<Integer, MatrixEdge> edgeSpBU(MatrixVertex actual, Integer a, Map<MatrixVertex, Sp<Integer, MatrixEdge>> memory) {
		List<MatrixVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = new ArrayList<>();
		for (MatrixVertex v : neighbors) {
			Sp<Integer, MatrixEdge> s = memory.get(v);
			if (s == null) break;
			nbWeights.add(s.weight().doubleValue());
		}
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Integer,MatrixEdge> vertexSpBU(MatrixVertex actual,Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		List<Sp<Integer,MatrixEdge>> sps = new ArrayList<>();			
		for (Integer a : actual.actions()) {
			Sp<Integer,MatrixEdge> spa = edgeSpBU(actual,a, memory);
			sps.add(spa);
		}
		Sp<Integer,MatrixEdge> r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}
	
	
	public static Sp<Integer,MatrixEdge> edgeSpF(MatrixVertex actual, Integer a, Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		List<MatrixVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> search(v, memory))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue())
				.toList();
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Integer,MatrixEdge> vertexSpF(MatrixVertex actual,Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		return actual.actions().stream()
				.map(a -> edgeSpF(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}
	
	public static Sp<Integer,MatrixEdge> edgeSpFBU(MatrixVertex actual, Integer a, Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		List<MatrixVertex> neighbors = actual.neighbors(a);
		Integer nbn = neighbors.size();
		List<Double> nbWeights = neighbors.stream()
				.map(v -> memory.get(v))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue())
				.toList();
		return nbWeights.size() == nbn ? 
				Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;

	}
	
	public static Sp<Integer,MatrixEdge> vertexSpFBU(MatrixVertex actual,Map<MatrixVertex, Sp<Integer,MatrixEdge>> memory) {
		return actual.actions().stream()
				.map(a -> edgeSpF(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}


}
