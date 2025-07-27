package us.lsi.carmelo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

//import asignacion.AssignLocationTechniquesLevel2PLI;
import us.lsi.common.Files2;
import us.lsi.gurobi.GurobiLp;
import us.lsi.gurobi.GurobiSolution;
import us.lsi.solve_test.AuxGrammar;

public class AssignLocationPLIC {

	// Number of components
	private static Integer N;
	
	// Number of components
	public static Integer getN() {
		return N;
	}

	// Number of locations
	private static Integer L;
	
	// Number of locations
	public static Integer getL() {
		return L;
	}

	// Number of transportation techniques
	private static Integer T;
	
	// Number of transportation techniques
	public static Integer getT() {
		return T;
	}

	// Component i directly precedes component j
	private static Boolean[][] precedes;
	
	// Component i directly precedes component j
	public static Boolean precedes(Integer i, Integer j) {
		return precedes[i][j];
	}

	// Component i directly precedes component j
//	public static Boolean precedes(Integer i, Integer l, Integer j, Integer m, Integer t) {
//		return precedes[i][j];
//	}

	// if component i could be processed at location l 
	private static Boolean[][] alpha;
	
	// if component i could be processed at location l 
	public static Boolean getAlpha(Integer i, Integer l) {
		return alpha[i][l];
	}
	
	// if component i could not be processed at location l 
	public static Boolean getNotAlpha(Integer i, Integer l) {
		return !alpha[i][l];
	}

	// if component i could be transported using the transportation technique t   
	private static Boolean[][] gamma;
	
	// if component i could be transported using the transportation technique t    
	public static Boolean getGamma(Integer i, Integer t) {
		return gamma[i][t];
	}

	// if component i could not be transported using the transportation technique t    
	public static Boolean getNotGamma(Integer i, Integer t) {
		return !gamma[i][t];
	}

	// if transportation technique t is applied from location l to location m 
	// only one pair of locations for each transportation technique
	private static Boolean[][][] rho;
	
	// if transportation technique t is applied from location l to location m
	// only one pair of locations for each transportation technique
	public static Boolean getRho(Integer t, Integer l, Integer m) {
		return rho[t][l][m];
	}
	
	public static Boolean getNotRho(Integer t, Integer l, Integer m) {
		return !rho[t][l][m];
	}

	// Component i directly precedes component j
	public static Boolean precedesAndNotGetRho(Integer i, Integer l, Integer j, Integer m, Integer t) {
		return precedes[i][j] && !rho[t][l][m];
	}

	// if component i requires one of the feasible transportation techniques    
	private static Boolean[] TR;
	
	// if component i requires one of the feasible transportation techniques     
	public static Boolean getTR(Integer i) {
		return TR[i];
	}

	// total distance using transportation technique t
	private static Integer[] DIST;
	
	// total distance using transportation technique t
	public static Integer getDIST(Integer t) {
		return DIST[t];
	}

	// cost of transporting component i using transportation technique t 
	// (from location l to location m if ρtlm=1)
	private static Integer[][] TCOST;
	
	// cost of transporting component i using transportation technique t 
	// (from location l to location m if ρtlm=1)
	public static Integer getTCOST(Integer i, Integer t) {
		return TCOST[i][t];
	}
	
	// CO2 emissions of transporting component i using transportation technique t 
	// (from location l to location m if ρtlm=1) 
	private static Integer[][] TCO2;
	
	// CO2 emissions of transporting component i using transportation technique t 
	// (from location l to location m if ρtlm=1) 
	public static Integer getTCO2(Integer i, Integer t) {
		return TCO2[i][t];
	}
	
	// time of transporting component i using transportation technique t  
	// (from location l to location m if ρtlm=1) 
	private static Integer[][] TTIME;
	
	// time of transporting component i using transportation technique t  
	// (from location l to location m if ρtlm=1) 
	public static Integer getTTIME(Integer i, Integer t) {
		return TTIME[i][t];
	}
	
	// fixed cost of assigning component i at location l
	private static Integer[][] LCOST;
	
