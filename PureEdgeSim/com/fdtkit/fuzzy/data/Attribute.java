/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author Mohammed H. Jabreel

 */
public class Attribute {
    
    protected String name;
    //protected LinguisticVariable linguisticVariable;
    protected List<String> linguisticTerms;
    
    
    
    protected Dataset dataset;
    
    protected int index;

    public Attribute() {
    }

    
    public Attribute(String name) {
        this.name = name;
        this.linguisticTerms = new ArrayList<>();
    }
    
    
    public Attribute(String[] linguisticTerms) {
        this.linguisticTerms = new ArrayList<>(Arrays.asList(linguisticTerms));
    }

    public Attribute(String name, String[] linguisticTerms) {
        this.name = name;
        this.linguisticTerms = new ArrayList<>(Arrays.asList(linguisticTerms));
    }

    public Dataset getDataset() {
        return dataset;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setLinguisticTerms(List<String> linguisticTerms) {
        this.linguisticTerms = linguisticTerms;
    }
    
    public void addLinguisticTerm(String name) {
        this.linguisticTerms.add(name);
    }

    public List<String> getLinguisticTerms() {
        return linguisticTerms;
    }
    
    public Object getCrispValue(int idx) {
        return null;
    }
    
    public Object[] getCrispValues() {
        int rc = this.dataset.getRowsCount();
        Object[] vals = new Object[rc];
        for(int i = 0; i < rc; i++) {
            vals[i] = this.dataset.getCrispValue(i, this.name);
        }
        return vals;
    }
    
    public double[] getFuzzyValues(String termName) {
        int n = this.dataset.getRowsCount();
        double[] vals = new double[n];
        for(int i = 0; i < n; i++) {
            vals[i] = this.dataset.getFuzzyValue(i, this.index, termName);
        }
        return vals;
    }
    
    
    //g
    public double[] getFuzzyValues(int rowIdx) {
        int n = linguisticTerms.size();
        double[] vals = new double[n];
        for(int i = 0; i < n; i++) {
            vals[i] = this.dataset.getFuzzyValue(rowIdx, this.index, i);
        }
        return vals;
    }
    

    
    public double getFuzzyValue(int rowIdx, String termName) {
        return this.dataset.getFuzzyValue(rowIdx, this.index, termName);
    }
    
    public double getFuzzyValue(int rowIdx, int termIdx) {
        return this.dataset.getFuzzyValue(rowIdx, this.index, termIdx);
    }    
    
    public double[][] getFuzzyValues() {
        int n = this.dataset.getRowsCount();
        int m = linguisticTerms.size();
        double[][] vals = new double[n][m];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++)
            vals[i][j] = this.dataset.getFuzzyValue(i, this.index, j);
        }
        return vals;
    }
    
    public int getLinguisticTermIndex(String termName) {
        return this.linguisticTerms.indexOf(termName);
    }
    
    public String getBestLinguisticTerm(int rowIdx) {
        double [] vals = this.getFuzzyValues(rowIdx);
        double max = vals[0];
        
        int maxIdx = 0;
        for(int i = 1; i < vals.length; i++) {
            if(vals[i] > max) {
                maxIdx = i;
                max = vals[i];
            }
        }
        return this.linguisticTerms.get(maxIdx);
    } 
    
    public String getBestLinguisticTerm(double [] vals) {
        if(vals.length != linguisticTerms.size()) {
            throw new IndexOutOfBoundsException("");
        }
        double max = vals[0];
        
        int maxIdx = 0;
        for(int i = 1; i < vals.length; i++) {
            if(vals[i] > max) {
                maxIdx = i;
                max = vals[i];
            }
        }
        return this.linguisticTerms.get(maxIdx);
    }
    public int getLinguisticTermsCount(){
        return linguisticTerms.size();
    }
    
    
}
