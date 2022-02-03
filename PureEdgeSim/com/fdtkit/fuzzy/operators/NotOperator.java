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
public class NotOperator implements UnaryOperator {

    @Override
    public double evaluate(double membership) {
        return ( 1 - membership );
    }
    
}
