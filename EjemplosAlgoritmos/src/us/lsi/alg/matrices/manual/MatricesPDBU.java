package us.lsi.alg.matrices.manual;



import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import us.lsi.alg.matrices.DatosMatrices;
import us.lsi.alg.matrices.MatrixEdge;
import us.lsi.alg.matrices.MatrixVertex;
import us.lsi.graphs.alg.PDMC;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.graphs.alg.PDMC.Search;
import us.lsi.hypergraphs.GraphTree;


public class MatricesPDBU implements Search<MatrixVertex,MatrixEdge,Integer,String> {
	
	public MatrixVertex startVertex;
	public PDMC<MatrixVertex,MatrixEdge,Integer,String> pdmc; 
	
	public static MatricesPDBU of(MatrixVertex startVertex) {
		return new MatricesPDBU(startVertex);
	}
	
	private MatricesPDBU(MatrixVertex startVertex) {
		this.pdmc = PDMC.of(this);
		this.startVertex = startVertex;	
	}
	
	
	public Map<MatrixVertex,Sp<Integer,MatrixEdge>> search(){
		Map<MatrixVertex,Sp<Integer,MatrixEdge>> memory = new HashMap<>();
		search(this.startVertex,memory);
		return memory;
	}
	
	public Sp<Integer, MatrixEdge> search(MatrixVertex vertex, Map<MatrixVertex, Sp<Integer, MatrixEdge>> memory) {
		Integer n = DatosMatrices.n;
		Sp<Integer, MatrixEdge> r = null;
		for (int t = 0; t <= n; t++) {
			for (int i = 0; i <= n; i++) {
				if (i + t > n) continue;
				Integer j = i+t;
				r = null;
				MatrixVertex actual = MatrixVertex.of(i, j);
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
		
		DatosMatrices.leeFichero("ficheros/matrices/matrices.txt");
		
		MatrixVertex start = MatrixVertex.of(0,DatosMatrices.n);
		
		MatricesPDBU a = MatricesPDBU.of(start);
		
		Map<MatrixVertex, Sp<Integer, MatrixEdge>> r = a.search();
		
		GraphTree<MatrixVertex,MatrixEdge,Integer,String> tree = 
				GraphTree.graphTree(start,r);
					
		System.out.println(tree.solution());
		
		System.out.println(tree.toString());
	}

}

