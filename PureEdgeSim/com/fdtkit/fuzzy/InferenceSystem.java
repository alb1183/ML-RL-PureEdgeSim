/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import com.fdtkit.fuzzy.defuzzification.Defuzzifier;
import com.fdtkit.fuzzy.operators.CoNorm;
import com.fdtkit.fuzzy.operators.MaximumCoNorm;
import com.fdtkit.fuzzy.operators.MinimumNorm;
import com.fdtkit.fuzzy.operators.Norm;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class InferenceSystem {
    // The linguistic variables of this system
    private Database database;
    
    // The fuzzy rules of this system
    private Rulebase ruleBase;
    
    // The defuzzifier method choosen 
    private Defuzzifier defuzzifier;
    
    // Norm operator used in rules and deffuzification
    private Norm normOperator;
    
    // CoNorm operator used in rules
    private CoNorm conormOperator;

    public InferenceSystem(Database database, Defuzzifier defuzzifier, Norm normOperator, CoNorm conormOperator) {
        this.database = database;
        this.defuzzifier = defuzzifier;
        this.normOperator = normOperator;
        this.conormOperator = conormOperator;
    }

    public InferenceSystem(Database database, Defuzzifier defuzzifier) {
        this(database, defuzzifier, new MinimumNorm(), new MaximumCoNorm());
    }
    
    public Rule newRule(String name, String rule) {
        Rule r = new Rule(name, rule, database, normOperator, conormOperator);
        this.ruleBase.addRule(r);
        return r;
    }
    
    public void setInput(String variableName, double value) {
        this.database.getVariable(variableName).setNumericInput(value);
    }
    
    public LinguisticVariable getLinguisticVariable(String variableName) {
        return this.database.getVariable(variableName);
    }
    
    public Rule getRule(String ruleName) {
        return this.ruleBase.getRule(ruleName);
    }
    
    public FuzzyOutput executeInference( String variableName ) {
        // gets the variable
        LinguisticVariable lingVar = database.getVariable( variableName );
        
        // object to store the fuzzy output
        FuzzyOutput fuzzyOutput = new FuzzyOutput( lingVar );
        
        // select only rules with the variable as output
        Rule[] rules = ruleBase.getRules( );
        for(Rule r : rules) {
            if ( r.getOutput().getVariable().getName() == variableName ) {
                String labelName = r.getOutput().getVariable().getName();
                double firingStrength = r.evaluateFiringStrength();
                if(firingStrength > 0) {
                    fuzzyOutput.addOutput(labelName, firingStrength);
                }
            }
        }
        
        return fuzzyOutput;
    }
    
    
    
    
}
