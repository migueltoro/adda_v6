package us.lsi.p3.ej_3;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import us.lsi.gurobi.GurobiLp;
import us.lsi.gurobi.GurobiSolution;
import us.lsi.p3.ej_3.DatosAlumnos.Alumno;
import us.lsi.solve.AuxGrammar;

public class Ejemplo3PLE {
	public static List<Alumno> alumnos;
	
	public static Integer getNumAlumnos() {
		return alumnos.size();
	}
	public static Integer getNumGrupos() {
		return alumnos.get(0).afinidades().size();
	}
	public static Integer getTamGrupo() {
		return getNumAlumnos()/getNumGrupos();
	}
	public static Integer getAfinidad(Integer i, Integer j) {
		return alumnos.get(i).getAfinidad(j);
	}
	
	public static void ejemplo3_model() throws IOException {
		DatosAlumnos.iniDatos("ficheros/p3/ejemplo3_3.txt");
		
		alumnos = DatosAlumnos.getAlumnos();
		
		AuxGrammar.generate(Ejemplo3PLE.class,"modelos/ejemplo3.lsi","gurobi_models/ejemplo3.lp");
		Optional<GurobiSolution> solution = GurobiLp.gurobi("gurobi_models/ejemplo3.lp");
		if (solution.isPresent()) {
			Locale.setDefault(Locale.of("en", "US"));
			System.out.println(solution.get().toString((s,d)->d>0.));
		} else {
			System.out.println("\n\n*****Modelo sin solución****");
		}
	}
	
	public static void main(String[] args) throws IOException {	
		ejemplo3_model();
	}

}
