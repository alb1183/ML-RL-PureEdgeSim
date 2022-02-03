/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

import com.fdtkit.fuzzy.utils.MappingFunction;

/**
 *
 * @author MHJ
 */
public class QuadraticMappingFunction extends MappingFunction {

    @Override
    public double map(double v) {
        return v * v; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] map(double[] vals) {
        double [] mappedVals = new double[vals.length];
        for(int i = 0; i < vals.length; i++) {
            mappedVals [i] = Math.pow(vals[i], 2);
        }
        return mappedVals; 
    }

}
