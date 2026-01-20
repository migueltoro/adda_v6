# Programación Dinámica

Para resolver un problema por esta técnica partimos de un conjunto de problemas que denominaremos *P*. Un problema concreto de este conjunto sea *p*. Cada problema tiene un conjunto de propiedades y dos problemas $p_1, p_2$, son iguales si sus propiedades son iguales. Cada problema tiene un tamaño dado por $t(p)$. Para cada problema tiene un conjunto finito $A_p$ de alternativas posibles. Una alternativa de estas sea $a$.

Para cada problema se define la función *Boolean b(P p)* que indica si el problema es un caso base o no.

Dada una alternativa $a$ y un problema $p$ quedan definidos un conjunto de subproblemas mediante la función *set(P) nx(P p, A a)*..

Dado un problema $p$ se trata de encontrar una propiedad *s* de tipo *S* definida por la función *S sl(P p)* que llamaremos su solución.

Para conseguir un algoritmo eficiente definimos el tipo *Sp<A,W,E>* cuyos valores son tripeltas
*(a,w,e)* que llamaremos soluciones parciales y representaremos sus valores por *sp*. Esta tripleta nos da la mejor alternativa $a$ de tipo *A* y el mejor valor $w$ de la propiedad buscada de la solución de tipo *W* y la hiperarista siguiendo esa alternativa.

A cada solución *s* de un problema $p$ podemos asociarle una tripleta *(a,w,e)*.

Dado un problema *p* la función *Sp<A,W,E> f(P p,)* encuentra el mejor valor de la solución parcial.

Dado un problema *p* y una alternativa $a$ la función *Sp<A,W,E> g(P p, A a)* encuentra el valor de la solución parcial si te toma la alternativa *a*.

El algoritmo de programación dinámica usa memoria. Tiene una formulación recursiva y otra iterativa *bottom-up*.
  

## Notación:

  

- Si un problema no tiene solución lo representamos por $\perp$

- $A_p$ es el conjunto de alternativas para el problema $p$

- La función $t(p)$ es un número entero no negativo que nos da el tamaño de un problema.

- La función *Boolean b(P p)* que indica si el problema es un caso base o no.

- Si p es un caso base su solución parcial es dada por la función *Sp<A,W,E> sbp(P p,)*.

- Si p es un caso base su solución es dada por la función *Sp<A,W,E> sb(P p,)*.

- La función $list(P) \ nx(P \ p, A \ a)$ define el conjunto de subproblemas para un problema dado $p$ y una alternativa $a$. Cada subproblema de $p$ tiene un tamaño menor que $p$. Si queremos especificar un problema dado lo haremos con un subíndice.

- Las funciones *sp.a(), sp.w(),sp.e()* nos dan la alternativa, el valor y la arista de una solución parcial *sp*

  

## Definiciones de las funciones

la función *f(p)* calcula la solución parcial asociada a un vértice

```math 
f(p) = \begin{cases} sbp(p) & b(p) \\
 \underset{a \in A_p}{\large M} \ g(p,a) & \neg b(p)
 \end{cases}
```
La función *g(p)* calcula la solución parcial asociada a un vértice si tomamos la alternativa *a*
```math
g(p,a) = \underset{h \in nx(p,a)}{\large CBS} \ f(h)
```

La función *sl(p)* calcula la solución asociada a un vértice conocidas las soluciones parciales de los mismos.

```math
sl(p) = \begin{cases} sb(p) & b(p) \\
 \underset{h \in nx(p,a(f(p)))}{\large CS} \ sl(h) & \neg b(p) 
 \end{cases}
```
## Propiedades de los operadores

  

- El operador $\underset{a \in A_p}{\large M}$ combina las soluciones parciales obtenidas siguiendo cada una de las alternativas. Filtra todas las soluciones nulas y devuelve solución nula si todas son nulas. Ejemplos son el operador *max*, y el *min*. Pero también podría ser el operador contador.

- El operador $\underset{h \in nx(p,a)}{\large CBS}$ combina las soluciones parciales de los hijos del problema *p* siguiendo la alternativa *a* usando las propiedades de *p*. Devuelve nulo si alguna solución lo es.

- El operador $\underset{h \in nx(p,f(p).a())}{\large CS}$ combina las soluciones de los hijos alcanzados por la alternativa óptima $f(p).a()$


## Ejemplos

  

### Mochila

  

- En este ejemplo un problema viene definido por $p = (i,cr)$.

- El conjunto de alternativas de tipo entero son

$A_{(i,cr)} = [min(m_i,cr/w_i),…,0]$ siendo $m_i,w_i$ propiedades del objeto $i$.

- La propiedad *W* es también de tipo entero el peso de la mochila.

- El operador $\underset{a \in A_p}{\large M}$ es en este caso el operador min con filtro de nulos.

- El tipo $S$ de la solución es un *Multiset<Objeto>*

