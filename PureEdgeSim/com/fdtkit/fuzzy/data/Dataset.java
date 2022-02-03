/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class Dataset {
    
    private List<Row> rows = null;
    private List<Attribute> attribues = null;
    
    
    private String name;
    
    private String className;

    public Dataset(String name, String className) {
        this.name = name;
        this.className = className;
        this.attribues = new ArrayList<>();
        this.rows = new ArrayList<>();        
    }

    public Dataset() {
        this.attribues = new ArrayList<>();
        this.rows = new ArrayList<>();
    }
    
      
    

    public Dataset(String className) {
        this.className = className;
        this.name = "";
        this.attribues = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public List<Row> getRows() {
        return rows;
    }
   
    public List<Attribute> getAttribues() {
        return attribues;
    }
    
    public void addRow(Row row) {
        row.index = this.rows.size();
        row.dataset = this;
        this.rows.add(row);
    }
    
    public void addAttribute(Attribute attribute) {
        attribute.index = this.attribues.size();
        attribute.dataset = this;
        this.attribues.add(attribute);
    }
    
   
    public int getRowsCount() {
        return this.rows.size();
    }
    
    public int getAttributesCount() {
        return this.attribues.size();
    }
    
    public Attribute getAttribute(int idx) {
        return this.attribues.get(idx);
    }
    
    public Attribute getAttribute(String attrName) {
        for (Attribute attribue : this.attribues) {
            if (attribue.name.equals(attrName)) {
                return attribue;
            }
        }
        throw new IllegalArgumentException("Invalid attribute name : " + attrName);
    }
    
    public Object getCrispValue(int rowIdx, int attrIdx) {
        return this.rows.get(rowIdx).crispValues.get(attrIdx);
    }
    
    public Object getCrispValue(int rowIdx, String attrName) {
        int i = 0;
        for(; i < attribues.size(); i++) {
            if(attribues.get(i).name.equals(attrName)) {
                return this.getCrispValue(rowIdx, i);
            }
        }
        return null;
    }   
    
    public double getFuzzyValue(int rowIdx, int attrIdx, int termIdx) {
        return this.rows.get(rowIdx).getFuzzyValue(attrIdx, termIdx);
    }
    
    public void setFuzzyValue(int rowIdx, int idx, int termIdx, double val) {
        this.rows.get(rowIdx).setFuzzyValue(idx, termIdx, val);
    }    
    
    public double getFuzzyValue(int rowIdx, int attrIdx, String termName) {
        return this.rows.get(rowIdx).getFuzzyValue(attrIdx, termName);
    } 
    
    public void setFuzzyValue(int rowIdx, int idx, String termName, double val) {
        this.rows.get(rowIdx).setFuzzyValue(idx, termName, val);
    }    
    
    public double getFuzzyValue(int rowIdx, String attrName, int termIdx) {
        return this.rows.get(rowIdx).getFuzzyValue(attrName, termIdx);
    }
    
    public void setFuzzyValue(int rowIdx, String attrName, int termIdx, double val) {
        this.rows.get(rowIdx).setFuzzyValue(attrName, termIdx, val);
    }    
    
    public double getFuzzyValue(int rowIdx, String attrName, String termName) {
        return this.rows.get(rowIdx).getFuzzyValue(attrName, termName);
    }   
    
    public void setFuzzyValue(int rowIdx, String attrName, String termName, double val) {
        this.rows.get(rowIdx).setFuzzyValue(attrName, termName, val);
    }    
    
    public double[] getFuzzyValues(int rowIdx, int attrIdx) {
        return this.attribues.get(attrIdx).getFuzzyValues(rowIdx);
    }
    
    public double[] getFuzzyValues(int rowIdx, String attrName) {
        int i = 0;
        for(; i < this.attribues.size(); i++) {
            if(this.attribues.get(i).name.equals(attrName)) {
                return this.attribues.get(i).getFuzzyValues(rowIdx);
            }
        }
        
        return null;
    } 
    
    public void setFuzzyValues(int rowIdx, int attrIdx, double[] values) {
        this.rows.get(rowIdx).setFuzzyValues(attrIdx, values);
    } 
    
    public void setFuzzyValues(int rowIdx, String attrName, double[] values) {
        this.rows.get(rowIdx).setFuzzyValues(attrName, values);
    }  
    
    public void setFuzzyValues(int rowIdx, double[][] values) {
        this.rows.get(rowIdx).setFuzzyValues(values);
    }      
}
