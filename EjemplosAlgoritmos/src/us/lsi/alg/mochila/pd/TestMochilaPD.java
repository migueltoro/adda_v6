package us.lsi.alg.mochila.pd;


import java.util.Locale;
import java.util.Map;

import us.lsi.common.Multiset;
import us.lsi.graphs.alg.PD;
import us.lsi.graphs.alg.PD.PDType;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.GraphTree;
import us.lsi.hypergraphs.SimpleVirtualHyperGraph;
import us.lsi.mochila.datos.DatosMochila;
import us.lsi.mochila.datos.ObjetoMochila;

public class TestMochilaPD {

	public static void main(String[] args) {
		
		Locale.setDefault(Locale.of("en", "US"));
		DatosMochila.iniDatos("ficheros/mochila/objetosMochila.txt");
		MochilaVertexPD.capacidadInicial = 78;
		
		MochilaVertexPD p = MochilaVertexPD.of();
		
		System.out.println(p);				
		SimpleVirtualHyperGraph<MochilaVertexPD,MochilaEdgePD,Integer> graph3 = 
				SimpleVirtualHyperGraph.simpleVirtualHyperGraph(p);
		
		PD<MochilaVertexPD, MochilaEdgePD, Integer,Multiset<ObjetoMochila>> a = 
				PD.dynamicProgrammingSearch(graph3,PDType.Max);
		
//		a.withGraph = true;
		a.search();
		
		Map<MochilaVertexPD, Sp<Integer, MochilaEdgePD>> s = a.getSolutionsTree();
		
		GraphTree<MochilaVertexPD,MochilaEdgePD,Integer,Multiset<ObjetoMochila>> tree = 
				GraphTree.graphTree(p,s);

		System.out.println(tree.solution());
		
		System.out.println(MochilaVertexPD.valor(tree.solution()));
		
//		System.out.println(tree.vertices());
		
//		a.toDot("ficheros/floydPD.gv",
//				v->String.format("(%d,%d,%d)",v.i(),v.j(),v.k()),
//				e->e.action()?"Y":"N",
//				tree.vertices())

	}

}
