package us.lsi.solve_test;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import us.lsi.common.Files2;
import us.lsi.common.Preconditions;
import us.lsi.common.Trio;
import us.lsi.model_test.PLIModelLexer;
import us.lsi.model_test.PLIModelParser;




public class AuxGrammar {


	public static enum Type{INT,DOUBLE,BOOLEAN};
	
	public static String  lineaColumna(ParserRuleContext ctx) {
		Integer line = ctx.getStart().getLine();
		Integer columnStart = ctx.start.getCharPositionInLine();
		Integer columnEnd = ctx.stop.getCharPositionInLine();
		return String.format("Error en la línea %d, entre columnas %d-%d", line, columnStart, columnEnd);
	}
	
	public static Boolean assertion(ParserRuleContext ctx, String txt, Boolean condition) {
		Boolean r = true;
		if (!condition) {
			Integer line =0;
			Integer columnStart=0;
			Integer columnEnd=0;
			String txtError=txt;
			if (condition != null) {
				line = ctx.getStart().getLine();
				columnStart = ctx.start.getCharPositionInLine();
				columnEnd = ctx.stop.getCharPositionInLine();
				txtError = ctx.getText();
			}
			Trio<Integer, Integer, Integer> t = Trio.of(line, columnStart, columnEnd);
			if (!AuxGrammar.previousErrors.contains(t)) {
				AuxGrammar.previousErrors.add(t);
				AuxGrammar.nErrors = AuxGrammar.nErrors + 1;
				AuxGrammar.errors.append(String.format("\n%s: ** %s **.\n%s", lineaColumna(ctx), txtError, txt));
			}
			r = false;
		}
		return r;
	}
	
	public static Boolean notNull(ParserRuleContext ctx, Object idx) {
		Boolean r = true;
		if (idx == null) {
//			System.out.println("Error en la línea " + ctx.getStart().getLine() + " columna " + ctx.start.getCharPositionInLine() + " " + ctx.getText());
//			System.out.println(AuxGrammar.nErrors);
			Integer line = ctx.getStart().getLine();
			Integer columnStart = ctx.start.getCharPositionInLine();
			Integer columnEnd = ctx.stop.getCharPositionInLine();
			String txt = ctx.getText();
			Trio<Integer,Integer,Integer> t = Trio.of(line,columnStart,columnEnd);	
			if (!AuxGrammar.previousErrors.contains(t)) {
				AuxGrammar.previousErrors.add(t);
				AuxGrammar.nErrors = AuxGrammar.nErrors + 1;
				AuxGrammar.errors.append(String.format("\nError en la línea %d, entre columnas %d-%d: ** %s **.\nEl valor no puede ser null",
							line, columnStart, columnEnd, txt));
			}
			r = false;
		}
		return r;
	}
	
	public static Boolean isOneOfTypes(ParserRuleContext ctx, Class<?> type, Class<?>... requiredTypes) {
		Set<Class<?>> ts = Arrays.stream(requiredTypes).collect(Collectors.toSet());
		Boolean r = true;
		if (!ts.contains(type)) {
//			System.out.println("Error en la línea " + ctx.getStart().getLine() + " columna " + ctx.start.getCharPositionInLine() + " " + ctx.getText());
//			System.out.println(AuxGrammar.nErrors);
			Integer line = ctx.getStart().getLine();
			Integer columnStart = ctx.start.getCharPositionInLine();
			Integer columnEnd = ctx.stop.getCharPositionInLine();
			String txt = ctx.getText();
			Trio<Integer,Integer,Integer> tr = Trio.of(line,columnStart,columnEnd);	
			if (!AuxGrammar.previousErrors.contains(tr)) {
				AuxGrammar.previousErrors.add(tr);
				AuxGrammar.nErrors = AuxGrammar.nErrors + 1;
				AuxGrammar.errors.append(String.format(
					"\nError en la línea %d, entre columnas %d-%d: ** %s **.\nEl tipo %s no es uno de los tipos permitidos %s",
					line, columnStart, columnEnd, txt, type.getSimpleName(),
					Arrays.stream(requiredTypes).map(t -> t.getSimpleName()).collect(Collectors.joining(","))));
			}
			r = false;
		}
		return r;
	}
	
