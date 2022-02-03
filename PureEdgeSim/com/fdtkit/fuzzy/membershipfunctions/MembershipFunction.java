/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.membershipfunctions;

/**
 *
 * @author Mohammed H. Jabreel
 */
public interface MembershipFunction {

    double getMembership( double x );

    double getLiftLimit();
    
    double getRightLimit ();
}
