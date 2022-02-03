/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class DoublePoint {
    
    private double x;
    
    private double y;

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public double distanceTo(DoublePoint point) {
        double dx = this.x - point.x;
        double dy = this.y - point.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public DoublePoint add(DoublePoint point) {
        return new DoublePoint(this.x + point.x, this.y + point.y);
    }
    
    public DoublePoint sub(DoublePoint point) {
        return new DoublePoint(this.x - point.x, this.y - point.y);
    }    
    
    public DoublePoint multiply(double factor) {
        return new DoublePoint(this.x * factor, this.y * factor);
    }    
    
    public DoublePoint divide(double factor) {
        return new DoublePoint(this.x / factor, this.y / factor);
    }    

    @Override
    public String toString() {
        return String.format("{0} , {1}", this.x, this.y);
    }
    
    
    
    
}
