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

public class MochilaPDBU implements Search<MochilaVertexPD, MochilaEdgePD, Integer, Multiset<ObjetoMochila>> {

	public MochilaVertexPD startVertex;
	public PDMC<MochilaVertexPD, MochilaEdgePD, Integer, Multiset<ObjetoMochila>> pdmc;

	public static MochilaPDBU of() {
		return new MochilaPDBU(MochilaVertexPD.of());
	}

	private MochilaPDBU(MochilaVertexPD startVertex) {
		this.pdmc = PDMC.of(this,PDType.Max);
		this.startVertex = startVertex;
	}

	public Map<MochilaVertexPD, Sp<Integer, MochilaEdgePD>> search() {
		Map<MochilaVertexPD, Sp<Integer, MochilaEdgePD>> memory = new HashMap<>();
		search(this.startVertex, memory);
		return memory;
	}

	public Sp<Integer, MochilaEdgePD> search(MochilaVertexPD a,
			Map<MochilaVertexPD, Sp<Integer,MochilaEdgePD>> memory) {
		Integer n = MochilaVertexPD.n;
		Sp<Integer, MochilaEdgePD> r = null;
		for (int index = n; index >=0; index--) {
			for (int cr = 0; cr <= MochilaVertexPD.capacidadInicial; cr++) {
				r = null;
				MochilaVertexPD actual = MochilaVertexPD.of(index, cr);
				if (memory.containsKey(actual)) {
					r = memory.get(actual);
				} else if (actual.isBaseCase()) {
					Double w = actual.baseCaseWeight();
					if (w != null) r = Sp.of(w);
					memory.put(actual, r);
				} else {
					r = this.pdmc.vertexSpBU(actual, memory);
					memory.put(actual, r);
				}
			}
		}
		return r;
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.of("en", "US"));
		DatosMochila.iniDatos("ficheros/mochila/objetosMochila.txt");
		MochilaVertexPD.capacidadInicial = 78;

		MochilaVertexPD start = MochilaVertexPD.of();

		MochilaPDBU a = MochilaPDBU.of();

		Map<MochilaVertexPD, Sp<Integer, MochilaEdgePD>> r = a.search();
		
		GraphTree<MochilaVertexPD,MochilaEdgePD,Integer,Multiset<ObjetoMochila>> tree = 
				GraphTree.graphTree(start,r);
		
		System.out.println(tree.solution());
		
		System.out.println(MochilaVertexPD.valor(tree.solution()));

//		System.out.println(r.entrySet().stream()
//				.map(x->x.toString())
//				.collect(Collectors.joining("\n")));

//		GraphTree<MochilaVertexPD, MochilaEdgePD, Integer, Multiset<ObjetoMochila>> tree = 
//				GraphTree.graphTree(start,r);
//
//		System.out.println(tree.solution());
//		
//		System.out.println(MochilaVertexPD.valor(tree.solution()));
	}

}
