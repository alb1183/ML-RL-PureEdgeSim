/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class FuzzyOutput {
    public class OutputConstraint {
        // The label of a fuzzy output
        private String label;
        
        // The firing strength of a fuzzy rule, to be applied to the label
        private double firingStrength;

        public OutputConstraint(String label, double firingStrength) {
            this.label = label;
            this.firingStrength = firingStrength;
        }

        public double getFiringStrength() {
            return firingStrength;
        }

        public String getLabel() {
            return label;
        }
        
    };
    
    // the linguistic variables repository
    private List<OutputConstraint> outputList;
    
    // the output linguistic variable 
    private LinguisticVariable outputVariable;

    public FuzzyOutput() {
    }

    public FuzzyOutput(LinguisticVariable outputVar) {
        this.outputList = new ArrayList<>(20);
        this.outputVariable = outputVar;
    }

    public void addOutput(String labelName, double firingStrength ) {
        
        this.outputVariable.getLabel(labelName);
        this.outputList.add(new OutputConstraint(labelName, firingStrength));
        
    }
    
    
    public void clearOutput() {
        this.outputList.clear();
    }
    public List<OutputConstraint> getOutputList() {
        return outputList;
    }

    public LinguisticVariable getOutputVariable() {
        return outputVariable;
    }
    
    
}
