/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.stream.*;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class LinguisticVariable {
    
    // name of the linguistic variable
    private String name;
    // right limit within the lingusitic variable works
    private double start;
    // left limit within the lingusitic variable works
    private double end;
    // the linguistic labels of the linguistic variable
    private Dictionary<String, FuzzySet> labels;
    // the numeric input of this variable
    private double numericInput;
    
    public LinguisticVariable( String name, double start, double end )  {
        this.name  = name;
        this.start = start;
        this.end   = end;

        // instance of the labels list - usually a linguistic variable has no more than 10 labels
        this.labels = new Hashtable<>(10);
    }   
    
    public void addLabel( FuzzySet label ) {
        // checking for existing name
        if ( this.labels.get(label.getName())!= null)
            throw new IllegalArgumentException( "The linguistic label name already exists in the linguistic variable." );
        

        // checking ranges
        if ( label.getLeftLimit() < this.start )
            throw new IllegalArgumentException( "The left limit of the fuzzy set can not be lower than the linguistic variable's starting point." );
        if ( label.getRightLimit() > this.end )
            throw new IllegalArgumentException( "The right limit of the fuzzy set can not be greater than the linguistic variable's ending point." );

        // adding label
        this.labels.put(label.getName(), label );
    }

    public void clearLabels( ){
        this.labels = new Hashtable<>(10);
    }


    public FuzzySet getLabel( String labelName ) {
        return labels.get(labelName);
    }


    public double getLabelMembership( String labelName, double value )  {
        FuzzySet fs = labels.get(labelName);
        if (fs != null)
            return fs.getMembership( value );
        else 
            throw new IllegalArgumentException();
    }
    
    public double[] getMemberships(double x) {
        double[] memValues = new double[this.labels.size()];
        Enumeration<String> it = this.labels.keys();
        int i = 0;
        for(;it.hasMoreElements();) {
            String key = it.nextElement();
            memValues[i++] = this.labels.get(key).getMembership(x);
        }
        
        return memValues;
    }

    public String getName() {
        return name;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double getNumericInput() {
        return numericInput;
    }

    public void setNumericInput(double numericInput) {
        this.numericInput = numericInput;
    }
    
    public String[] getLableNames() {
        String[] res = new String[this.labels.size()];
        int i = res.length - 1;
       for (Enumeration e = this.labels.keys(); e.hasMoreElements();) {
            res[i--] = e.nextElement().toString();
        }
        return res;
    }
    
    
    
}
