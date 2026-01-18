package us.lsi.alg.floyd.manual;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.graph.GraphWalk;

import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.alg.floyd.manual.FloydPD.Sp;
import us.lsi.graphs.SimpleEdge;

public class FloydPDFunctional {
	
	private Map<FloydVertex,Sp> memory;
	public FloydVertex startVertex;
	
	public static FloydPDFunctional of(FloydVertex startVertex) {
		return new FloydPDFunctional(startVertex);
	}
	
	private FloydPDFunctional(FloydVertex startVertex) {
		this.startVertex = startVertex;
	}
	
	public Map<FloydVertex,Sp> search(){
		this.memory = new HashMap<>();
		search(this.startVertex,memory);
		return memory;
	}

	public static Sp edgeSp(FloydVertex actual, Boolean a, Integer nbn, List<Double> nbWeights) {
		return nbWeights.size() == nbn ? Sp.of(a,actual.edge(a).weight(nbWeights),actual.edge(a)): null;
	}
	
	public static Sp edgeSp(FloydVertex actual, Boolean a, Map<FloydVertex, Sp> memory) {
		List<FloydVertex> neighbors = actual.neighbors(a);
		return neighbors.stream()
				.map(v -> search(v, memory))
				.takeWhile(s -> s != null)
				.map(s -> s.weight().doubleValue())
				.collect(Collectors.collectingAndThen(Collectors.toList(),
						ls->edgeSp(actual,a,neighbors.size(),ls)));
	}
	
	public static Sp vertexSp(FloydVertex actual,Map<FloydVertex, Sp> memory) {
		return actual.actions().stream()
				.map(a -> edgeSp(actual, a, memory))
				.filter(sp -> sp != null)
				.min(Comparator.naturalOrder())
				.orElse(null);
	}

	private static Sp search(FloydVertex actual, Map<FloydVertex, Sp> memory) {
		Sp r = null;
		if (memory.containsKey(actual)) {
			r = memory.get(actual);
		} else if (actual.isBaseCase()) {
			Double w = actual.baseCaseWeight();
			if (w != null) r = Sp.of(w);
			memory.put(actual, r);
		} else {
			r = vertexSp(actual, memory);
			memory.put(actual, r);
		}
		return r;
	}
	
	public GraphWalk<Integer,SimpleEdge<Integer>> solucion(FloydVertex p, Map<FloydVertex,Sp> memory) {
		GraphWalk<Integer,SimpleEdge<Integer>> r;
		Sp sp = memory.get(p);
		if(sp.action() == null) {
			r = p.baseCaseSolution();
		}
		else {
			List<GraphWalk<Integer, SimpleEdge<Integer>>> b = 
					p.neighbors(sp.action()).stream().map(v->solucion(v,memory)).toList();
			r = p.solution(b);
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
		
		FloydPDFunctional a = FloydPDFunctional.of(start);
		
		Map<FloydVertex, Sp> r = a.search();
		
		
		System.out.println(a.solucion(start, r).getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}

