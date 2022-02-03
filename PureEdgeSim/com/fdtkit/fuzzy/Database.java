/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class Database {

    private Dictionary<String, LinguisticVariable> variables;

    public Database() {
        this.variables = new Hashtable<>();
    }
    
    public void addVariable(LinguisticVariable variable) {
        if(this.variables.get(variable.getName()) != null) 
            throw new IllegalArgumentException("The linguistic variable name already exists in the database.");
        
        this.variables.put(variable.getName(), variable);
    }
    
    public void clearVariables() {
        this.variables = new Hashtable<>(10);
    }
    
    public LinguisticVariable getVariable(String variableName) {
        LinguisticVariable lv = this.variables.get(variableName);
        if(lv == null) {
            throw new IllegalArgumentException("");
        }
        return lv;
    }
    
}
