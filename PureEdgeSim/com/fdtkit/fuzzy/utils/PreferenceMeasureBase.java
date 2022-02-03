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
public abstract class PreferenceMeasureBase implements PreferenceMeasure {

    @Override
    public String getBestAttribute(Dataset dataset) {
        String [] attributes = new String[dataset.getAttributesCount() - 1];
        for(int i = 0, k = 0; i < attributes.length; i++) {
            if(!dataset.getAttribute(i).getName().equals(dataset.getClassName())) {
                attributes[k++] = dataset.getAttribute(i).getName();
            }
        }
        return getBestAttribute(dataset, attributes, null);
    }

    @Override
    public String getBestAttribute(Dataset dataset, String[] attributes, String[] evidances) {
        double fpv = getPreferenceValue(dataset, attributes[0], evidances);
        String bestAttribute = attributes[0];
        for(int i  = 1; i < attributes.length; i++) {
            double pv = getPreferenceValue(dataset, attributes[i], evidances);
            if(this.isBetter(pv, fpv)) {
                fpv = pv;
                bestAttribute = attributes[i];
            }
        }
        return bestAttribute;
    }
    
    
    
    @Override
    public double getPreferenceValue(Dataset dataset, String attribute) {
        return getPreferenceValue(dataset, attribute, null);
    }   
}
