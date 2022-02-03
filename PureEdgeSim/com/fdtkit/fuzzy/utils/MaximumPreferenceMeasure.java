/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

/**
 *
 * @author MHJ
 */
public abstract class MaximumPreferenceMeasure implements PreferenceMeasure {

    @Override
    public boolean isBetter(double v1, double v2) {
        return v1 > v2;
    }
    
}
