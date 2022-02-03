/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.membershipfunctions;

import com.fdtkit.fuzzy.utils.DoublePoint;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class TrapezoidalFunction extends PiecewiseLinearFunction {

    public TrapezoidalFunction(int size) {
        this.points = new DoublePoint[size];
    }

    public TrapezoidalFunction(double m1, double m2, double m3, double m4, double max, double min) {
        this(4);
        this.points[0] = new DoublePoint(m1, min);
        this.points[1] = new DoublePoint(m2, max);
        this.points[2] = new DoublePoint(m3, max);
        this.points[3] = new DoublePoint(m4, min);
    }

    public TrapezoidalFunction(double m1, double m2, double m3, double m4) {
        this(m1, m2, m3, m4, 1.0, 0.0);
    }

    public TrapezoidalFunction(double m1, double m2, double m3, double max, double min) {
        this(3);
        this.points[0] = new DoublePoint(m1, min);
        this.points[1] = new DoublePoint(m2, max);
        this.points[2] = new DoublePoint(m3, min);
    }

    public TrapezoidalFunction(double m1, double m2, double m3 ) {
        this(m1, m2, m3, 1.0, 0.0);
    }

    public TrapezoidalFunction(double m1, double m2, double max, double min, EdgeType edge) {
        this(2);
        if(edge == EdgeType.Left) {
            this.points[0] = new DoublePoint( m1, min );
            this.points[1] = new DoublePoint( m2, max );
        }
        else {
            this.points[0] = new DoublePoint( m1, max );
            this.points[1] = new DoublePoint( m2, min );
        }
    }

    public TrapezoidalFunction(double m1, double m2, EdgeType edge) {
        this(m1, m2, 1.0, 0.0, edge);
    }
    
    public double getA() {
        return this.points[0].getX();
    }
    
    public double getB() {
        return this.points[1].getX();
    }    
    
    public double getC() {
        return this.points[2].getX();
    }    
    
    public double getD() {
        return this.points[3].getX();
    }    

    @Override
    public String toString() {
        return "Trapezodial Function";
    }
    
    
    
}