	// fixed cost of assigning component i at location l
	public static Integer getLCOST(Integer i, Integer l) {
		return LCOST[i][l];
	}

	// CO2 emissions of assigning component i at location l
	private static Integer[][] LCO2;
	
	// CO2 emissions of assigning component i at location l
	public static Integer getLCO2(Integer i, Integer l) {
		return LCO2[i][l];
	}

	// set-up time of assigning component i at location l
	private static Integer[][] LTIME;
	
	// set-up time of assigning component i at location l
	public static Integer getLTIME(Integer i, Integer l) {
		return LTIME[i][l];
	}

	
	public static void leeFichero(String file) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(file));
		} catch (FileNotFoundException e1) {
			throw new IllegalArgumentException("Fichero " + file + " no encontrado");
		}

		List<String> lineas = new ArrayList<>();

		while (sc.hasNext()) {
			String linea = sc.nextLine().replace("\t", " ").trim();
			if (!linea.isEmpty())
				lineas.add(linea);
		}

		sc.close();
		
		try {
			AssignLocationPLIC.N = lineas.indexOf("#locations") - lineas.indexOf("#components") - 1;
			AssignLocationPLIC.L = lineas.indexOf("#transportation_techniques") - lineas.indexOf("#locations") - 1;
			AssignLocationPLIC.T = lineas.indexOf("#precedes") - lineas.indexOf("#transportation_techniques") - 1;
			
			AssignLocationPLIC.precedes = new Boolean[N][N];
			for (int i = 0; i < N; i++) {
			    for (int j = 0; j < N; j++) {
			        precedes[i][j] = false;
			    }
			}

			List<String> precedesLines = lineas.subList(lineas.indexOf("#precedes")+1, lineas.indexOf("#alpha"));
			for (String line : precedesLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim())-1;
				int j = Integer.parseInt(datos[1].trim())-1;
				precedes[i][j] = true;
			}
			
			AssignLocationPLIC.alpha = new Boolean[N][L];
			for (int i = 0; i < N; i++) {
			    for (int j = 0; j < L; j++) {
			        alpha[i][j] = false;
			    }
			}

			List<String> alphaLines = lineas.subList(lineas.indexOf("#alpha")+1, lineas.indexOf("#gamma"));
			for (String line : alphaLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim())-1;
				int j = Integer.parseInt(datos[1].trim())-1;
				alpha[i][j] = true;
			}
			
			AssignLocationPLIC.gamma = new Boolean[N][T];
			for (int i = 0; i < N; i++) {
			    for (int j = 0; j < T; j++) {
			        gamma[i][j] = false;
			    }
			}

			List<String> gammaLines = lineas.subList(lineas.indexOf("#gamma")+1, lineas.indexOf("#rho"));
			for (String line : gammaLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim())-1;
				int j = Integer.parseInt(datos[1].trim())-1;
				gamma[i][j] = true;
			}
			
			AssignLocationPLIC.rho = new Boolean[T][L][L];
			for (int t = 0; t < T; t++) {
			    for (int l = 0; l < L; l++) {
				    for (int m = 0; m < L; m++) {
				    	rho[t][l][m] = false;
				    }
			    }
			}

			List<String> rhoLines = lineas.subList(lineas.indexOf("#rho")+1, lineas.indexOf("#tr"));
			for (String line : rhoLines) {
				String[] datos = line.split("\\s+");
				int t = Integer.parseInt(datos[0].trim())-1;
				int l = Integer.parseInt(datos[1].trim())-1;
				int m = Integer.parseInt(datos[2].trim())-1;
				rho[t][l][m] = true;
			}
			
			AssignLocationPLIC.TR = new Boolean[N];

			List<String> trLines = lineas.subList(lineas.indexOf("#tr")+1, lineas.indexOf("#dist"));
			for (String line : trLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int j = Integer.parseInt(datos[1].trim());
				TR[i] = j==1 ? true : false;
			}
			
			AssignLocationPLIC.DIST = new Integer[T];

			List<String> distLines = lineas.subList(lineas.indexOf("#dist")+1, lineas.indexOf("#tcost"));
			for (String line : distLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int j = Integer.parseInt(datos[1].trim());
				DIST[i] = j;
			}

			AssignLocationPLIC.TCOST = new Integer[N][T];
			for (int i = 0; i < N; i++) {
			    for (int t = 0; t < T; t++) {
			    	TCOST[i][t] = 0;
			    }
			}

			List<String> tcostLines = lineas.subList(lineas.indexOf("#tcost")+1, lineas.indexOf("#tco2"));
			for (String line : tcostLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int t = Integer.parseInt(datos[1].trim()) - 1;
				int x = Integer.parseInt(datos[2].trim());
				TCOST[i][t] = x;
			}

			AssignLocationPLIC.TCO2 = new Integer[N][T];
			for (int i = 0; i < N; i++) {
			    for (int t = 0; t < T; t++) {
			    	TCO2[i][t] = 0;
			    }
			}

			List<String> tco2Lines = lineas.subList(lineas.indexOf("#tco2")+1, lineas.indexOf("#ttime"));
			for (String line : tco2Lines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int t = Integer.parseInt(datos[1].trim()) - 1;
				int x = Integer.parseInt(datos[2].trim());
				TCO2[i][t] = x;
			}

			AssignLocationPLIC.TTIME = new Integer[N][T];
			for (int i = 0; i < N; i++) {
			    for (int t = 0; t < T; t++) {
			    	TTIME[i][t] = 0;
			    }
			}

			List<String> ttimeLines = lineas.subList(lineas.indexOf("#ttime")+1, lineas.indexOf("#lcost"));
			for (String line : ttimeLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int t = Integer.parseInt(datos[1].trim()) - 1;
				int x = Integer.parseInt(datos[2].trim());
				TTIME[i][t] = x;
			}

			AssignLocationPLIC.LCOST = new Integer[N][L];
			for (int i = 0; i < N; i++) {
			    for (int t = 0; t < L; t++) {
			    	LCOST[i][t] = 0;
			    }
			}

			List<String> lcostLines = lineas.subList(lineas.indexOf("#lcost")+1, lineas.indexOf("#lco2"));
			for (String line : lcostLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int t = Integer.parseInt(datos[1].trim()) - 1;
				int x = Integer.parseInt(datos[2].trim());
				LCOST[i][t] = x;
			}

			AssignLocationPLIC.LCO2 = new Integer[N][L];
			for (int i = 0; i < N; i++) {
			    for (int t = 0; t < L; t++) {
			    	LCO2[i][t] = 0;
			    }
			}

			List<String> lco2Lines = lineas.subList(lineas.indexOf("#lco2")+1, lineas.indexOf("#ltime"));
			for (String line : lco2Lines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int t = Integer.parseInt(datos[1].trim()) - 1;
				int x = Integer.parseInt(datos[2].trim());
				LCO2[i][t] = x;
			}

			AssignLocationPLIC.LTIME = new Integer[N][L];
			for (int i = 0; i < N; i++) {
			    for (int t = 0; t < L; t++) {
			    	LTIME[i][t] = 0;
			    }
			}

			List<String> ltimeLines = lineas.subList(lineas.indexOf("#ltime")+1, lineas.size());
			for (String line : ltimeLines) {
				String[] datos = line.split("\\s+");
				int i = Integer.parseInt(datos[0].trim()) - 1;
				int t = Integer.parseInt(datos[1].trim()) - 1;
				int x = Integer.parseInt(datos[2].trim());
				LTIME[i][t] = x;
			}

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"La composici�n del fichero no es correcta");
		}

	}
	
	public static void asignLocTec_model() throws IOException {
		AssignLocationPLIC.leeFichero("ficheros/datos3comp.txt");
		AuxGrammar.generate(AssignLocationPLIC.class,"ficheros/asignLoc_cost_filters_C.lsi","ficheros/asignLoc.lp");
		GurobiSolution solution = GurobiLp.gurobi("ficheros/asignLoc.lp").orElse(null);
		Locale.setDefault(Locale.of("en", "US"));
		if (solution!=null)
			System.out.println(solution.toString((s,d)->d>0.));
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		asignLocTec_model();

	}


}
