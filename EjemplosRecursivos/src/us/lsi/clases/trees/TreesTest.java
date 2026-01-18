package us.lsi.clases.trees;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import us.lsi.common.Set2;
import us.lsi.common.String2;
import us.lsi.clase.palindromo.Palindromo;
import us.lsi.tiposrecursivos.Tree;
import us.lsi.tiposrecursivos.TEmpty;
import us.lsi.tiposrecursivos.TLeaf;
import us.lsi.tiposrecursivos.TNary;


public class TreesTest {
	
	public static <E> Integer sizeR(Tree<E> tree) {
		return switch(tree) {
		case TEmpty() -> 0 ; 
		case TLeaf(var lb) -> 1; 
		case TNary(var lb, var chd) -> 1 + chd.stream().mapToInt(x->sizeR(x)).sum(); 
		};	
	}
	
	public static <E> Integer sizeI(Tree<E> tree) {
		return (int)tree.byDepth().filter(t->!t.isEmpty()).count();
	}
	
	public static <E> Integer height(Tree<E> tree) {
		return switch(tree) {
		case TEmpty() -> -1; 
		case TLeaf(var lb) -> 0; 
		case TNary(var lb, var chd) -> 1+ chd.stream().mapToInt(x->x.height()).max().getAsInt(); 
		};	
	}
	
	public static <E> E minLabel(Tree<E> tree, Comparator<E> cmp) {
		return tree.byDepth()
		.map(tt -> tt.optionalLabel())
		.filter(x-> x.isPresent())
		.map(x->x.get())
		.min(cmp).get();
	}
	
	
	public static <E> Boolean containsLabelR(Tree<E> tree, E label) {
		return switch (tree) {
		case TEmpty() -> false;
		case TLeaf(var lb) -> lb.equals(label);
		case TNary(var lb, var chd) -> lb.equals(label)
				|| chd.stream().anyMatch(x -> containsLabelR(x, label));
		};
	}
	
	public static <E> Boolean containsLabelI(Tree<E> tree, E label)  {
		return tree.byDepth()
				.filter(t->!t.isEmpty())
				.anyMatch(tt->!tt.isEmpty() && tt.optionalLabel().get().equals(label));
	}
	
	public static <E> Boolean equals(Tree<E> t1, Tree<E> t2) {
		return switch(t1) {
		case TEmpty() when t2 instanceof TEmpty<E> s -> true;
		case TLeaf(var lb) when t2 instanceof TLeaf<E> s -> s.label().equals(lb);
		case TNary(var lb, var chd) when t2 instanceof TNary<E> s -> 
					lb.equals(s.label()) 
					&& chd.size() == s.childrenNumber()
					&& IntStream.range(0, chd.size())
					.allMatch(i -> chd.get(i).equals(s.child(i)));
		default -> false;
		};
	}
	
	public static <E> Tree<E> copy(Tree<E> tree) {
		return switch (tree) {
		case TEmpty() -> Tree.empty();
		case TLeaf(var lb) -> Tree.leaf(lb);
		case TNary(var lb, var chd) -> {
			List<Tree<E>> ch = chd.stream().map(x -> copy(x)).collect(Collectors.toList());
			yield Tree.nary(lb, ch);
		}
		};
	}
	
	public static <E> List<E> toListPostOrden(Tree<E> tree) {
		return switch(tree) {
		case TEmpty()  -> new ArrayList<>(); 
		case TLeaf(var lb) -> {
			List<E> r = new ArrayList<>();
			r.add(lb);
			yield r;
		}
		case TNary(var lb, var chd)  -> { 
			List<E> r = chd.stream().flatMap(x->toListPostOrden(x).stream()).collect(Collectors.toList());
			r.add(lb);	
			yield r;
		}
		};
	}
	
	public static <E> List<E> toListPostOrden2(Tree<E> tree) {
		List<E> res = new ArrayList<>();
		toListPostOrden2(tree, res);
		return res;
	}
	
	public static <E> void toListPostOrden2(Tree<E> tree, List<E> res) {
		switch (tree) {
		case TEmpty() -> {}
		case TLeaf(var lb) -> {res.add(lb);}
		case TNary(var lb, var chd) -> {
			for (Tree<E> t : chd)
				toListPostOrden2(t, res);
			res.add(lb);
		}
		};
	}
	
