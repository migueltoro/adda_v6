package us.lsi.alg.floyd.manual;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.alg.floyd.manual.FloydPD.Sp;

public class FloydPDBottomUp {
	
	public static FloydVertex startVertex;
	
	public static FloydPDBottomUp of(FloydVertex startVertex) {
		return new FloydPDBottomUp(startVertex);
	}
	
	private FloydPDBottomUp(FloydVertex startVertex) {
		FloydPDBottomUp.startVertex = startVertex;
	}
	
	public static Map<FloydVertex,Sp> search(){
		Map<FloydVertex,Sp> memory = new HashMap<>();
		searchBU(memory);
		return memory;
	}
	
	public static void searchBU(Map<FloydVertex, Sp> memory) {
		Integer n = FloydVertex.n;
		for (int k = n; k >= 0; k--) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					Sp r = null;
					FloydVertex actual = FloydVertex.of(i, j, k);
					if (actual.isBaseCase()) {
						r = null;
						Double w = actual.baseCaseWeight();
						if (w != null) r = Sp.of(w);
						memory.put(actual, r);
					} else {
						r = FloydPD.vertexSp(actual, memory);
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
		
		Map<FloydVertex, Sp> r = search();
		
		System.out.println(FloydPD.solucion(start, r).getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}
