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
public class PiecewiseLinearFunction implements MembershipFunction {
   
    
    protected DoublePoint[] points;
    

    public PiecewiseLinearFunction() {
        this.points = null;
    }

    public PiecewiseLinearFunction(DoublePoint[] points) {

        if(points[0].getY() < 0 || points[0].getY() > 1) 
            throw new IllegalArgumentException("Y value of points must be in the range of [0, 1].");
        
        for(int i = 1; i < points.length; i++) {
            if(points[i].getY() < 0 || points[i].getY() > 1) 
                throw new IllegalArgumentException("Y value of points must be in the range of [0, 1].");            
            
            if(points[i - 1].getX() > points[i].getX()) 
                throw new IllegalArgumentException("Points must be in crescent order on X axis.");
        }
        
        this.points = points;
        
    }
    
    
    
    @Override
    public double getMembership(double x) {
        if(this.points.length == 0) 
            return 0.0d;
        if(x < this.points[0].getX())
            return this.points[0].getY();
        for(int i = 1; i < this.points.length; i++) {
            if(x <= this.points[i].getX()) {
                double y1 = this.points[i].getY();
                double y0 = this.points[i - 1].getY();
                double x1 = this.points[i].getX();
                double x0 = this.points[i - 1].getX();
                
                double m = (y1 - y0) / (x1 - x0);
                return m * (x - x0) + y0;
                
            }
        }
        
        return this.points[this.points.length - 1].getY();
    }

    @Override
    public double getLiftLimit() {
        return this.points[0].getX();
    }

    @Override
    public double getRightLimit() {
        return this.points[this.points.length - 1].getX();
    }
    
}
