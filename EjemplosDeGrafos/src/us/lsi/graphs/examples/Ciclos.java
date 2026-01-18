package us.lsi.graphs.examples;


import java.util.List;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jgrapht.alg.interfaces.CycleBasisAlgorithm.CycleBasis;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleWeightedGraph;

import us.lsi.common.String2;
import us.lsi.grafos.datos.Carretera;
import us.lsi.grafos.datos.Ciudad;
import us.lsi.graphs.Graphs2;
import us.lsi.graphs.GraphsReader;

/**
 * Este método main realiza varias operaciones sobre un grafo de ciudades y
 * carreteras usando la librería JGraphT:
 *
 * Carga del grafo: Lee un grafo ponderado desde el archivo
 * ficheros/andalucia.txt, donde los vértices son objetos Ciudad y las aristas
 * son objetos Carretera con pesos basados en kilómetros.
 *
 * Completa el grafo: Crea un grafo completo explícito (gc) a partir del
 * original, asignando un peso alto (200000) a las aristas inexistentes y usando
 * un peso por defecto para nuevas carreteras.
 *
 * Aproximación al TSP: Usa el algoritmo TwoApproxMetricTSP para encontrar un
 * ciclo hamiltoniano aproximado (recorrido que pasa una vez por cada ciudad).
 * Imprime la lista de ciudades y las aristas del camino hallado.
 *
 * Ciclo euleriano: Comprueba si el grafo original es euleriano (existe un ciclo
 * que recorre cada arista exactamente una vez) usando HierholzerEulerianCycle e
 * imprime el resultado.
 * 
 * Conjunto de ciclos: Calcula una conjunto de ciclos del grafo (conjunto de ciclos
 * independientes) usando PatonCycleBase y la imprime como caminos en el grafo.
 *
 * En resumen, el código muestra cómo analizar y resolver problemas clásicos de
 * grafos (TSP, ciclos eulerianos, base de ciclos) sobre un grafo de ciudades y
 * carreteras, mostrando los resultados por consola.
 **/

public class Ciclos {
	

	public static void main(String[] args) {
		
		SimpleWeightedGraph<Ciudad,Carretera> graph =  
				GraphsReader.newGraph("ficheros/andalucia.txt",
						Ciudad::ofFormat, 
						Carretera::ofFormat,
						Graphs2::simpleWeightedGraph,
						Carretera::km);
		
		SimpleWeightedGraph<Ciudad, Carretera> gc = 
				Graphs2.explicitCompleteGraph(
						graph,
						200000.,
						Graphs2::simpleWeightedGraph,
						()->Carretera.of(10000.),
						Carretera::km);
		
		TwoApproxMetricTSP<Ciudad, Carretera> tsp = new  TwoApproxMetricTSP<>();
		List<Ciudad> r3 = tsp.getTour(gc).getVertexList();
		GraphWalk<Ciudad,Carretera> gw = new GraphWalk<>(gc,r3,0.);
		
		String2.toConsole("Hamiltonian");
		String2.toConsole(r3,"Ciudades en el camino");
		String2.toConsole(gw.getEdgeList(), "Aristas en el camino");	
		
		HierholzerEulerianCycle<Ciudad,Carretera> hc = new HierholzerEulerianCycle<>();
		Boolean r2 = hc.isEulerian(graph);
		
		String2.toConsole(r2.toString());
		
		var sc = new PatonCycleBase<Ciudad,Carretera>(graph);
		CycleBasis<Ciudad,Carretera> r = sc.getCycleBasis();
		
//		Set<GraphWalk2<Ciudad,Carretera>> sgw = 
//				r.getCycles()
//				 .stream()
//				 .map(x->new GraphWalk2<Ciudad,Carretera>(g,x))
//				 .collect(Collectors.toSet());
//		
//		sgw.stream().forEach(x->System.out.println(x.getWeight()+"==="+x.getLength()+"==="+x.getVertexList()));
		String2.toConsole(r.getCyclesAsGraphPaths(),"Ciclos");
	}
	
	

}
