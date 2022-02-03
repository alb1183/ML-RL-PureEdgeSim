/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

import java.util.Arrays;

/**
 *
 * @author MHJ
 */
public class MappingFunction {
    public double map(double v) {
        return v;
    }
    
    public double [] map(double [] vals) {
        return Arrays.copyOf(vals, vals.length);
    }
}
