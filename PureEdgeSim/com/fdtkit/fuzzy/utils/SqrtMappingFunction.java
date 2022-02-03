/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

/**
 *
 * @author Mohammed
 */
public class SqrtMappingFunction extends MappingFunction {

    @Override
    public double map(double v) {
        return Math.sqrt(v);
    }

    @Override
    public double[] map(double[] vals) {
        double [] mappedVals = new double[vals.length];
        for(int i = 0; i < vals.length; i++) {
            mappedVals [i] = Math.sqrt(vals[i]);
        }
        return mappedVals; 
    }    
    
    
}