	public static Integer sumIfPredicate(Tree<Integer> tree, Predicate<Integer> predicate) {
		return switch (tree) {
		case TEmpty() -> 0;
		case TLeaf(var lb) -> predicate.test(lb) ? lb : 0;
		case TNary(var lb, var chd) -> (predicate.test(lb) ? lb: 0) + tree.children().stream().mapToInt(x -> sumIfPredicate(x, predicate)).sum();
		};
	}

	/**
	 * Evalúa de forma iterativa un predicado sobre los nodos de un árbol por niveles.
	 * Devuelve una lista de valores booleanos, donde cada elemento indica si todos los nodos de ese nivel cumplen el predicado.
	 *
	 * @param tree Árbol a analizar.
	 * @param pd Predicado a aplicar sobre los nodos.
	 * @param <E> Tipo de las etiquetas de los nodos.
	 * @return Lista de booleanos por nivel, true si todos los nodos del nivel cumplen el predicado, false en caso contrario.
	 */
	public static <E> List<Boolean> todosEnUnNivelCumplenI(Tree<E> tree, Predicate<Tree<E>> pd) {
		Map<Integer,Boolean> m = tree.byLevel()
				.collect(Collectors.groupingBy(p->p.level(),
						Collectors.collectingAndThen(
								Collectors.mapping(p->p.tree(),Collectors.toList()),
									ls->ls.stream().allMatch(pd))));
		return m.entrySet()
				.stream().sorted(Comparator.comparing(e->e.getKey()))
				.map(e->e.getValue()).toList();
	}
	
	/**
	 * Evalúa de forma recursiva un predicado sobre los nodos de un árbol por niveles.
	 * Devuelve una lista de valores booleanos, donde cada elemento indica si todos los nodos de ese nivel cumplen el predicado.
	 *
	 * @param tree Árbol a analizar.
	 * @param pd Predicado a aplicar sobre los nodos.
	 * @param <E> Tipo de las etiquetas de los nodos.
	 * @return Lista de booleanos por nivel, true si todos los nodos del nivel cumplen el predicado, false en caso contrario.
	 */
	public static <E> List<Boolean> todosEnUnNivelCumplenR(Tree<E> tree, Predicate<Tree<E>> pd) {
		List<List<Tree<E>>> niveles = new ArrayList<>();
		agrupaPorNivelR(tree, 0, niveles);
		List<Boolean> res = new ArrayList<>();
		for (List<Tree<E>> nivel : niveles) {
			res.add(nivel.stream().allMatch(pd));
		}
		return res;
	}

	public static <E> void agrupaPorNivelR(Tree<E> tree, int nivel, List<List<Tree<E>>> niveles) {
		if (niveles.size() <= nivel) {
			niveles.add(new ArrayList<>());
		}
		niveles.get(nivel).add(tree);
		switch (tree) {
		case TEmpty() -> {}
		case TLeaf(var lb) -> {}
		case TNary(var lb, var chd) -> {
			for (Tree<E> hijo : chd) {
				agrupaPorNivelR(hijo, nivel + 1, niveles);
			}
		}
		}
	}
	
	public static <E> void agrupaPorNivelI(Tree<E> tree, int nivel, List<List<Tree<E>>> niveles) {
		Map<Integer, List<Tree<E>>> m = tree.byLevel()
				.collect(Collectors.groupingBy(p -> p.level(), 
						Collectors.mapping(p -> p.tree(), Collectors.toList())));
		int maxNivel = m.keySet().stream().max(Comparator.naturalOrder()).get();
		for (int i = 0; i <= maxNivel; i++) {
			niveles.add(m.get(i));
		}	
	}
	
	public static <E> Map<Integer,List<Tree<E>>> agrupaPorNumeroDeHijos(Tree<E> tree) {
		return tree.byDepth().collect(Collectors.groupingBy(t->t.childrenNumber()));
	}

	/**
	 * Devuelve el conjunto de todos los caminos desde la raíz hasta las hojas de un árbol.
	 * Cada camino es una lista de etiquetas que representa la secuencia de nodos visitados.
	 *
	 * @param tree Árbol del que se obtienen los caminos.
	 * @param <E> Tipo de las etiquetas de los nodos.
	 * @return Conjunto de listas, cada una representa un camino desde la raíz a una hoja.
	 */
	public static <E> Set<List<E>> todosLosCaminos(Tree<E> tree) {
		Set<List<E>> res = Set2.of();
		todosLosCaminos(tree, new ArrayList<>(), res);
		return res;
	}
	
