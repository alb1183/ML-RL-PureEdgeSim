/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.defuzzification;

import com.fdtkit.fuzzy.FuzzyOutput;
import com.fdtkit.fuzzy.operators.Norm;

/**
 *
 * @author Mohammed H. Jabreel
 */
public interface Defuzzifier {
    
    // <returns>The numerical representation of the fuzzy output.</returns>
    double defuzzify( FuzzyOutput fuzzyOutput, Norm normOperator);
}
