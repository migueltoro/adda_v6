package us.lsi.alg.mochila.pd;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import us.lsi.alg.mochila.MochilaVertexI;
import us.lsi.common.Multiset;
import us.lsi.hypergraphs.VirtualHyperVertex;
import us.lsi.mochila.datos.DatosMochila;
import us.lsi.mochila.datos.ObjetoMochila;

public record MochilaVertexPD(Integer index, Integer capacidadRestante) 
	implements VirtualHyperVertex<MochilaVertexPD,MochilaEdgePD,Integer,Multiset<ObjetoMochila>>{
	
	public static Integer n = DatosMochila.numeroDeObjetos;
	public static Integer capacidadInicial;
	
	public static MochilaVertexPD of() {
		return new MochilaVertexPD(0, capacidadInicial);
	}
	
	public static MochilaVertexPD of(Integer index, Integer capacidadRestante) {
		return new MochilaVertexPD(index, capacidadRestante);
	}
	
	public Integer greedyAction() {
		return Math.min(this.capacidadRestante/DatosMochila.getPeso(index),DatosMochila.getNumMaxDeUnidades(index));
	}

	@Override
	public List<Integer> actions() {
		if(this.index == n) return List.of();
		Integer nu = greedyAction().intValue();
		if(this.index == n-1) return List.of(nu);
		List<Integer> alternativas = IntStream.rangeClosed(0,nu)
				.boxed()
				.collect(Collectors.toList());
		Collections.reverse(alternativas);
		return alternativas;
	}

	@Override
	public Boolean isBaseCase() {
		return this.index == MochilaVertexI.n || this.index == n-1;
	}

	@Override
	public Double baseCaseWeight() {
		Double r = null;
		if(this.index == n) r = 0.;
		else if(this.index == n-1) {
		 Integer nu = greedyAction();
		 r = nu.doubleValue() * DatosMochila.getValor(index);
		}
		return r;
	}

	@Override
	public Boolean isValid() {
		return index>=0 && index<=DatosMochila.getObjetos().size();
	}

	@Override
	public Multiset<ObjetoMochila> baseCaseSolution() {
		Multiset<ObjetoMochila> r = null;
		if(this.index == n) r= Multiset.empty();
		if(this.index == n-1) {
			Integer nu = greedyAction().intValue();
			ObjetoMochila om = DatosMochila.getObjeto(index);
			r = Multiset.empty();
			r.add(om, nu);
		}
		return r;
	}

	@Override
	public Multiset<ObjetoMochila> solution(Integer a, List<Multiset<ObjetoMochila>> solutions) {
		ObjetoMochila om = DatosMochila.getObjeto(this.index());
		Multiset<ObjetoMochila> ms = solutions.get(0);
		ms.add(om, a);
		return ms;
	}

	@Override
	public List<MochilaVertexPD> neighbors(Integer a) {
		Integer cr = capacidadRestante - a * DatosMochila.getPeso(index);
		return List.of(MochilaVertexPD.of(index + 1, cr));
	}

	@Override
	public MochilaEdgePD edge(Integer a) {
		List<MochilaVertexPD> targets = this.neighbors(a);
		return MochilaEdgePD.of(this,targets,a);
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d)", index, capacidadRestante);
	}
	
	public static Integer valor(Multiset<ObjetoMochila> s) {
		return s.elementSet().stream()
				.mapToInt(o -> o.valor() * s.count(o))
				.sum();
	}

}