	public static Boolean isType(ParserRuleContext ctx, Class<?> type, Class<?> requiredType) {
		Boolean r = true;
		if (!type.equals(type)) {
			AuxGrammar.nErrors = AuxGrammar.nErrors + 1;
//			System.out.println("Error en la línea " + ctx.getStart().getLine() + " columna " + ctx.start.getCharPositionInLine() + " " + ctx.getText());
//			System.out.println(AuxGrammar.nErrors);
			Integer line = ctx.getStart().getLine();
			Integer columnStart = ctx.start.getCharPositionInLine();
			Integer columnEnd = ctx.stop.getCharPositionInLine();
			String txt = ctx.getText();
			Trio<Integer,Integer,Integer> tr = Trio.of(line,columnStart,columnEnd);	
			if (!AuxGrammar.previousErrors.contains(tr)) {
				AuxGrammar.previousErrors.add(tr);
				AuxGrammar.nErrors = AuxGrammar.nErrors + 1;
				AuxGrammar.errors.append(String.format(
					"\nError en la línea %d, entre columnas %d-%d: ** %s **.\nEl tipo %s no es uno de los tipos permitidos %s",
					line, columnStart, columnEnd, txt, type.toString(), requiredType.toString()));		
			}
			r = false;
		}
		return r;
	}
	
	public static Boolean isInteger(Object d) {
		Class<? extends Object> c = d.getClass();
		return c.equals(Integer.class); 
	}
	
	public static Boolean isDouble(Object d) {
		Class<? extends Object> c = d.getClass();
		return c.equals(Double.class); 
	}
	
	public static Boolean isBoolean(Object d) {
		Class<? extends Object> c = d.getClass();
		return c.equals(Boolean.class); 
	}
	
	public static Boolean isString(Object d) {
		Class<? extends Object> c = d.getClass();
		return c.equals(String.class); 
	}
	
	public static Boolean isListString(Object d) {
		Class<? extends Object> c = d.getClass();
		return c.equals(ListString.class); 
	}
	
	public static Boolean asBoolean(Object d) {
		Boolean r;
		Class<? extends Object> c = d.getClass();
		if(c.equals(Boolean.class)) {
			r = (Boolean) d;
		} else {
			throw new IllegalArgumentException(String.format("Tipo no permitido %s",c));
		}
		return r;
	}
	
	public static Integer asInteger(Object d) {
		Integer r = null;
		Class<? extends Object> c = d.getClass();
		if(c.equals(Integer.class)) {
			r = (Integer) d;
		} else {
			Preconditions.checkArgument(false,String.format("Tipo no es Integer %s",c));
		}
		return r;
	}

	
	public static Double asDouble(Object d) {
		Double r = null;
		Class<? extends Object> c = d.getClass();
		if(c.equals(Double.class)) {
			r = (Double) d;
		} else {
			Preconditions.checkArgument(false,String.format("Tipo no es Double %s",c));
		}
		return r;
	}
	
	public static String asString(Object d) {
		String r = null;;
		Class<? extends Object> c = d.getClass();
		if(c.equals(String.class)) {
			r = (String) d;
		} else {
			Preconditions.checkArgument(false,String.format("Tipo no es String %s",c));
		}
		return r;
	}
	
	public static List<String> asListString(Object d) {
		if(d == null) return null;
		List<String> r = null;
		Class<? extends Object> c = d.getClass();
		if(c.equals(ListString.class)) {
			r = (ListString) d;
		} else {
			Preconditions.checkArgument(false,String.format("Tipo no es List<String> %s",c));
		}
		return r;
	}
	
	public static List<Integer> asListInteger(Object d) {
		List<Integer> r = null;
		Class<? extends Object> c = d.getClass();
		if(c.equals(ListInteger.class)) {
			r = (ListInteger) d;
		} else {
			Preconditions.checkArgument(false,String.format("Tipo no es List<String> %s",c));
		}
		return r;
	}
	
