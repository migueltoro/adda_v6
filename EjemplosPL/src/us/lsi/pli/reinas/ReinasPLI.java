package us.lsi.pli.reinas;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import us.lsi.gurobi.GurobiLp;
import us.lsi.gurobi.GurobiSolution;
import us.lsi.solve.AuxGrammar;

public class ReinasPLI {
	
	public static int n = 100;
	
	public static void reinas_gen() throws IOException {
		AuxGrammar.generate(ReinasPLI.class,"modelos/reinas_1.lsi","ficheros/reinas_1.lp");
	}
	
	public static void reinas_model_2() throws IOException {
		AuxGrammar.generate(ReinasPLI.class, "modelos/reinas_1.lsi", "ficheros/reinas_1.lp");
		Optional<GurobiSolution> solution = GurobiLp.gurobi("ficheros/reinas_1.lp");
		if (solution.isPresent()) {
			Locale.setDefault(Locale.of("en", "US"));
			System.out.println(solution.get().toString((s, d) -> !s.contains("$") && d > 0));
		} else {
			System.out.println("\n\n*****Modelo sin solución****");
		}
	}
	
	public static void main(String[] args) throws IOException {
		reinas_model_2();
	}

}
