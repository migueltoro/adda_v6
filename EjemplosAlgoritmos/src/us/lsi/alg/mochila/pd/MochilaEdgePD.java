package us.lsi.alg.mochila.pd;

import java.util.List;

import us.lsi.hypergraphs.SimpleHyperEdge;
import us.lsi.mochila.datos.DatosMochila;
import us.lsi.mochila.datos.ObjetoMochila;

public record MochilaEdgePD(MochilaVertexPD source,List<MochilaVertexPD> targets,Integer action) 
	implements SimpleHyperEdge<MochilaVertexPD,MochilaEdgePD,Integer> {
	
	public static MochilaEdgePD of(MochilaVertexPD source,List<MochilaVertexPD> targets,Integer action) {
		return new MochilaEdgePD(source,targets,action);
	}

	@Override
	public Double weight(List<Double> targetsWeight) {
		ObjetoMochila om = DatosMochila.getObjeto(source.index());
		return action*om.valor()+targetsWeight.get(0);
	}

}
