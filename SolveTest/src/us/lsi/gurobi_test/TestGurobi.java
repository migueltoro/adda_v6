package us.lsi.gurobi_test;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import us.lsi.gurobi.GurobiLp;
import us.lsi.gurobi.GurobiSolution;

public class TestGurobi {
	
	public static void test(String file) {
		Locale.setDefault(Locale.of("en", "US"));
		Optional<GurobiSolution> solution = GurobiLp.gurobi(file);
		if (solution.isPresent()) {
			GurobiSolution sl = solution.get();
			Locale.setDefault(Locale.of("en", "US"));
			System.out.println(sl.toString((s, d) -> d > 0.));

			System.out.println("\n\n\n\n");
			System.out.println(String.format("Objetivo : %.2f", sl.objVal));
			System.out.println("\n\n");
			System.out.println(sl.values.keySet().stream()
//				.filter(e->!e.contains("$"))
					.sorted().map(e -> String.format("%s == %.1f, %.1f, %.1f", 
							e, sl.values.get(e),
							sl.values.get(e) + 1, sl.values.get(e) - 1))
					.collect(Collectors.joining("\n")));
		} else {
			System.out.println("\n\n*****Modelo sin solución****");
		}
	}

	public static void main(String[] args) {
		test("ficheros/gurobi.lp");
	}

}