	public static <E> Set<List<E>> todosLosCaminos(Tree<E> tree, List<E> camino, Set<List<E>> res) {
		switch (tree) {
		case TEmpty() -> {}
		case TLeaf(var lb) -> {
			List<E> nuevoCamino = new ArrayList<>(camino);
			nuevoCamino.add(lb);
			res.add(nuevoCamino);
		}
		case TNary(var lb, var chd) -> {
			List<E> nuevoCamino = new ArrayList<>(camino);
			nuevoCamino.add(lb);
			for (Tree<E> t : chd) {
				todosLosCaminos(t, nuevoCamino, res);
			}
		}
		}
		return res;
	}

	public static <E> Set<List<E>> todosLosCaminos2(Tree<E> tree) {
		Set<List<E>> res = Set2.of();
		todosLosCaminos2(tree, new ArrayList<>(), res);
		return res;
	}

	public static <E> Set<List<E>> todosLosCaminos2(Tree<E> tree, List<E> camino, Set<List<E>> res) {
		switch (tree) {
		case TEmpty() -> {}
		case TLeaf(var lb) -> {
			camino.add(lb);
			List<E> nuevoCamino = new ArrayList<>(camino);
			res.add(nuevoCamino);
			camino.remove(camino.size()-1);
		}
		case TNary(var lb, var chd) -> {
			camino.add(lb);
			for (Tree<E> t : chd) {
				todosLosCaminos(t, camino, res);
			}
			camino.remove(camino.size()-1);
		}
		}
		return res;
	}
	
	public static String toString(List<Character> ls) {
		StringBuilder sb = new StringBuilder();
		for (Character c : ls) {
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static Set<String> caminosPalindromos(Tree<Character> tree) {
		Set<List<Character>> caminos = todosLosCaminos(tree);
		return caminos.stream()
				.map(pt -> toString(pt))
				.filter(s -> Palindromo.esPalindromo1(s))
				.collect(Collectors.toSet());
	}
	
	public static Set<String> caminosPalindromos2(Tree<Character> tree) {
		Set<List<Character>> caminos = todosLosCaminos2(tree);
		return caminos.stream()
				.map(pt -> toString(pt))
				.filter(s -> Palindromo.esPalindromo1(s))
				.collect(Collectors.toSet());
	}
	
	public static String toString2(List<Tree<Character>> ls) {
		StringBuilder sb = new StringBuilder();
		for (Tree<Character> t : ls) {
			sb.append(t.optionalLabel().orElse(null));
		}
		return sb.toString();
	}
	
	public static Set<String> caminosPalindromos3(Tree<Character> tree) {
		Stream<List<Tree<Character>>> caminos = tree.allPath();
		return caminos
				.map(pt -> toString2(pt))
				.filter(s -> Palindromo.esPalindromo1(s))
				.collect(Collectors.toSet());
	}
	
	public static void test1() {
		Tree<Integer> t7 = Tree.parse("34(2,27(_,2,3,4),9(8,_))").map(e->Integer.parseInt(e));	
		System.out.println(t7);
		System.out.println(minLabel(t7,Comparator.naturalOrder()));
		Predicate<Tree<Integer>> pd = t->t.isEmpty() || t instanceof TLeaf<Integer> s &&  s.label()%2==0
				|| t instanceof TNary<Integer> s &&  s.label()%2==0;
		String2.toConsole("%s",todosEnUnNivelCumplenR(t7,pd));
	}
	
	public static void test2() {
		Tree<Character> tree = Tree.parse("r(a(p(a(r)),d(a(_,r),i(o,_)),t),t,u(t(a)))").map(t->t.charAt(0));
		String2.toConsole("%s",tree);
		String2.toConsole("%s",caminosPalindromos(tree));
		String2.toConsole("%s",caminosPalindromos2(tree));
		String2.toConsole("%s",caminosPalindromos3(tree));
//		Tree<String> tree2 = Tree.parse("[2.;3.](a(p(a(r)),d(a(_,r),i(o,_)),t),t,u(t(a)))");
//		String2.toConsole("%s",tree2);
//		GraphColors.toDot(tree2,"ficheros/tree2.gv");
//		SimpleDirectedGraph<Tree<Character>, DefaultEdge> graph = tree.toGraph();
//		GraphColors.toDot(graph,"ficheros/tree5.gv");
//		System.out.println(tree);
	}
	
	public static void main(String[] args) {
		test2();
	}

}
