package us.lsi.graphs.examples;



import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.HamiltonianCycleAlgorithm;
import org.jgrapht.alg.tour.HeldKarpTSP;
import org.jgrapht.graph.SimpleWeightedGraph;

import us.lsi.colors.GraphColors;
import us.lsi.colors.GraphColors.Color;
import us.lsi.colors.GraphColors.Style;
import us.lsi.common.String2;
import us.lsi.grafos.datos.Carretera;
import us.lsi.grafos.datos.Ciudad;
import us.lsi.graphs.Graphs2;
import us.lsi.graphs.GraphsReader;
import us.lsi.graphs.views.CompleteGraphView;

/**
 * Resuelve un problema de tour hamiltoniano
 * 
 * @author Miguel Toro
 * 
 * Este código construye y analiza un grafo de ciudades y carreteras, y resuelve el problema del ciclo hamiltoniano aproximado:
 * 
 * Carga del grafo:
 * 		Lee un grafo ponderado desde ficheros/andalucia.txt, donde los vértices son ciudades (Ciudad) y las aristas son carreteras (Carretera), usando los kilómetros como peso.
 * 		Vista de grafo completo:
 * 		Crea una vista completa del grafo original (CompleteGraphView), añadiendo aristas faltantes con un peso alto (1000 km).
 * 
 * Ciclo hamiltoniano:
 * 		Usa el algoritmo Held-Karp (HeldKarpTSP) para encontrar un ciclo hamiltoniano aproximado en el grafo completo.
 * 
 * Salida de resultados:
 * 		Imprime la lista de aristas del ciclo encontrado en consola.
 * 
 * Visualización:
 * 		Genera un archivo .gv para visualizar el grafo, resaltando en negrita las aristas que forman parte del ciclo hamiltoniano.
 * 
 * En resumen, el código resuelve y visualiza una aproximación al TSP (Traveling Salesman Problem) sobre un grafo de ciudades andaluzas.
 *
 */

public class SubGraphsAndViews {

	
	public static void main(String[] args) {
		SimpleWeightedGraph<Ciudad, Carretera> graph = GraphsReader.newGraph("ficheros/andalucia.txt", 
				Ciudad::ofFormat,
				Carretera::ofFormat, 
				Graphs2::simpleWeightedGraph,
				Carretera::km);	
		
		Graph<Ciudad, Carretera> graph2 = CompleteGraphView.of(graph,()->Carretera.of(1000.));
		
		HamiltonianCycleAlgorithm<Ciudad, Carretera> a = new HeldKarpTSP<>();
		GraphPath<Ciudad, Carretera> r = a.getTour(graph2);
			
		String2.toConsole(r.getEdgeList(), "Camino");	
		
		GraphColors.<Ciudad,Carretera>toDot(graph,"ficheros/andaluciaSpanningTree.gv",
				x->String.format("%s",x.nombre()),
				x->String.format("%.sf",x.km()),
				v->GraphColors.color(Color.black),
				e->GraphColors.styleIf(Style.bold,r.getEdgeList().contains(e)));
		
	}

}
