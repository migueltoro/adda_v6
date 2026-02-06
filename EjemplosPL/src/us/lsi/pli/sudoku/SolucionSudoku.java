package us.lsi.pli.sudoku;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolucionSudoku {	
	
	public static SolucionSudoku of(List<Casilla> casillas) {
		return new SolucionSudoku(casillas);
	}
	
	public SolucionSudoku(List<Casilla> casillas) {
		super();
		this.casillas = casillas;
		this.map = casillas.stream()
				.collect(Collectors.toMap(cs->CasFC.of(cs.f(),cs.c()),cs->cs));
	}
	
	private List<Casilla> casillas;
	private Map<CasFC,Casilla> map;
	
	public static record CasFC(Integer f, Integer c) {
		public static CasFC of(Integer f, Integer c) {
			return new CasFC(f, c);
		}
	}
	
	public Casilla casilla(Integer p) {
		return this.casillas.get(p);
	}
	
	public Casilla casilla(Integer f, Integer c) {
		CasFC cs = CasFC.of(f, c);
		return map.get(cs);
	}
	
	
	private String fila(Integer f) {
		return IntStream.range(0, DatosSudoku2.n).boxed()
				.map(c -> this.casilla(f, c).stringValue())
				.collect(Collectors.joining(" "));
	}
	
	@Override
	public String toString() {
		return IntStream.range(0,DatosSudoku2.n)
			    .boxed()
			    .map(f->fila(f))
			    .collect(Collectors.joining("\n"));
	}
	
}
