package us.lsi.pli.equipo;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import us.lsi.gurobi.GurobiLp;
import us.lsi.gurobi.GurobiSolution;
import us.lsi.solve.AuxGrammar;

public class TestEquipoPL {
	
	public static Integer get(String s, Integer i) {
		String[] partes = s.split("_");
		return Integer.parseInt(partes[i]);
	}
	
	public static void equipo_model(String fichero) throws IOException {
		DatosEquipo.iniDatos(fichero);
		AuxGrammar.generate(DatosEquipo.class,"modelos/equipo.lsi","ficheros/equipo.lp");
		Optional<GurobiSolution> solution = GurobiLp.gurobi("ficheros/equipo.lp");
		if (solution.isPresent()) {
			Locale.setDefault(Locale.of("en", "US"));
			System.out.println(solution.get().values.keySet()
				.stream()
				.filter(k ->k.startsWith("x") && solution.get().values.get(k)==1)
				.sorted()
				.map(k->String.format("%s,%s,%.2f",k,solution.get().values.get(k),DatosEquipo.getR(get(k,1),get(k,2))))
				.collect(Collectors.joining("\n")));
		} else {
			System.out.println("\n\n*****Modelo sin solución****");
		}
	}

	public static void main(String[] args) throws IOException {	
		Locale.setDefault(Locale.of("en", "US"));
		equipo_model("ficheros/equipo1.txt");
		equipo_model("ficheros/equipo2.txt");
		equipo_model("ficheros/equipo3.txt");
	}

}
