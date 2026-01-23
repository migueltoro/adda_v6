package us.lsi.alg.mochila.manual.pd;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import us.lsi.alg.mochila.pd.MochilaEdgePD;
import us.lsi.alg.mochila.pd.MochilaVertexPD;
import us.lsi.common.Multiset;
import us.lsi.graphs.alg.PDMC;
import us.lsi.graphs.alg.PD.PDType;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.graphs.alg.PDMC.Search;
import us.lsi.hypergraphs.GraphTree;
import us.lsi.mochila.datos.DatosMochila;
import us.lsi.mochila.datos.ObjetoMochila;

public class MochilaPD implements Search<MochilaVertexPD,MochilaEdgePD,Integer,Multiset<ObjetoMochila>>{
	
	public MochilaVertexPD startVertex;
	public PDMC<MochilaVertexPD,MochilaEdgePD,Integer,Multiset<ObjetoMochila>> pdmc; 
	
	public static MochilaPD of() {
		return new MochilaPD(MochilaVertexPD.of());
	}
	
	private MochilaPD(MochilaVertexPD startVertex) {
		this.pdmc = PDMC.of(this,PDType.Max);
		this.startVertex = startVertex;	
	}
	
	public Map<MochilaVertexPD,Sp<Integer,MochilaEdgePD>> search(){
		Map<MochilaVertexPD,Sp<Integer,MochilaEdgePD>> memory = new HashMap<>();
		search(this.startVertex,memory);
		return memory;
	}

	public Sp<Integer,MochilaEdgePD> search(MochilaVertexPD actual, Map<MochilaVertexPD, Sp<Integer,MochilaEdgePD>> memory) {
		Sp<Integer,MochilaEdgePD> r = null;
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
		DatosMochila.iniDatos("ficheros/mochila/objetosMochila.txt");
		MochilaVertexPD.capacidadInicial = 78;
		
		MochilaVertexPD start = MochilaVertexPD.of();
		
		MochilaPD a = MochilaPD.of();
		
		Map<MochilaVertexPD, Sp<Integer,MochilaEdgePD>> r = a.search();
		
		System.out.println(r);
		
		GraphTree<MochilaVertexPD,MochilaEdgePD,Integer,Multiset<ObjetoMochila>> tree = 
				GraphTree.graphTree(start,r);
				
		
		System.out.println(tree.solution());
		
		System.out.println(MochilaVertexPD.valor(tree.solution()));
	}

}
