/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.operators;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class MinimumNorm implements Norm {

    @Override
    public double evaluate(double membershipA, double membershipB) {
        return Math.min( membershipA, membershipB );    
    }
    
}
