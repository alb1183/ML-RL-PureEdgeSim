/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
/**
 *
 * @author Mohammed H. Jabreel
 */
public class Row {
    
    protected int index;
    protected Dataset dataset;
    
    protected List<Object> crispValues;
    protected List<List<Double>> fuzzyValues;

    protected Row() {
        
    }

    public Row(Object[] crispValues, Dataset dataset) {
        
        this.crispValues = new ArrayList<>(Arrays.asList(crispValues));
        this.fuzzyValues = new ArrayList<>();
        this.dataset = dataset;
        int n = this.dataset.getAttributesCount();
        for(int i = 0; i <n; i++) {
            int m = this.dataset.getAttribute(i).getLinguisticTerms().size();
            List<Double> ls = new ArrayList<>();
            for(int j = 0; j < m; j++) {
                ls.add(Double.NaN);
            }
            fuzzyValues.add(ls);
        }
    }
    
    public Row(Object[] crispValues) {
        
        this.crispValues = new ArrayList<>(Arrays.asList(crispValues));
        this.fuzzyValues = new ArrayList<>();
    }    
    
    public Row(Object[] crispValues, double[][] fuzzyValues) {
        
        this.crispValues = new ArrayList<>(Arrays.asList(crispValues));
        this.fuzzyValues = new ArrayList<>();
        int n = fuzzyValues.length;
        
        for(int i = 0; i < n; i++) {
            List<Double> l = new ArrayList<>();
            for(int j = 0, m = fuzzyValues[i].length; j < m; j++) {
                l.add(fuzzyValues[i][j]);
            }
            this.fuzzyValues.add(l);
        }
        //this.fuzzyValues = fuzzyValues;
    }

    
    public Object[] getCrispValues() {
        return this.crispValues.toArray();
    }
    
    public void setCrispValues(Object[] values) {
        this.crispValues.clear();
        this.crispValues.addAll(Arrays.asList(values));
    }
    
    public double[] getFuzzyValues(int attrIdex) {
        double [] vals = new double[fuzzyValues.get(attrIdex).size()];
        for(int i = 0; i < this.fuzzyValues.get(attrIdex).size(); i++) {
            vals[i] = this.fuzzyValues.get(attrIdex).get(i);
        }
        return vals;
    }
    
    public double[] getFuzzyValues(String attrName) {
        int attrIdex = this.dataset.getAttribute(attrName).index;
        double [] vals = new double[fuzzyValues.get(attrIdex).size()];
        for(int i = 0; i < this.fuzzyValues.get(attrIdex).size(); i++) {
            vals[i] = this.fuzzyValues.get(attrIdex).get(i);
        }
        return vals;
    }    
    
    public void setFuzzyValues(int attrIdex, double[] values) {
        for(int i = 0; i <values.length; i++) {
             this.fuzzyValues.get(attrIdex).set(i, values[i]);
        }        
    }
    
    public void setFuzzyValues(String attrName, double[] values) {
        int attrIdex = this.dataset.getAttribute(attrName).index;
        for(int i = 0; i < this.fuzzyValues.get(attrIdex).size(); i++) {
            this.fuzzyValues.get(attrIdex).set(i, values[i]);
        }
    }  
    
    public void putFuzzyValues(String attrName, double[] values) {
        List<Double> fvals = new ArrayList<>();
        for(double v : values) {
            fvals.add(v);
        }
        this.fuzzyValues.add(fvals);       
    }
    
    public void setFuzzyValues(double[][] values) {
        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < values[i].length; j++) {
                this.fuzzyValues.get(i).set(j, values[i][j]);
            }
        }
    }       

    public Dataset getDataset() {
        return dataset;
    }

    public int getIndex() {
        return index;
    }
    
    
    public Object getCrispValue(int idx) {
        return this.crispValues.get(index);
    }
    
    public Object getCrispValue(String attributeName) {
        
        for(int i = 0; i < this.dataset.getAttributesCount(); i++){
            if(this.dataset.getAttribues().get(i).name.equals(attributeName))
                return this.crispValues.get(i);
        }
        return null;
    }
    
    public void setCrispValue(int idx, Object val) {
        this.crispValues.set(idx, val);
    }
    
    public void setCrispValue(String attributeName, Object val) {
        for(int i = 0; i < this.dataset.getAttributesCount(); i++){
            if(this.dataset.getAttribues().get(i).name.equals(attributeName)) {
                this.crispValues.set(i, val);
                return;
            }
        } 
        throw new IllegalArgumentException("Invalid attribute name : " + attributeName);
    }    
    
    public double getFuzzyValue(int idx, int termIdx) {
        return this.fuzzyValues.get(idx).get(termIdx);        
    }
    
    public void setFuzzyValue(int idx, int termIdx, double val) {
        this.fuzzyValues.get(idx).set(termIdx, val);        
    }
    
    public double getFuzzyValue(int idx, String linguisticTermName) {
        Attribute a = this.dataset.getAttribute(idx);
        int termIdx = a.getLinguisticTermIndex(linguisticTermName);
        if(termIdx >= 0) {
            return this.fuzzyValues.get(idx).get(termIdx);
        }
        throw new IndexOutOfBoundsException("Term Index out of bound : " + linguisticTermName);        
    }
    
    public void setFuzzyValue(int idx, String linguisticTermName, double val) {
        Attribute a = this.dataset.getAttribute(idx);
        int termIdx = a.getLinguisticTermIndex(linguisticTermName);
        if(termIdx >= 0) {
           this.fuzzyValues.get(idx).set(termIdx, val);
           return;
        }
        throw new IndexOutOfBoundsException("Term Index out of bound : " + linguisticTermName);         
    }    
    
    public double getFuzzyValue(String attributeName, int termIdx) {
        Attribute a = this.dataset.getAttribute(attributeName);
        if(a != null) {
            return this.fuzzyValues.get(a.index).get(termIdx);
            
        }
        throw new IndexOutOfBoundsException();        
        
    }
    
    public void setFuzzyValue(String attributeName, int termIdx, double val) {
        Attribute a = this.dataset.getAttribute(attributeName);
        if(a != null) {
            this.fuzzyValues.get(a.index).set(termIdx, val);
            return;
        }
        throw new IndexOutOfBoundsException();        
    }    
    
    public double getFuzzyValue(String attributeName, String linguisticTermName)  {
        Attribute a = this.dataset.getAttribute(attributeName);
        if(a != null) {
            if(!linguisticTermName.isEmpty()) {
                int lti = a.getLinguisticTermIndex(linguisticTermName);
                if(lti >= 0) {
                    return this.fuzzyValues.get(a.index).get(lti);
                }
            }
        }
        throw new IndexOutOfBoundsException();        
    }
    
    public void setFuzzyValue(String attributeName, String linguisticTermName, double val) {
        Attribute a = this.dataset.getAttribute(attributeName);
        if(a != null) {
            if(!linguisticTermName.isEmpty()) {
                int lti = a.getLinguisticTermIndex(linguisticTermName);
                if(lti >= 0) {
                    this.fuzzyValues.get(a.index).set(lti, val);
                    return;
                }
            }
        }
        
        throw new IndexOutOfBoundsException();        
    }    
}