	public static Type asType(String text) {
		Type rt = null;
		switch(text) {
		case "Integer": rt = Type.INT; break;
		case "Double": rt = Type.DOUBLE; break;
		case "Boolean": rt = Type.BOOLEAN; break;
		default: Preconditions.checkArgument(false,String.format("Tipo no conocido %s",text));
		}
		return rt;
	}
	
	public static Class<?> asTypeClass(AuxGrammar.Type type) {
		Class<?> rt = null;
		switch(type) {
		case INT: rt = Integer.class; break;
		case DOUBLE: rt = Double.class; break;
		case BOOLEAN: rt = Boolean.class; break;
		}
		return rt;
	}
	
	public static Object castInteger(Object a) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) r = a;
		if (AuxGrammar.isDouble(a)) {
			Double d = asDouble(a);
			r = (Integer) d.intValue();
		}
		return r;
	}
	
	public static Object castDouble(Object a) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			Integer d = asInteger(a);
			r = (Double) d.doubleValue();
		}
		if (AuxGrammar.isDouble(a)) r = a;
		return r;
	}
	
	public static Object sum(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) + AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) + AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) + AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) + AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object difference(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) - AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) - AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) - AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) - AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object minus(Object b) {
		Object r = null;
		if(AuxGrammar.isInteger(b))  r  =  - AuxGrammar.asInteger(b);
		else if(AuxGrammar.isDouble(b)) r  =  - AuxGrammar.asDouble(b);
		return r;
	}
	
	public static Object multiply(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) * AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) * AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) * AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) * AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object div(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) / AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) / AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) / AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) / AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object rest(Object a, Object b) {
		Object r = null;
		Preconditions.checkArgument(AuxGrammar.isInteger(a) && AuxGrammar.isInteger(b),"Operador no permitido"); 
		r  = AuxGrammar.asInteger(a) % AuxGrammar.asInteger(b);	
		return r;
	}
	
	public static Object le(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) <= AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) <= AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) <= AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) <= AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object lt(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) < AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) < AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) < AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) < AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object ge(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) >= AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) >= AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) >= AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) >= AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object gt(Object a, Object b) {
		Object r = null;
		if (AuxGrammar.isInteger(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asInteger(a) > AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asInteger(a) > AuxGrammar.asDouble(b);
		} else if(AuxGrammar.isDouble(a)) {
			if(AuxGrammar.isInteger(b))  r  = AuxGrammar.asDouble(a) > AuxGrammar.asInteger(b);
			else if(AuxGrammar.isDouble(b)) r  = AuxGrammar.asDouble(a) > AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object eq(Object a, Object b) {
		Object r = false;
		if (AuxGrammar.isInteger(a) && AuxGrammar.isInteger(b)) {
			r  = AuxGrammar.asInteger(a) == AuxGrammar.asInteger(b);
		} 
	    if(AuxGrammar.isDouble(a) && AuxGrammar.isDouble(b)) {
			r  = AuxGrammar.asDouble(a) == AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object ne(Object a, Object b) {
		Object r = false;
		if (AuxGrammar.isInteger(a) && AuxGrammar.isInteger(b)) {
			r  = AuxGrammar.asInteger(a) != AuxGrammar.asInteger(b);
		} 
	    if(AuxGrammar.isDouble(a) && AuxGrammar.isDouble(b)) {
			r  = AuxGrammar.asDouble(a) != AuxGrammar.asDouble(b);
		}
		return r;
	}
	
	public static Object and(Object a, Object b) {
		Object r = false;
		if (AuxGrammar.isBoolean(a) && AuxGrammar.isBoolean(b)) {
			r  = AuxGrammar.asBoolean(a) && AuxGrammar.asBoolean(b);
		} 
		return r;
	}
	
	public static Object or(Object a, Object b) {
		Object r = false;
		if (AuxGrammar.isBoolean(a) && AuxGrammar.isBoolean(b)) {
			r  = AuxGrammar.asBoolean(a) || AuxGrammar.asBoolean(b);
		} 
		return r;
	}
	
	public static String toString(Object[] types) {
		return Arrays.stream(types).map(t->t.toString()).collect(Collectors.joining(",","{","}"));
	}
	
	public static Boolean allSpaces(String s) {
		return IntStream.range(0,s.length()).boxed().allMatch(i->Character.isSpaceChar(s.charAt(i)));
	}
	
	
	public static List<String> constraints = new ArrayList<>();
	
	public static List<String> generalConstraints = new ArrayList<>();
	
	public static List<String> bounds = new ArrayList<>();
	
	public static List<String> dBinaries = new ArrayList<>();
	
	public static Integer nInts = 0;
	
	public static Integer nBinarys = 0;
	
	public static Integer nDBinarys = 0;
	
	public static Integer nFrees = 0;
	
	public static Integer nSemicontinous = 0;
	
	public static Integer nContinous = 0;
	
	public static Integer nErrors = 0;
	
	public static StringBuilder errors = new StringBuilder("\nErrores encontrados:\n");
	
	public static Set<Trio<Integer,Integer,Integer>> previousErrors = new HashSet<>();
	
	public static Map<String,Type> types = new HashMap<>();
	
	public static Map<String,Object> values = new HashMap<>();
	
	public static Map<String,Method> functions = new HashMap<>();
	
	public static Map<String,Type[]> parametersType = new HashMap<>();
	
	public static Map<String,Class<?>[]> parametersClass = new HashMap<>();
	
	public static Map<String,Type> resultType = new HashMap<>();
	
	private static String constraintName[] = {"a","b","c","d","e","f","g","h","p","q"};
	
	public static String constraintName(Integer index) {
		String s = index.toString();
		return IntStream.range(0,s.length()).boxed()
				.map(i->constraintName[Integer.parseInt(""+s.charAt(i))])
				.collect(Collectors.joining(""));			
	}
	
	public static Integer cn = 0;
	public static Integer constraintNumber = 0;	
	
	public static Class<?> dataClass;
	
	
	public static Object result(String name, Object... parameters) {
		Method m = functions.get(name);
		Preconditions.checkNotNull(m,String.format("M�todo %s no encontrado",name));
		Preconditions.checkNotNull(parameters,String.format("Parameters null"));	
		Object r = null;
		try {
//			System.out.println(name);
//			System.out.println(m);
//			System.out.println(Arrays.asList(parameters));
			r = m.invoke(null,parameters);
		} catch (IllegalAccessException e) {
			Preconditions.checkNotNull(false,
					String.format("IllegalAccessException en el m�todo %s con %s",
							name,AuxGrammar.toString(parameters)));
		} catch (IllegalArgumentException e) {
			Preconditions.checkNotNull(false,
					String.format("IllegalArgumentException en el m�todo %s con %s",
							name,AuxGrammar.toString(parameters)));
		} catch (InvocationTargetException e) {
			Preconditions.checkNotNull(false,
					String.format("InvocationTargetException en el m�todo %s con %s",
							name,AuxGrammar.toString(parameters)));
		}  catch (NullPointerException e) {
			Preconditions.checkNotNull(false,
					String.format("NullPointerException en el m�todo %s con %s",
							name,AuxGrammar.toString(parameters)));
		}
		Preconditions.checkNotNull(r,String.format("El resultado de invocar el m�todo %s con %s es null",
				name,AuxGrammar.toString(parameters)));
	    return r;
	}
	
	public static Method getMethod(String name, Type... parameters) {
		Integer n = parameters.length;
		Class<?> lc[] = Arrays.stream(parameters)
				.map(p->AuxGrammar.asTypeClass(p))
			    .collect(Collectors.toList())
			    .toArray(new Class<?>[n]);		
		Method m = null;
		try {
			m = AuxGrammar.dataClass.getDeclaredMethod(name,lc);
		} catch (NoSuchMethodException e) {
			Preconditions.checkArgument(false,String.format("M�todo no encontrado %s %s",
					name,AuxGrammar.toString(parameters)));
		} catch (SecurityException e) {
			Preconditions.checkArgument(false,String.format("Excepci�n de seguridad en %s %s",name,
					AuxGrammar.toString(parameters)));
		}	
		return m;
	}
	
	public static class ListString extends ArrayList<String> implements List<String> {
		public static ListString of() {
			return new ListString();
		}	
		public static ListString of(List<String> ls) {
			return new ListString(ls);
		}
		private static final long serialVersionUID = 1L;
		public ListString() {
			super();
		}
		public ListString(Collection<? extends String> c) {
			super(c);
		}	
	}
	
	public static class ListInteger extends ArrayList<Integer> implements List<Integer> {
		public static ListInteger of() {
			return new ListInteger();
		}	
		public static ListInteger of(List<Integer> ls) {
			return new ListInteger(ls);
		}
		private static final long serialVersionUID = 1L;
		public ListInteger() {
			super();
		}
		public ListInteger(Collection<? extends Integer> c) {
			super(c);
		}	
	}
	
	public static class Limits {
		public static Limits of(Integer li, Integer ls) {
			return new Limits(li,ls);
		}
		public final Integer li;
		public final Integer ls;		
		public Limits(Integer li, Integer ls) {
			super();
			this.li = li;
			this.ls = ls;
		}
		@Override
		public String toString() {
			return String.format("(%d,%d)",li,ls);
		}		
	}
	
//	private static String RE = "(?<left>.+) (?<binaryOP>[+|-]) (?<num>[+|-]?\\d\\d*\\.\\d\\d*) \\s*$";
	private static String RE1 = "(?<left>.+)(?<binaryOP> [+|-] )(?<num>[+|-]?\\d\\d*(\\.\\d*)?)$";
	private static Pattern pattern = Pattern.compile(RE1);
	
	public static String[] partes(String mensaje) {
		Matcher m = AuxGrammar.pattern.matcher(mensaje);
		String r[] = new String[2];
		if (m.find()) {
			String left = m.group("left");
			String bop = m.group("binaryOP");
			String num = m.group("num");
//			System.out.println(String.format("%s,%s,%s,",left,bop,num));
			Object right = null;
			switch (bop) {
			case " + ": right = !num.contains(".")?-Integer.parseInt(num):-Double.parseDouble(num); break;
			case " - ": right = !num.contains(".")?Integer.parseInt(num):Double.parseDouble(num); break;
			}
//			System.out.println(String.format("%s,%s,%s,%s",left,bop,num,right.toString()));
			r[0] = left;
			r[1] = right.toString();
		} else {
			r[0] = mensaje;
			r[1] = "0";
		}		
		return r;
	}
	
	public static void generate(Class<?> dataClass, String model, String outFile) throws IOException {
		AuxGrammar.dataClass = dataClass;
		PLIModelLexer lexer = new PLIModelLexer(CharStreams.fromFileName(model));
		PLIModelParser parser = new PLIModelParser(new CommonTokenStream(lexer));

		
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));
		String answer=null;
		try {
			ParseTree tree = parser.model();
			answer = asString(tree.accept(new PLIModelVisitorC()));
			String errorOutput = errContent.toString();
			if (errorOutput != null && !errorOutput.isEmpty()) {
				AuxGrammar.nErrors++;
				AuxGrammar.errors.append("\nError en Parser : " + errorOutput);
			}
		} catch (Exception e) {
			AuxGrammar.nErrors++;
			AuxGrammar.errors.append("\nError en Parser : "+e.getMessage());			
            e.printStackTrace();
        } finally {
        	System.setErr(originalErr);
        }		
		if (AuxGrammar.nErrors > 0) {
			AuxGrammar.errors.append("\n\n=======================\n\n");
			AuxGrammar.errors.append(String.format("El modelo %s contiene %d errores y no se compiló correctamente.\n",
					model, AuxGrammar.nErrors));
			System.out.println(AuxGrammar.errors.toString());
			System.exit(1);
		} else {
			System.out.println("\n\n======================\n\n");
			System.out.println("El modelo " + model + " fué compilado sin errores y el resultado se puso en " + outFile);
			System.out.println("\n\n======================\n\n"
					+ "Tenga en cuenta que el formato intermedio LP no distingue entre desigualdades estrictas y no estrictas en las restricciones "
					+ "\nPor lo que, por ejemplo, < y <= son equivalentes a <=. \n\n======================\n\n");
			Files2.toFile(answer, outFile);
		}
	}
	

}
