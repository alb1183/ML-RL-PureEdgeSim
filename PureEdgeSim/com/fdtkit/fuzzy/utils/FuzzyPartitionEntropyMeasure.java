/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

import com.fdtkit.fuzzy.data.Dataset;
import java.util.List;

/**
 *
 * @author MHJ
 */
public class FuzzyPartitionEntropyMeasure extends MinimumPreferenceMeasure {
    
    @Override
    public double getPreferenceValue(Dataset dataset, String attribute, String[] evidances) {
        
        String className = dataset.getClassName();
        List<String> terms = dataset.getAttribute(attribute).getLinguisticTerms();
        List<String> classTerms = dataset.getAttribute(className).getLinguisticTerms();
        
        double [] a = null;
        if(evidances != null) {
            a = dataset.getAttribute(evidances[0]).getFuzzyValues(evidances[1]);
            for(int i = 2; i < evidances.length - 1; i += 2) {
 
                double [] aPrime = dataset.getAttribute(evidances[i]).getFuzzyValues(evidances[i + 1]);
                a = Utils.min(a, aPrime);

            }
        }

        double [] mi = new double[terms.size()];
        double [] entropies = new double[terms.size()];
        int j = 0;
        for(String term : terms) {
            double [] vals = dataset.getAttribute(attribute).getFuzzyValues(term);
            if(a != null) {
                vals = Utils.min(a, vals);
            }
            
            mi[j] = Utils.sum(vals);
            
            double [] mij = new double[classTerms.size()];
            int k = 0;
            for(String ck : classTerms) {
                double [] vals2 = dataset.getAttribute(className).getFuzzyValues(ck);
                
                vals2 = Utils.min(vals, vals2);
                
                mij[k++] = Utils.sum(vals2);
            }
            
            Utils.normalizeWith(mij, Utils.sum(mij));
            entropies[j++] = Utils.entropy(mij);
        }
        Utils.normalizeWith(mi, Utils.sum(mi));
        double s = 0;
        for(int i = 0; i < mi.length; i++) {
            s += entropies[i] * mi[i];
        }
        return s;        
        
    }

    
    
}
