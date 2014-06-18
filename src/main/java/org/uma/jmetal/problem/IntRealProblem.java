//  IntRealProblem.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solution.IntRealSolution;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing a problem having N integer and M real variables.
 * This is not a true problem; it is only intended as an example
 */
public class IntRealProblem extends Problem {

  /**
   *
   */
  private static final long serialVersionUID = -3553215776619374111L;
  int intVariables_;
  int realVariables_;

  /**
   * Constructor.
   * Creates a default instance of the IntRealProblem problem.
   */
  public IntRealProblem(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 3, 3);
  } // IntRealProblem

  /**
   * Constructor.
   * Creates a new instance of the IntRealProblem problem.
   *
   * @param intVariables  Number of integer variables of the problem
   * @param realVariables Number of real variables of the problem
   */
  public IntRealProblem(String solutionType, int intVariables, int realVariables)
    throws JMetalException {
    intVariables_ = intVariables;
    realVariables_ = realVariables;

    numberOfVariables_ = intVariables_ + realVariables_;
    numberOfObjectives_ = 2;
    numberOfConstraints_ = 0;
    problemName_ = "IntRealProblem";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];

    for (int i = 0; i < intVariables; i++) {
      lowerLimit_[i] = -5;
      upperLimit_[i] = 5;
    }

    for (int i = intVariables; i < (intVariables + realVariables); i++) {
      lowerLimit_[i] = -5.0;
      upperLimit_[i] = 5.0;
    }

    if (solutionType.compareTo("IntReal") == 0) {
      solutionType_ = new IntRealSolution(this, intVariables, realVariables);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] variable = solution.getDecisionVariables();

    double[] fx = new double[2];

    fx[0] = 0.0;
    for (int var = 0; var < intVariables_; var++) {
      fx[0] += (int) variable[var].getValue();
    }

    fx[1] = 0.0;
    for (int var = intVariables_; var < numberOfVariables_; var++) {
      fx[1] += variable[var].getValue();
    }

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }
}