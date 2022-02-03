/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class Clause {
    

    // the linguistic var of the clause
    private LinguisticVariable variable;
    // the label of the clause
    private FuzzySet label;

    public Clause(LinguisticVariable variable, FuzzySet label) {
        this.variable = variable;
        this.label = label;
    }

    public LinguisticVariable getVariable() {
        return variable;
    }

    public FuzzySet getLabel() {
        return label;
    }
    
    public double evaluate( ) {
        return label.getMembership(variable.getNumericInput() );
    }


    @Override
    public String toString() {
        return this.variable.getName() + " IS " + this.label.getName();
    }    

   
}
