/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.membershipfunctions;

/**
 *
 * @author MHJ
 */
public class AlphaTrapezoidalFunction extends TrapezoidalFunction {
    
    private double alpha;

    public AlphaTrapezoidalFunction(int size) {
        super(size);
        this.alpha = 0;
    }

    public AlphaTrapezoidalFunction(double alpha, int size) {
        super(size);
        this.alpha = alpha;
    }

    public AlphaTrapezoidalFunction(double m1, double m2, EdgeType edge) {
        super(m1, m2, edge);
    }

    public AlphaTrapezoidalFunction(double m1, double m2, double m3) {
        super(m1, m2, m3);
    }

    public AlphaTrapezoidalFunction(double alpha, double m1, double m2, EdgeType edge) {
        super(m1, m2, edge);
        this.alpha = alpha;
    }

    public AlphaTrapezoidalFunction(double m1, double m2, double m3, double m4) {
        super(m1, m2, m3, m4);
    }

    public AlphaTrapezoidalFunction(double m1, double m2, double max, double min, EdgeType edge) {
        super(m1, m2, max, min, edge);
    }

    public AlphaTrapezoidalFunction(double alpha, double m1, double m2, double m3, double m4) {
        super(m1, m2, m3, m4);
        this.alpha = alpha;
    }

    public AlphaTrapezoidalFunction(double alpha, double m1, double m2, double max, double min, EdgeType edge) {
        super(m1, m2, max, min, edge);
        this.alpha = alpha;
    }

    public AlphaTrapezoidalFunction(double m1, double m2, double m3, double m4, double max, double min) {
        super(m1, m2, m3, m4, max, min);
    }

    public AlphaTrapezoidalFunction(double alpha, double m1, double m2, double m3, double m4, double max, double min) {
        super(m1, m2, m3, m4, max, min);
        this.alpha = alpha;
    }
        

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double getMembership(double x) {
        double mu = super.getMembership(x);
        return mu < alpha ? 0 : mu;
    }
    
    
    
    
}
