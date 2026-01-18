package us.lsi.tiposrecursivos;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Stream;

import us.lsi.colors.GraphColors;
import us.lsi.common.List2;

import us.lsi.streams.Stream2;

public class Trees {
	
	public static <E> Stream<Tree<E>> byDeph(Tree<E> tree) {
		return Stream2.ofIterator(DepthPathTree.of(tree));
	}
	
	public static <E> Stream<TreeLevel<E>> byLevel(Tree<E> tree) {
		return Stream2.ofIterator(BreadthPathTree.of(tree));
	}
	
	public static <E> Stream<List<Tree<E>>> allPath(Tree<E> tree,Boolean emptyIncluded) {
		return Stream2.ofIterator(AllPathTree.of(tree,emptyIncluded));
	}
	
	public static <E> Tree<E> copy(Tree<E> tree) {
		return switch (tree) {
		case TEmpty<E>  t -> Tree.empty();
		case TLeaf<E>  t -> Tree.leaf(t.label());
		case TNary<E>  t -> Tree.nary(t.label(),t.children().stream().map(x->Trees.copy(x)).toList());
		};
	}
	
	public static record TreeLevel<E>(Integer level, Tree<E> tree){
		public static <R> TreeLevel<R> of(Integer level, Tree<R> tree){
			return new TreeLevel<R>(level,tree);
		}
		@Override
		public String toString() {
			return String.format("(%d,%s)",this.level,this.tree);
		}
	}
	
	public static class DepthPathTree<E> implements Iterator<Tree<E>>, Iterable<Tree<E>> {
		
		public static <E> DepthPathTree<E> of(Tree<E> tree){
			return new DepthPathTree<E>(tree);
		}

		private Stack<Tree<E>> stack;
		
		public DepthPathTree(Tree<E> tree) {
			super();
			this.stack = new Stack<>();
			this.stack.add(tree);
		}

		@Override
		public Iterator<Tree<E>> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return !this.stack.isEmpty();
		}

