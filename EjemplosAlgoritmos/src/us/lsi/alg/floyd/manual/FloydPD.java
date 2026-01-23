package us.lsi.alg.floyd.manual;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import org.jgrapht.GraphPath;


import us.lsi.alg.floyd.DatosFloyd;
import us.lsi.alg.floyd.FloydEdge;
import us.lsi.alg.floyd.FloydVertex;
import us.lsi.graphs.SimpleEdge;
import us.lsi.graphs.alg.PD.PDType;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.graphs.alg.PDMC;
import us.lsi.graphs.alg.PDMC.Search;
import us.lsi.hypergraphs.GraphTree;


public class FloydPD implements Search<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>>{
	
	public FloydVertex startVertex;
	public PDMC<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> pdmc; 
	
	public static FloydPD of(FloydVertex startVertex) {
		return new FloydPD(startVertex);
	}
	
	private FloydPD(FloydVertex startVertex) {
		this.pdmc = PDMC.of(this,PDType.Min);
		this.startVertex = startVertex;	
	}
	
	public Map<FloydVertex,Sp<Boolean,FloydEdge>> search(){
		Map<FloydVertex,Sp<Boolean,FloydEdge>> memory = new HashMap<>();
		search(this.startVertex,memory);
		return memory;
	}

	public Sp<Boolean,FloydEdge> search(FloydVertex actual, Map<FloydVertex, Sp<Boolean,FloydEdge>> memory) {
		Sp<Boolean,FloydEdge> r = null;
		if (memory.containsKey(actual)) {
			r = memory.get(actual);
		} else if (actual.isBaseCase()) {
			Double w = actual.baseCaseWeight();
			if (w != null) r = Sp.of(w);
			memory.put(actual, r);
		} else {
			r = this.pdmc.vertexSp(actual, memory);
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
		
		FloydPD a =FloydPD.of(start);
		
		Map<FloydVertex, Sp<Boolean,FloydEdge>> r = a.search();
		
		GraphTree<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> tree = 
				GraphTree.graphTree(start,r);
				
		
		System.out.println(tree.solution().getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList());
	}

}
