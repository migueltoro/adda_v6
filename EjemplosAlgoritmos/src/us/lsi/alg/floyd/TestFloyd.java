package us.lsi.alg.floyd;


import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jgrapht.GraphPath;
import us.lsi.grafos.datos.Ciudad;
import us.lsi.graphs.SimpleEdge;
import us.lsi.graphs.alg.PD;
import us.lsi.graphs.alg.PD.PDType;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.GraphTree;
import us.lsi.hypergraphs.SimpleVirtualHyperGraph;

public class TestFloyd {
	
	public static void main(String[] args) {
		
		Locale.setDefault(Locale.of("en", "US"));
		
		DatosFloyd.datos();
		
		System.out.println(DatosFloyd.graph);
		System.out.println(DatosFloyd.graphI);
		
		Integer origen = DatosFloyd.graphI.index(v->v.nombre().equals("Sevilla"));
		Integer destino = DatosFloyd.graphI.index(v->v.nombre().equals("Almeria"));
		
		FloydVertex.graph = DatosFloyd.graphI;
		FloydVertex.n = DatosFloyd.graphI.vertexSet().size();
		FloydVertex p = FloydVertex.initial(origen,destino);
		
		SimpleVirtualHyperGraph<FloydVertex,FloydEdge,Boolean> graph3 = 
				SimpleVirtualHyperGraph.simpleVirtualHyperGraph(p);
		
		PD<FloydVertex, FloydEdge, Boolean,GraphPath<Integer,SimpleEdge<Integer>>> a = 
				PD.dynamicProgrammingSearch(graph3,PDType.Min);
		
		a.withGraph = true;
		a.search();
		
		Map<FloydVertex, Sp<Boolean, FloydEdge>> s = a.getSolutionsTree();
		
		GraphTree<FloydVertex,FloydEdge,Boolean,GraphPath<Integer,SimpleEdge<Integer>>> tree = 
				GraphTree.graphTree(p,s);
//		System.out.println(FloydVertex.solution(tree).getVertexList().stream().collect(Collectors.toList()));
//		System.out.println(tree);
		List<Ciudad> lc = tree.solution().getVertexList().stream()
				.map(i->DatosFloyd.graphI.vertex(i))
				.toList();
		System.out.println(tree.solution());
		System.out.println(lc);
		
		System.out.println(tree.vertices());
		
//		a.toDot("ficheros/floydPD.gv",
//				v->String.format("(%d,%d,%d)",v.i(),v.j(),v.k()),
//				e->e.action()?"Y":"N",
//				tree.vertices());
	}

}