		@Override
		public Tree<E> next() {
			Tree<E> actual = stack.pop();
			switch(actual) {
			case TNary<E> t: 
				for(Tree<E> v:List2.reverse(t.children())) {
					stack.add(v);
				}
				break;
			case TEmpty<E> t:break;
			case TLeaf<E> t:break;
			}
			return actual;
		}
		
	}
	
	public static class BreadthPathTree<E> implements Iterator<TreeLevel<E>>, Iterable<TreeLevel<E>> {
		
		public static <E> BreadthPathTree<E> of(Tree<E> tree){
			return new BreadthPathTree<E>(tree);
		}

		private Queue<TreeLevel<E>> queue;
		
		public BreadthPathTree(Tree<E> tree) {
			super();
			this.queue = new LinkedList<>();
			this.queue.add(TreeLevel.of(0,tree));
		}

		@Override
		public Iterator<TreeLevel<E>> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return !this.queue.isEmpty();
		}

		private static <E> List<TreeLevel<E>> children(TreeLevel<E> actual){
			return actual.tree().children().stream().map(t->TreeLevel.of(actual.level()+1,t)).toList();
		}
		
		@Override
		public TreeLevel<E> next() {
			TreeLevel<E> actual = queue.remove();
			switch(actual.tree()) {
			case TNary<E> t: 
				for(TreeLevel<E> v:children(actual)) {
					queue.add(v);
				}
				break;
			case TEmpty<E> t:break;
			case TLeaf<E> t:break;
			}
			return actual;
		}
		
	}
	
	public static class AllPathTree<E> implements Iterator<List<Tree<E>>>, Iterable<List<Tree<E>>> {

		public static record Frame<E>(Tree<E> tree, Boolean visited) {
			public static <R> Frame<R> of(Tree<R> tree, Boolean visited) {
				return new Frame<R>(tree, visited);
			}

			@Override
			public String toString() {
				return String.format("(%s,%b)", this.tree.isEmpty()?"_":this.tree.optionalLabel().get(), this.visited);
			}
		}

		public static <E> AllPathTree<E> of(Tree<E> tree,Boolean emptyIncluded) {
			return new AllPathTree<E>(tree,emptyIncluded);
		}

		private Stack<Frame<E>> stack;
		private List<Tree<E>> path;
		private List<Tree<E>> newPath;
		private Boolean emptyIncluded;

		public AllPathTree(Tree<E> tree,Boolean emptyIncluded) {
			super();
			this.stack = new Stack<>();
			this.stack.add(Frame.of(tree, false));
			this.path = new ArrayList<>();
			this.newPath = null;
			this.emptyIncluded = emptyIncluded;
			advance();
		}

		@Override
		public Iterator<List<Tree<E>>> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return this.newPath != null;
		}

		private void advance() {
			this.newPath = null;
			while (this.newPath == null && !this.stack.isEmpty()) {
				Frame<E> actual = stack.pop();
				Tree<E> tree = actual.tree;
				if (!actual.visited) {
					this.path.add(tree);
					this.stack.push(Frame.of(tree, true));
					switch (tree) {
					case TEmpty<E> t -> {
						if (this.emptyIncluded) {
							this.newPath = new ArrayList<>(this.path);
						}
					}
					case TLeaf<E> t -> {
						this.newPath = new ArrayList<>(this.path);
					}
					case TNary<E> t -> {
						for (Tree<E> v : t.children().reversed()) {
							this.stack.add(Frame.of(v, false));
						}
					}					
					}
				} else {
					this.path.remove(this.path.size() - 1);
				}
			}
		}

		@Override
		public List<Tree<E>> next() {
			List<Tree<E>> old = new ArrayList<>(this.newPath);
			advance();
			return old;
		}
	}
	
	public static void test0() {
		Tree<Integer> t1 = Tree.empty();
		Tree<Integer> t2 = Tree.leaf(2);
		Tree<Integer> t3 = Tree.leaf(3);
		Tree<Integer> t4 = Tree.leaf(4);
		Tree<Integer> t5 = Tree.nary(27,t1,t2,t3,t4);
		Tree<Integer> t6 = Tree.nary(39,t2,t5);
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t6);
		String ex = "39(2,27(/_,2,3,4))";
		Tree<String> t7 = Tree.parse(ex);
		System.out.println("-------------");
		System.out.println(t7);
		List<String> l1 = t7.byDepth()
				.map(t->switch(t) {
				case TEmpty<String> te -> "_";
				case TLeaf<String> tl -> tl.label();
				case TNary<String> tn -> tn.label();
				}
				).toList();
		System.out.println(l1);
		System.out.println("-------------");
		System.out.println(List2.reverse(List2.of(1,2,3,4,5,6,7,8,9)));
		Tree<String> t8 = t7.reverse();
		System.out.println(t8);
		System.out.println(Tree.parse("39(2.,27(/_,2,3,4),9(8.,/_))"));
	}
	

	public static void test3() {
		Tree<String> t1 = Tree.parse("39(2.,27(/_,2,3,4),9(8.,/_))");
		Tree<Double> t2 = t1.map(s->Double.parseDouble(s));
		Tree<String> t3 = Tree.parse("9(8.,/_)");
		Tree<Double> t4 = t3.map(s->Double.parseDouble(s));
		System.out.println(t2);
		System.out.println(t4);
	}
	
	public static void test4() {
		String ex = "39(2.,27(/_,2,3,4),9(8.,/_))";
		Tree<String> t7 = Tree.parse(ex);		
		System.out.println(t7);
		System.out.println("______________");
		System.out.println(t7);
		t7.byLevel().forEach(t->System.out.println(t));
	}
	
	public static void test5() {
		String ex = "4(2(1(0,/_),3),7(5(/_,6),10(9(8,/_),11(/_,12))))";
		Tree<String> t7 = Tree.parse(ex);		
		System.out.println(t7);
		System.out.println("-------------");
		System.out.println(t7);
		List<String> l1 = t7.byDepth()
				.map(t->switch(t) {
				case TEmpty<String> te -> "_";
				case TLeaf<String> tl -> tl.label();
				case TNary<String> tn -> tn.label();
				}
				).toList();
		System.out.println(l1);
		System.out.println("-------------");
	}
	
	public static void test6() {
		String ex = "Pepa/(Vera/,Antonio/)(2(1(0,/_),3),7(5(/_,6),10(9(8,/_),11(/_,12))))";
		Tree<String> t7 = Tree.parse(ex);		
		System.out.println(t7);
	}
	
	public static <E> List<E> toListLabels(List<Tree<E>> path) {
		return path.stream().map(t->t.optionalLabel().orElse(null)).toList();
	}
	
	public static void test7() {
		String ex = "4(2(1(0,/_),3),7(5(/_,6),10(9(8,/_),11(/_,12))))";
		Tree<String> t7 = Tree.parse(ex);
		GraphColors.toDot(t7,"ficheros/tree2.gv");
		System.out.println(t7);
		System.out.println("-------------");
		List<List<String>> l1 = allPath(t7,true)
				.map(t->toListLabels(t))
				.toList();
		System.out.println(l1.size());
		for (List<String> p : l1) {
			System.out.println(p);
		}
	}
	
	public static void test8() {
		Tree<String> t1 = Tree.empty();
		Tree<String> t2 = Tree.parse("/_");
		System.out.println(t1);
		System.out.println(t2);
		switch(t2) {
		case TEmpty<String> te -> {
			System.out.println("Es vacio");
		}
		case TLeaf<String> te -> {
            System.out.println("Es vacio");
		}
		case TNary<String> te -> {
            System.out.println("No es vacio");
        }
		}
	}

	public static void main(String[] args) {
		test7();
	}

}
