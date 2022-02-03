/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.membershipfunctions;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class SingletonFunction implements MembershipFunction {

    // The unique point where the membership value is 1.
    protected double support;

    public SingletonFunction(double support) {
        this.support = support;
    }
    
    
    
    @Override
    public double getMembership(double x) {
        return ( this.support == x ) ? 1 : 0;
    }

    @Override
    public double getLiftLimit() {
        return this.support;
    }

    @Override
    public double getRightLimit() {
        return this.support;
    }

    @Override
    public String toString() {
        return "Singletone Function";
    }

    public double getSupport() {
        return support;
    }
    
    
    
    
}
