package us.lsi.pli.sudoku;


import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import us.lsi.common.Files2;


public class DatosSudoku2 {
	
	/**
	 * Numero de filas, columnas y valores
	 */
	public static Integer n = 9;
	
	/**
	 * Numero de filas de subtabla
	 */
	public static Integer nft = 3;
	
	/**
	 * Numero de columnas de subtabla
	 */
	public static Integer nct = 3;
	
	
	/**
	 * Numero de casillas
	 */
	public static Integer numeroDeCasillas = n*n;
	
	public static List<Casilla> casillas;
	public static List<Casilla> casillasFijas;
	
	public static List<Casilla> copyCasillas(List<Casilla> casillas) {
		return casillas.stream().map(c->c.copy()).collect(Collectors.toList());
	}
	
	/**
	 * @param nf Fichero de datos
	 * @post Inicializa las variables del tipo
	 */
	public static void leeFichero(String file) {
		DatosSudoku2.casillas = IntStream.range(0, DatosSudoku2.numeroDeCasillas).boxed()
				.map(p->Casilla.of(p))
				.collect(Collectors.toList());
		Files2.streamFromFile(file)
				.map(ln->Casilla.parse(ln))
				.forEach(c -> casillas.set(c.p(), c));
		casillasFijas = casillas.stream().filter(c -> c.definida()).toList();
	}
	
	
	/**
	 * @param nf Fichero de datos
	 * @post Inicializa las variables del tipo
	 */
	public static void leeFichero2(String file) {
        // 1) Leer la primera línea no vacía para decidir si hay cabecera
        Optional<String> firstLineOpt = Files2.streamFromFile(file)
        			.map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .findFirst();

        Boolean hasHeader = firstLineOpt
                 .map(l -> l.startsWith("#SIZES"))   
                 .orElse(false);

        Integer skipLines = 0;
        
        if (hasHeader) {
            // 2) Leer las 3 primeras líneas no vacías (cabecera)
            List<String> header = Files2.streamFromFile(file)
            		.map(String::trim)
                    .filter(l -> !l.isEmpty())
                    .limit(3)
                    .toList();

            // Tercera línea: #FIXED ...
            if (!header.get(2).startsWith("#FIXED")) {
                throw new IllegalArgumentException("Cabecera inválida: se esperaba #FIXED en la tercera línea.");
            }

            // Segunda línea: formato de tamaños (distinto a datos)
    		String[] sp = header.get(1).split(",");
    		DatosSudoku2.n = Integer.parseInt(sp[0].trim());
    		DatosSudoku2.nft = Integer.parseInt(sp[1].trim());
    		DatosSudoku2.nct = DatosSudoku2.n / DatosSudoku2.nft; 
    		DatosSudoku2.numeroDeCasillas = DatosSudoku2.n * DatosSudoku2.n; 

            skipLines = 3;
        }

		DatosSudoku2.casillas = IntStream.range(0, DatosSudoku2.numeroDeCasillas).boxed()
				.map(p->Casilla.of(p))
				.collect(Collectors.toList());
        
		Files2.streamFromFile(file)
				.skip(skipLines)
				.map(ln->Casilla.parse(ln))
				.forEach(c -> casillas.set(c.p(), c));
		casillasFijas = casillas.stream().filter(c -> c.definida()).toList();
	}
	
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.of("en", "US"));
		DatosSudoku2.leeFichero("data/sudoku/sudoku3.txt");
		SolucionSudoku s = SolucionSudoku.of(DatosSudoku2.casillas);
		System.out.println(s);
		System.out.println(s.casilla(8,8));
		System.out.println("_____________");
		for(int f = 0; f < DatosSudoku2.n;f++) {
			for(int c = 0; c < DatosSudoku2.n;c++) {
				System.out.println(s.casilla(f,c));
			}
		}
	}
}
