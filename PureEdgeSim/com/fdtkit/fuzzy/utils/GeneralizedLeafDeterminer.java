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
public class GeneralizedLeafDeterminer implements LeafDeterminer {
    
    
    protected double truthLevel;
    protected MappingFunction mappingFunction;

    public GeneralizedLeafDeterminer(double truthLevel, MappingFunction mappingFunction) {
        this.truthLevel = truthLevel;
        this.mappingFunction = mappingFunction;
    }

    public GeneralizedLeafDeterminer(double truthLevel) {
        this(truthLevel, new MappingFunction());
    }

    public MappingFunction getMappingFunction() {
        return mappingFunction;
    }

    public void setMappingFunction(MappingFunction mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    public double getTruthLevel() {
        return truthLevel;
    }

    public void setTruthLevel(double truthLevel) {
        this.truthLevel = truthLevel;
    }

       
    

    @Override
    public LeafDescriptor getLeafDescriptor(Dataset dataset, String[] evidances) {
        if(evidances.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid arguments. ");
        }  
        double [] a = dataset.getAttribute(evidances[0]).getFuzzyValues(evidances[1]);       
        int n = a.length;
        
        for(int i = 0; i < n; i++) {
            for(int j = 2; j < evidances.length - 2; j += 2) {
                double v = dataset.getAttribute(evidances[j]).getFuzzyValue(i, evidances[j + 1]); 
                a[i] = Math.min(v, a[i]);
            }
        }
        double[] b = dataset.getAttribute(evidances[evidances.length - 2]).getFuzzyValues(evidances[evidances.length - 1]);      
        
        double s = Utils.subSetHood(mappingFunction.map(a), mappingFunction.map(b));
        
        return new LeafDescriptor(s >= truthLevel, s);
        
    }    
    
    
    
}
