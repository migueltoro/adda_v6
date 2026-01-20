package us.lsi.alg.floyd.manual;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jgrapht.GraphPath;

import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydEdge;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.graphs.SimpleEdge;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.GraphTree;

public class FloydPDBottomUp {
	
	public static FloydVertex startVertex;
	
	public static FloydPDBottomUp of(FloydVertex startVertex) {
		return new FloydPDBottomUp(startVertex);
	}
	
	private FloydPDBottomUp(FloydVertex startVertex) {
		FloydPDBottomUp.startVertex = startVertex;
	}
	
	public static Map<FloydVertex,Sp<Boolean,FloydEdge>> search(){
		Map<FloydVertex,Sp<Boolean,FloydEdge>> memory = new HashMap<>();
		search(memory);
		return memory;
	}
	
	public static void search(Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		Integer n = FloydVertex.n;
		for (int k = n; k >= 0; k--) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					Sp<Boolean,FloydEdge> r = null;
					FloydVertex actual = FloydVertex.of(i, j, k);
					if (memory.containsKey(actual)) {
						r = memory.get(actual);
					} else if (actual.isBaseCase()) {
						r = null;
						Double w = actual.baseCaseWeight();
						if (w != null) r = Sp.of(w);
						memory.put(actual, r);
					} else {
						r = Common.vertexSpF(actual, memory);
						memory.put(actual, r);
					}
				}
			}
		}
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
		
		Map<FloydVertex, Sp<Boolean,FloydEdge>> r = search();
		
		GraphTree<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> tree = 
				GraphTree.graphTree(start,r);
				
		
		System.out.println(tree.solution().getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}
