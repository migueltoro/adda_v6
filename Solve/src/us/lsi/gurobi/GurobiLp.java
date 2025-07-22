package us.lsi.gurobi;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/* Copyright 2020, Gurobi Optimization, LLC */

/* This example reads an LP model from a file and solves it.
   If the model is infeasible or unbounded, the example turns off
   presolve and solves the model again. If the model is infeasible,
   the example computes an Irreducible Inconsistent Subsystem (IIS),
   and writes it to a file */

import gurobi.*;

public class GurobiLp {
  
//	public static void main(String[] args) {
	
	public static void solve(String file) {
		Locale.setDefault(Locale.of("en", "US"));
		Optional<GurobiSolution> solution = GurobiLp.gurobi(file);
		if (!solution.isPresent()) {
			System.out.println("Error in solving the model.");
			return;
		}
		GurobiSolution s = solution.get();
		System.out.println("\n\n\n\n");
		System.out.println(String.format("Objetivo : %.2f",s.objVal));
		System.out.println("\n\n");
		System.out.println(s.values.keySet()
				.stream()
				.filter(e->s.values.get(e)>0.)
				.map(e->String.format("%s == %.1f",e,s.values.get(e)))
				.collect(Collectors.joining("\n")));
	}
	
	public static GurobiSolution solveSolution(String file) {
		Locale.setDefault(Locale.of("en", "US"));
		GurobiSolution solution = GurobiLp.gurobi(file).get();
		return solution;
	}
	
	public static Optional<GurobiSolution> gurobi(String file) {

//		if (args.length < 1) {
//			System.out.println("Usage: java Lp filename");
//			System.exit(1);
//		}
		GRBModel model = null;
//		double[] xvals = null;
//		String[] names = null;
		Double objval = null;
		GRBVar[] vars = null;
		Map<String, Double> map = null;
		try {
			GRBEnv env = new GRBEnv();
			env.set("OutputFlag", "0"); // Suppress output
			model = new GRBModel(env, file);

			model.optimize();

			int optimstatus = model.get(GRB.IntAttr.Status);

			if (optimstatus == GRB.Status.INF_OR_UNBD) {
				model.set(GRB.IntParam.Presolve, 0);
				model.optimize();
				optimstatus = model.get(GRB.IntAttr.Status);
			}

//			if (optimstatus == GRB.Status.OPTIMAL || optimstatus == GRB.Status.SUBOPTIMAL) {
//                System.out.println("Optimization was successful");
//                System.out.println("Objective value: " + model.get(GRB.DoubleAttr.ObjVal));
			// Get the variable values
//				double objval = model.get(GRB.DoubleAttr.ObjVal);
//				System.out.println("Optimal objective: " + objval);
//            } else if (optimstatus == GRB.Status.SUBOPTIMAL) {
//                System.out.println("Suboptimal objective: " + model.get(GRB.DoubleAttr.ObjVal));
//			} else if (optimstatus == GRB.Status.INFEASIBLE) {
//				System.out.println("Model is infeasible");
//
//				// Compute and write out IIS
//				model.computeIIS();
//				model.write("model.ilp");
//			} else if (optimstatus == GRB.Status.UNBOUNDED) {
//				System.out.println("Model is unbounded");
//			} else {
//				System.out.println("Optimization was stopped with status = " + optimstatus);
//			}

			if (optimstatus == GRB.Status.OPTIMAL || optimstatus == GRB.Status.SUBOPTIMAL) {
//				System.out.println("Optimization was successful");
				objval = model.get(GRB.DoubleAttr.ObjVal);
				vars = model.getVars();
//				xvals = model.get(GRB.DoubleAttr.X, model.getVars());
//				names = model.get(GRB.DoubleAttr.VarName, model.getVars());
				map = new HashMap<>();
				for (GRBVar v : vars) {
					map.put(v.get(GRB.StringAttr.VarName), v.get(GRB.DoubleAttr.X));
				}

				// Dispose of model and environment
				model.dispose();
				env.dispose();
			}

		} catch (GRBException e) {
			return Optional.empty();
//			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		if (model == null || objval == null || map == null) {
			return Optional.empty();
		}
		return Optional.of(GurobiSolution.of(objval, map));
	}
}