- La función $set(P) \ nx(P \ p, A \ a)$ viene dada por $nx((i,cr),a) = {(i+1,cr-a w_i)}$

- La función $g((i,cr),a) = (a,f(nx((i,cr),a)).v()+a v_i)$

  

### Floyd

  

- En este ejemplo un problema viene definido por $p = (i,j,k)$.

- El conjunto de alternativas de tipo entero son $A_{(i,j,k)} = [T,F]$

- La propiedad W es de tipo real el peso del camino.

- El operador $\underset{a \in A_p}{\large M}$ es en este caso el operador min con filtro de nulos.

- El tipo $S$ de la solución es un *GraphPath\<Integer,SimpleEdge\<Integer\>\>*

- La función $set(P) \ nx(P \ p, A \ a)$ viene dada por $nx((i,j,k),F) = [(i,j,k+1)], nx((i,j,k),T) = [(i,k,k+1),(k,j,k+1)]$.

- La función $g((i,j,k),F) = (F,f(nx((i,j,k),F)_0).v())$, 
- $g((i,j,k),T) = (T,f(nx((i,j,k),T)_0)))+f(nx((i,j,k),T)_1).v())$

## Implementación

El conjunto de problemas de Programación Dinámica los podemos modelar como un hipergrafo dirigido con vértices de tipo *P*, el tipo de los problemas e hiperaristas de tipo  *E.* Una hiperarista conecta un vértice, la fuente, con un conjunto de vértices destino, targets, a través de una acción *a* de tipo *A*. Una hiperarista tiene, además un peso de tipo *W*.

Un vértice será un tipo inmutable que implemente `VirtualHyperVertex`:

```java
interface VirtualHyperVertex<V,E,A,W,S> {
	List<A> actions();
	Boolean isBaseCase();
	W baseCaseWeight();
	Boolean isValid();
	S baseCaseSolution();
	S solution(List<S> solutions);
	List<V> neighbors(A a);
	E edge(A a);
```


Una hiperarista será un tipo inmutable que implemente `SimpleHyperEdge`:

```java
interface SimpleHyperEdge<V,E,A,W> {
	V source();
	List<V> targets();
	A action();
	W weight(List<Double> targetsWeight);
}
```

Las soluciones parciales de los problemas en PD vienen definidas por `GraphTree<V,E,A,W,S> `. Un `GraphTree` es un tipo recursivo que dos constructores:

```java
sealed interface GraphTree<V,E,A,W,S> permits Gtb, Gtr {
	static <V,E,A,W,S> GraphTree<V,E,A,S> tb(V v) { ... }
	static <V,E,W,S> GraphTree<V,E,A,S> tr(V v, List<GraphTree<V, E, A, S>> targets) { ... }
}
record Gtb<V ,E, A, S>(V vertex) implements GraphTree<V, E, A, S> { ... }
record Gtr<V, E, A, S>(V vertex,List<GraphTree<V, E, A, S>> targets) implements GraphTree<V, E, A, S> { ... }
```

Del `GraphTree` podemos deducir su solución asociada, sus vértices, su peso, etc.

Justo a la anterior disponemos de dos funciones que implementan las anteriores funciones *f* y *g*. Ambas devuelven un *Sp*  y actualizan la memoria *Map<V,Sp<A,W,E>>*.

La implementación de f para vértices que no caso base lo hacemos mediante la función:

```java
Sp vertexSp(FloydVertex actual,Map<FloydVertex, Sp> memory) { ... }
```
La implementación de g  lo hacemos mediante la función:

```java
Sp edgeSp(FloydVertex actual, Boolean a, Map<FloydVertex, Sp> memory) { .... }
```
Estas funciones pueden tener una versión imperativa o funcional.
Usando ambas funciones la versión recursiva de f es la de la forma:

```java
Sp search(FloydVertex actual, Map<FloydVertex, Sp> memory) {
	Sp r = null;
	if (memory.containsKey(actual)) {
		r = memory.get(actual);
	} else if (actual.isBaseCase()) {
		Double w = actual.baseCaseWeight();
		if (w != null) r = Sp.of(w);
		memory.put(actual, r);
	} else {
		r = vertexSp(actual, memory);
		memory.put(actual, r);
	}
	return r;
}
```

La versión iterativa bottom-up de f es la de la forma:

```java
void searchBU(Map<FloydVertex, Sp> memory) {
	Integer n = FloydVertex.n;
	for (int k = n; k >= 0; k--) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Sp r = null;
				FloydVertex actual = FloydVertex.of(i, j, k);
				if (actual.isBaseCase()) {
					r = null;
					Double w = actual.baseCaseWeight();
					if (w != null) r = Sp.of(w);
					memory.put(actual, r);
				} else {
					r = FloydPD.vertexSp(actual, memory);
					memory.put(actual, r);
				}
			}
		}
	}
}
```