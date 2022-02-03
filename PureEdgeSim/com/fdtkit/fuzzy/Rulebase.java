/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.stream.*;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class Rulebase {
    
    private Dictionary<String, Rule> rules;

    public Rulebase() {
        this.rules = new Hashtable<>(20);
    }
    
    public void addRule(Rule rule) {
        if(rules.get(rule.getName()) != null) {
            throw new IllegalArgumentException( "The fuzzy rule name already exists in the rulebase." );
        }
        this.rules.put(rule.getName(), rule);
    }
    
    public void clearRules() {
        this.rules = new Hashtable<>(20);
    }
    
    public Rule getRule(String ruleName) {
        return rules.get(ruleName);
    }
    
    public Rule[] getRules() {
        Rule[] r = new Rule[rules.size()];
        int i = 0;
        //Iterator it = (Iterator) this.rules.elements();
        while(this.rules.elements().hasMoreElements()) {
            r[i++] = this.rules.elements().nextElement();
        }
        return r;
    }
    
    
}
