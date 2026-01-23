package us.lsi.alg.floyd.manual;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jgrapht.GraphPath;

import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydEdge;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.graphs.SimpleEdge;
import us.lsi.graphs.alg.PDMC;
import us.lsi.graphs.alg.PD.PDType;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.graphs.alg.PDMC.Search;
import us.lsi.hypergraphs.GraphTree;

public class FloydPDBU implements Search<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> {
	
	public FloydVertex startVertex;
	public PDMC<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> pdmc; 
	
	public static FloydPDBU of(FloydVertex startVertex) {
		return new FloydPDBU(startVertex);
	}
	
	private FloydPDBU(FloydVertex startVertex) {
		this.pdmc = PDMC.of(this,PDType.Min);
		this.startVertex = startVertex;	
	}
	
	public Map<FloydVertex,Sp<Boolean,FloydEdge>> search(){
		Map<FloydVertex,Sp<Boolean,FloydEdge>> memory = new HashMap<>();
		search(startVertex,memory);
		return memory;
	}
	
	public Sp<Boolean,FloydEdge> search(FloydVertex vertex, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		Integer n = FloydVertex.n;
		Sp<Boolean,FloydEdge> r = null;
		for (int k = n; k >= 0; k--) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					r = null;
					FloydVertex actual = FloydVertex.of(i, j, k);
					if (memory.containsKey(actual)) {
						r = memory.get(actual);
					} else if (actual.isBaseCase()) {
						r = null;
						Double w = actual.baseCaseWeight();
						if (w != null) r = Sp.of(w);
						memory.put(actual, r);
					} else {
						r = this.pdmc.vertexSpBU(actual, memory);
						memory.put(actual, r);
					}
				}
			}
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
		
		FloydPDBU a =FloydPDBU.of(start);
		
		Map<FloydVertex, Sp<Boolean,FloydEdge>> r = a.search();
		
		GraphTree<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> tree = 
				GraphTree.graphTree(start,r);
				
		
		System.out.println(tree.solution().getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}
