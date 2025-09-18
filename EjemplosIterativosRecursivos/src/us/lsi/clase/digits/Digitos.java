package us.lsi.clase.digits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import us.lsi.streams.Collectors2;
import us.lsi.streams.Stream2;

public class Digitos {
	
	public static List<Integer> digitos(Integer n, Integer b){
		List<Integer> r;
		if(n>b) {
			r = digitos(n/b,b);
			Integer d = n%b;
		    r.add(d);
		} else {
			r = new ArrayList<>();
			r.add(n);		}
		return r;	
	}
	
	public static List<Integer> digitosI(Integer n, Integer b){
		List<Integer> r = new ArrayList<>(); 
		while(n>b) {
			Integer d = n%b;
		    r.add(0,d);
			n = n/b;
		}
		r.add(0,n);
		return r;	
	}

	public static Integer numeroDeCeros(Integer n, Integer a){
		Integer b = 0;
		while(n>0) {
		     Integer d = n%a;
		     if(d == 0){
		    	 b = b+1;
		     }
		     n = n/a;
		} 
		return b;	
	}
	
	
	public static Long entero(List<Integer> digitos,Integer a){
		Long b = 0L;
		Integer i = 0;
		Integer n = digitos.size();
		while(n-i > 0){
			Integer d = digitos.get(i);
			b = b * a + d;
			i = i+1;
		} 
		return b;
	}

	public static Integer inverso(Integer n, Integer a){
		Integer b = 0;
		while(n > 0){
			int d =n%a;
			b = b * a + d;
			n = n/a;
		}
		return b;
	}
	
	/**
	 * Generates a list of digits of a given number in a specified base, 
	 * accumulating the digits in reverse order (from least significant to most significant).
	 *
	 * @param n The number to extract digits from.
	 * @param b The base to use for digit extraction.
	 * @return A list of digits of the number `n` in base `b`, accumulated from most significant to least significant.
	 */
	public static List<Integer> digitosF(Integer n, Integer b){
		Stream<Integer> s = Stream.iterate(n, x->x>0, x->x/b).map(x->x%b);
		return s.collect(Collectors2.toListLeft()); //Acumula en la lista por la izquierda
	}
	
	public static record H(Integer n, Integer a, Integer b) {
		
		public static H of(Integer n, Integer b) {
			return new H(n, 0, b);
		}
		
		public H nx() {
			Integer d = n%b;
			Integer a1 = a * b + d;
			return new H(n/b, a1, b); //acumulador de Horner, aplicada a los díitos de mayor a menor
		}
		
		public String toString() {
			return String.format("(%d,%d)", n, a);
		}
		
	}
	
	public static Integer enteroCondigitosInvertidos(Integer n, Integer b){
		return Stream.iterate(H.of(n, b), x->x.nx()).dropWhile(x -> x.n() > 0)
				.findFirst().get().a();
	}
	
	public static Long numberF(List<Integer> digits, Integer b){
		Stream<Integer> s1 = Stream.iterate(digits.size()-1, i->i >=0, i->i-1);
		Stream<Integer> s2 = Stream.iterate(1, p->p*b);
		LongStream s = Stream2.zip(s1,s2,(x,y)->digits.get(x)*y).mapToLong(x->x);
		return s.sum();
	}
	
	public static void test1() {
		Integer n = 234578977;
		Integer b = 10;
//		List<Integer> ls = digitos(n,a);
//		System.out.println("Digitos = "+ls);
//		ls = digitosI(n,a);
//		System.out.println("Digitos I = "+ls);
//		System.out.println("Ceros = "+numeroDeCeros(n,a));
//		System.out.println("Entero = "+n+","+entero(ls,a));
//		System.out.println("Entero = "+n+","+inverso(n,a));
//		System.out.println("Digitos = "+n+","+invierte(n,b));
		List<Integer> ls = digitosF(n,b);
		System.out.println("Número = "+n+","+ls);
		System.out.println("Número = "+numberF(ls,b));
		System.out.println("Número = "+enteroCondigitosInvertidos(n,b));	
	}
	
	public static void main(String[] args) throws IOException {
		test1();
	}

}
