La formulación del problema de recubrimiento de aristas (o _edge cover_) busca seleccionar el conjunto de aristas en un grafo tal que todos los vértices sean incidentes a al menos una de las aristas elegidas y la suma de los pesos de las aristas elegidas es mínimo.

Sea $E$ el conjunto de las aristas

$$
  \begin{array}{ll}
    \min \sum\limits_{i=0,j=0 | j \gt i, (i,j) \in E }^{n-1,n-1} w_{ij} x_{ij} & \\
     \sum\limits_{j=0 | j \gt i, (i,j) \in E }^{n-1} x_{ij} + \sum\limits_{j=0 | j \gt i, (i,j) \in E }^{n-1} x_{ji} \ge 1 &  i \in 0..n-1\\
     bin \ x_{ij}  &  i \in 0..n-1, j \in 0..n-1 | j \gt i, (i,j) \in E
  \end{array}
$$