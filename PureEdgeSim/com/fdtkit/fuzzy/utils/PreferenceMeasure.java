/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

import com.fdtkit.fuzzy.data.Dataset;


/**
 *
 * @author MHJ
 */
public interface PreferenceMeasure {
    
    double getPreferenceValue(Dataset dataset, String attribute, String [] evidances);
    
    double getPreferenceValue(Dataset dataset, String attribute);
    
    String getBestAttribute(Dataset dataset, String [] attributes, String [] evidances);
    
    String getBestAttribute(Dataset dataset);
    
    boolean isBetter(double v1, double v2);
    
    
}
