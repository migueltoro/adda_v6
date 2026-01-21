package us.lsi.alg.matrices.manual;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import us.lsi.alg.matrices.DatosMatrices;
import us.lsi.alg.matrices.MatrixEdge;
import us.lsi.alg.matrices.MatrixVertex;
import us.lsi.graphs.alg.PD.Sp;
import us.lsi.hypergraphs.GraphTree;


public class MatricesPD {

	public static MatricesPD of(MatrixVertex startVertex) {
		return new MatricesPD(startVertex);
	}
	
	private MatrixVertex startVertex;
	
	private MatricesPD(MatrixVertex startVertex) {
		this.startVertex = startVertex;
	}
	
	public Map<MatrixVertex, Sp<Integer, MatrixEdge>> search(){
		Map<MatrixVertex,Sp<Integer, MatrixEdge>> memory = new HashMap<>();
		search(this.startVertex,memory);
		return memory;
	}

	public static Sp<Integer, MatrixEdge> search(MatrixVertex actual, Map<MatrixVertex, Sp<Integer, MatrixEdge>> memory) {
		Sp<Integer,MatrixEdge> r = null;
		if (memory.containsKey(actual)) {
			r = memory.get(actual);
		} else if (actual.isBaseCase()) {
			Double w = actual.baseCaseWeight();
			if (w != null) r = Sp.of(w);
			memory.put(actual, r);
		} else {
			r = Common.vertexSpF(actual, memory);
			memory.put(actual, r);
		}
		return r;
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.of("en", "US"));
		
		DatosMatrices.leeFichero("ficheros/matrices/matrices.txt");
		
		MatrixVertex start = MatrixVertex.of(0,DatosMatrices.n);
		
		MatricesPD a = MatricesPD.of(start);
		
		Map<MatrixVertex, Sp<Integer, MatrixEdge>> r = a.search();
		
		GraphTree<MatrixVertex,MatrixEdge,Integer,String> tree = 
				GraphTree.graphTree(start,r);
					
		System.out.println(tree.solution());
	}

	
}
