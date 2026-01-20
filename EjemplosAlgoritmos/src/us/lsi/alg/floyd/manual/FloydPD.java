package us.lsi.alg.floyd.manual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.jgrapht.GraphPath;


import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydEdge;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.graphs.SimpleEdge;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.GraphTree;


public class FloydPD {
	
	public static FloydVertex startVertex;
	
	public static FloydPD of(FloydVertex startVertex) {
		return new FloydPD(startVertex);
	}
	
	private FloydPD(FloydVertex startVertex) {
		FloydPD.startVertex = startVertex;
	}
	
	public static Map<FloydVertex,Sp<Boolean,FloydEdge>> search(){
		Map<FloydVertex,Sp<Boolean,FloydEdge>> memory = new HashMap<>();
		search(FloydPD.startVertex,memory);
		return memory;
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

	public static Sp<Boolean,FloydEdge> search(FloydVertex actual, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		Sp<Boolean,FloydEdge> r = null;
		if (memory.containsKey(actual)) {
			r = memory.get(actual);
		} else if (actual.isBaseCase()) {
			Double w = actual.baseCaseWeight();
			if (w != null) r = Sp.of(w);
			memory.put(actual, r);
		} else {
			r = vertexSpF(actual, memory);
			memory.put(actual, r);
		}
		return r;
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.of("en", "US"));
		
		DatosFloyd.datos();
		
		System.out.println(DatosFloyd.graph);
		System.out.println(DatosFloyd.graphI);
		
		Integer origen = DatosFloyd.graphI.index(v->v.nombre().equals("Sevilla"));
		Integer destino = DatosFloyd.graphI.index(v->v.nombre().equals("Almeria"));
		
		FloydVertex.graph = DatosFloyd.graphI;
		FloydVertex.n = DatosFloyd.graphI.vertexSet().size();
		
		FloydVertex start = FloydVertex.initial(origen,destino);
		
		FloydPD.of(start);
		
		Map<FloydVertex, Sp<Boolean,FloydEdge>> r = search();
		
		GraphTree<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> tree = 
				GraphTree.graphTree(start,r);
				
		
		System.out.println(tree.solution().getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}
