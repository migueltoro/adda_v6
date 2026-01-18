package us.lsi.alg.floyd.manual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;

import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydEdge;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.grafos.datos.Carretera;
import us.lsi.grafos.datos.Ciudad;
import us.lsi.graphs.SimpleEdge;

public class FloydPD {
	
	public static record Sp(Boolean action,Double weight,FloydEdge edge) implements Comparable<Sp> {
		
		public static Sp of(Boolean a, Double weight,FloydEdge edge) {
			return new Sp(a, weight,edge);
		}
		
		public static Sp of(Double weight) {
			return new Sp(null, weight,null);
		}
		
		@Override
		public int compareTo(Sp sp) {
			return this.weight.compareTo(sp.weight);
		}
	}
	
	public static FloydVertex startVertex;
	
	public static FloydPD of(FloydVertex startVertex) {
		return new FloydPD(startVertex);
	}
	
	private FloydPD(FloydVertex startVertex) {
		FloydPD.startVertex = startVertex;
	}
	
	public static Map<FloydVertex,Sp> search(){
		Map<FloydVertex,Sp> memory = new HashMap<>();
		search(FloydPD.startVertex,memory);
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
		List<Sp> sps = new ArrayList<>();			
		for (Boolean a : actual.actions()) {
			Sp spa = edgeSp(actual,a, memory);
			sps.add(spa);
		}
		Sp r = sps.stream().filter(s -> s != null).min(Comparator.naturalOrder()).orElse(null);
		return r;
	}

	public static Sp search(FloydVertex actual, Map<FloydVertex, Sp> memory) {
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
	
	public static GraphWalk<Integer,SimpleEdge<Integer>> solucion(FloydVertex p, Map<FloydVertex,Sp> memory) {
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
	
	public static Ciudad ciudad(Graph<Ciudad,Carretera> graph, String nombre) {
		return graph.vertexSet().stream().filter(c->c.nombre().equals(nombre)).findFirst().get();
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
		
		Map<FloydVertex, Sp> r = search();
		
		
		System.out.println(FloydPD.solucion(start, r).getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}
