/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import com.fdtkit.fuzzy.membershipfunctions.MembershipFunction;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class FuzzySet {
   // name of the fuzzy set
    private String name;
    // membership functions that defines the shape of the fuzzy set
    private MembershipFunction function;

    public FuzzySet(String name, MembershipFunction function) {
        this.name = name;
        this.function = function;
    }

    
    public double getLeftLimit() {
        return function.getLiftLimit();
    }
    
    public double getRightLimit() {
        return function.getRightLimit();
    }
    
    
    public double getMembership(double x) {
        return function.getMembership(x);
    }
    

    public String getName() {
        return name;
    }

    public MembershipFunction getFunction() {
        return function;
    }
    

    

    
}
