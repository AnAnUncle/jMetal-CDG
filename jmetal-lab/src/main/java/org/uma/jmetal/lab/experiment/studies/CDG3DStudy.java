package org.uma.jmetal.lab.experiment.studies;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.cdg.CDGBuilder;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Viennet2;
import org.uma.jmetal.problem.multiobjective.dtlz.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with the algorithms NSGAII,
 * MOEA/D, and SMPSO.
 * <p>
 * This experiment assumes that the reference Pareto front are known and that, given a problem named
 * P, there is a corresponding file called P.csv containing its corresponding Pareto front. If this
 * is not the case, please refer to class {@link DTLZStudy} to see an example of how to explicitly
 * indicate the name of those files.
 * <p>
 * Five quality indicators are used for performance assessment: {@link Epsilon}, {@link Spread},
 * {@link GenerationalDistance}, {@link PISAHypervolume}, and {@link InvertedGenerationalDistancePlus}.
 * <p>
 * The steps to carry out are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Compute que quality indicators
 * 4. Generate Latex tables reporting means and medians, and tables with statistical tests
 * 5. Generate HTML pages with tables, boxplots, and fronts.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class CDG3DStudy {
    private static final int INDEPENDENT_RUNS = 30;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new JMetalException("Missing argument: experimentBaseDirectory");
        }
        String experimentBaseDirectory = args[0];

        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new DTLZ1()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ2()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ3()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ4()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ5()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ6()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ7()).setReferenceFront("DTLZ1.3D.csv"));
        problemList.add(new ExperimentProblem<>(new Viennet2()));

        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
                configureAlgorithmList(problemList);

        Experiment<DoubleSolution, List<DoubleSolution>> experiment =
                new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("CDG3DStudy")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setReferenceFrontDirectory("resources/referenceFronts")
                        .setExperimentBaseDirectory(experimentBaseDirectory)
                        .setOutputParetoFrontFileName("FUN.")
                        .setOutputParetoSetFileName("VAR.")
                        .setIndicatorList(List.of(
                                new NormalizedHypervolume()
                                ))
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(8)
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
    }

    /**
     * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
     * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
     */
    static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
            List<ExperimentProblem<DoubleSolution>> problemList) {
        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
        double cr = 1.0;
        double f = 0.5;
        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            for (var experimentProblem : problemList) {
                DifferentialEvolutionCrossover crossover =
                        new DifferentialEvolutionCrossover(
                                cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

                Algorithm<List<DoubleSolution>> algorithm =
                        new CDGBuilder(experimentProblem.getProblem())
                                .setCrossover(crossover)
                                .setMaxEvaluations(100 * 200)
                                .setPopulationSize(100)
                                .setResultPopulationSize(100)
                                .setNeighborhoodSelectionProbability(0.9)
                                .build();
                algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
            }
        }
        return algorithms;
    }
}
