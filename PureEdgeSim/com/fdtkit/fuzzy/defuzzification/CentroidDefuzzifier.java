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
public class CentroidDefuzzifier implements Defuzzifier {

    // number of intervals to use in numerical approximation
    private int intervals;

    public CentroidDefuzzifier(int intervals) {
        this.intervals = intervals;
    }
    
    
    
    @Override
    public double defuzzify(FuzzyOutput fuzzyOutput, Norm normOperator) {
        
        // results and accumulators
        double weightSum = 0,
               membershipSum = 0;
        
        // speech universe
        double start = fuzzyOutput.getOutputVariable().getStart();
        double end = fuzzyOutput.getOutputVariable().getEnd();
        
        //
        double increment = ( end - start ) / this.intervals;
        
        for(double x = start; x < end; x += increment) {
            for(FuzzyOutput.OutputConstraint oc : fuzzyOutput.getOutputList()) {
                double membership = fuzzyOutput.getOutputVariable().getLabelMembership( oc.getLabel(), x );
                double constrainedMembership = normOperator.evaluate( membership, oc.getFiringStrength() );
                
                weightSum += x * constrainedMembership;
                membershipSum += constrainedMembership;
            }
            
        }
        // if no membership was found, then the membershipSum is zero and the numerical output is unknown.
        if ( membershipSum == 0 )
            throw new IllegalArgumentException( "The numerical output in unavaliable. All memberships are zero." );
        return weightSum / membershipSum;
    }
    
}
