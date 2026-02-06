package us.lsi.pli.sudoku;

import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import us.lsi.gurobi.GurobiLp;
import us.lsi.gurobi.GurobiSolution;
import us.lsi.solve.AuxGrammar;

public class Sudoku2PLI {
	
	public static List<String> partes(String s) {
		return Arrays.asList(s.split("_"));
	}
	
	
	public static List<Casilla> solucion(GurobiSolution solution){
		List<Casilla> ls = solution.values.entrySet().stream()
				.filter(e->!e.getKey().contains("$") && e.getValue() > 0)
				.map(e->partes(e.getKey()))
				.map(p->Casilla.of(Integer.parseInt(p.get(1)),
						Integer.parseInt(p.get(2)),
						Integer.parseInt(p.get(3)),
						true,
						false))
				.collect(Collectors.toList());
		return ls;
	}
//
//	public static Boolean fi(Integer f, Integer c, Integer t) {
//		return t == c/DatosSudoku2.nct + DatosSudoku2.nft*(f/DatosSudoku2.nft);
//	}
	
	public static Boolean isFixed(Integer f, Integer c, Integer v) {
		Casilla casilla = Casilla.of(f, c);
		Integer index = DatosSudoku2.casillasFijas.indexOf(casilla);
		if (index != -1) {
			Casilla c1 = DatosSudoku2.casillasFijas.get(index);
			return (c1.value().equals(v));
		} else
		return false;
	}
	
	public static Integer size() {
		return DatosSudoku2.n;
	}
	
	public static void sudoku() throws IOException {	
		String txt = "data/sudoku/sudoku6.txt";
		DatosSudoku2.leeFichero2(txt);
		System.out.println(SolucionSudoku.of(DatosSudoku2.casillas));
		AuxGrammar.generate(Sudoku2PLI.class, "modelos/sudoku3.lsi", "ficheros/sudoku3.lp");
		Optional<GurobiSolution> st = GurobiLp.gurobi("ficheros/sudoku3.lp");
		if (st.isPresent()) {
			Locale.setDefault(Locale.of("en", "US"));
			List<Casilla> lc = solucion(st.get());
			Collections.sort(lc,Comparator.comparing(c->c.p()));	
		
		Collections.sort(DatosSudoku2.casillas,Comparator.comparing(c->c.p()));	
		for(int i = 0; i < DatosSudoku2.numeroDeCasillas;i++) {
			if(!DatosSudoku2.casillas.get(i).isWithInitialValue())
				DatosSudoku2.casillas.set(i,lc.get(i));
		}
		System.out.println(SolucionSudoku.of(DatosSudoku2.casillas));
		} else {
			System.out.println("\n\n*****Modelo sin solución****");
		}
	}
	
	

	public static void main(String[] args) throws IOException {
		sudoku();
	}

}
