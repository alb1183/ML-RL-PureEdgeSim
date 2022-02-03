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
public class MaximumCoNorm implements CoNorm {

    @Override
    public double evaluate(double membershipA, double membershipB) {
        return Math.max( membershipA, membershipB );    
    }
    
}
