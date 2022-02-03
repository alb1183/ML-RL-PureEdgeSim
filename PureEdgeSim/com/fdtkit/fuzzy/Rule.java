/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy;

import com.fdtkit.fuzzy.operators.CoNorm;
import com.fdtkit.fuzzy.operators.Norm;
import com.fdtkit.fuzzy.operators.NotOperator;
import com.fdtkit.fuzzy.operators.UnaryOperator;
import com.fdtkit.fuzzy.operators.MaximumCoNorm;
import com.fdtkit.fuzzy.operators.MinimumNorm;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class Rule {
    
    // name of the rule 
    private String name;
    
    // the original expression
    private String rule;
    
    // the parsed RPN (reverse polish notation) expression
    private List<Object> rpnTokenList;
    
    // the consequento (output) of the rule
    private Clause output;

    // the database with the linguistic variables
    private Database database;
    
    // the norm operator
    private Norm normOperator;
    
    // the conorm operator
    private CoNorm conormOperator; 
    
    // the complement operator
    private UnaryOperator notOperator;
    
    // the unary operators that the rule parser supports
    private String unaryOperators = "NOT;VERY";

    public Rule(String name, String rule, Database database, Norm normOperator, CoNorm conormOperator) {
        this.name = name;
        this.rule = rule;
        this.database = database;
        this.normOperator = normOperator;
        this.conormOperator = conormOperator;
        
        this.rpnTokenList = new ArrayList<>();
        this.notOperator = new NotOperator();
        
        this.parseRule();
    }
    
    public Rule( Database fuzzyDatabase, String name, String rule ) {
        this(name, rule, fuzzyDatabase, new MinimumNorm( ), new MaximumCoNorm( ) );
    }
    
    public String getRPNExpression( ) {
        String result = "";
        for (Object o : rpnTokenList ) {
            // if its a fuzzy clause we can call clause's ToString()
            if ( o instanceof Clause ) {
                Clause c = (Clause) o;
                result += c.toString( );
            }
            else
                result += o.toString( );
            result += ", ";
        }
        result += "#";
        result = result.replace( ", #", "" );
        return result;
    }
    
    private int getPriority(String op) {
        if(unaryOperators.indexOf(op) >= 0) 
            return 4;
        switch(op) {
            case "(" :
                return 1;
            case "OR" :
                return 2;
            case "AND" :
                return 3;
        }
        return 0;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Clause getOutput() {
        return output;
    }

    private void parseRule() {
        // flag to incicate we are on consequent state
        boolean consequent = false;
        String upRule = rule.toUpperCase();
        if ( !upRule.startsWith( "IF" ) )
            throw new IllegalArgumentException( "A Fuzzy Rule must start with an IF statement." );
        if ( upRule.indexOf("THEN" ) < 0 )
            throw new IllegalArgumentException( "Missing the consequent (THEN) statement." );
        
        // building a list with all the expression (rule) string tokens
        String spacedRule = rule.replace( "(", " ( " ).replace( ")", " ) " );
        String[] tokensList = getRuleTokens( spacedRule );
        
        // stack to convert to RPN
        Stack<String> s = new Stack<String>( );
        // storing the last token
        String lastToken = "IF";
        // linguistic var read, used to build clause
        LinguisticVariable lingVar = null;
        
        for(int i = 0; i < tokensList.length; i++) {
            
            // removing spaces
            String token = tokensList[i].trim( );
            // getting upper case
            String upToken = token.toUpperCase();
            // ignoring these tokens
            if ( "".equals(upToken) || "IF".equals(upToken) ) continue;
     
            // if the THEN is found, the rule is now on consequent
            if ( "THEN".equals(upToken) ) {
                lastToken = upToken;
                consequent = true;
                continue;
            }
            
            // if we got a linguistic variable, an IS statement and a label is needed
            if ( lastToken == "VAR" ) {
                if ( upToken == "IS" )
                    lastToken = upToken;
                else
                    throw new IllegalArgumentException( "An IS statement is expected after a linguistic variable." );
            }
            // if we got an IS statement, a label must follow it
            else if ( "IS".equals(lastToken) ) {
                try {
                    FuzzySet fs = lingVar.getLabel( token );
                    Clause c = new Clause( lingVar, fs );
                    if(consequent) {
                        output = c;
                    }
                    else {
                        rpnTokenList.add( c );
                    }
                    lastToken = "LAB";
                    
                }
                catch ( IllegalArgumentException ex) {
                    throw new IllegalArgumentException( "Linguistic label " + token + " was not found on the variable " + lingVar.getName() + "." );
                }
            }
            // not VAR and not IS statement
            else {
                // openning new scope
                if ( "(".equals(upToken) ) {
                    // if we are on consequent, only variables can be found
                    if ( consequent )
                        throw new IllegalArgumentException( "Linguistic variable expected after a THEN statement." );
                    // if its a (, just push it
                    s.push( upToken );
                    lastToken = upToken;
                }
                // operators
                else if( "AND".equals(upToken) || "OR".equals(upToken) || unaryOperators.contains(upToken)) {
                    // if we are on consequent, only variables can be found
                    if ( consequent )
                        throw new IllegalArgumentException( "Linguistic variable expected after a THEN statement." );
                    // pop all the higher priority operators until the stack is empty 
                    while ( ( s.size() > 0 ) && ( getPriority( s.peek() ) > getPriority( upToken ) ) )
                        rpnTokenList.add( s.pop( ) );
                    
                    // pushing the operator 
                    s.push( upToken );
                    lastToken = upToken;
                }
                // closing the scope
                else if ( ")".equals(upToken) ) {
                    // if we are on consequent, only variables can be found
                    if ( consequent ) {
                        throw new IllegalArgumentException( "Linguistic variable expected after a THEN statement." );
                    }
                    // if there is nothing on the stack, an oppening parenthesis is missing.
                    if(s.size() == 0) {
                        throw new IllegalArgumentException( "Openning parenthesis missing." );
                    }
                    
                    // pop the tokens and copy to output until openning is found 
                    while(!"(".equals(s.peek())) {
                        rpnTokenList.add( s.pop( ) );
                        if(s.size() == 0) {
                            throw new IllegalArgumentException( "Openning parenthesis missing." );
                        }
                    }
                    s.pop();
                    // saving last token...
                    lastToken = upToken;
                }
                // finally, the token is a variable
                else {
                    try {
                        lingVar = database.getVariable( token );
                        lastToken = "VAR";
                    }
                    catch(IllegalArgumentException ex) {
                        throw new IllegalArgumentException( "Linguistic variable " + token + " was not found on the database." );
                    }
                }
            }
        }
        
            
        // popping all operators left in stack
        while(s.size() > 0) {
            rpnTokenList.add(s.pop());
        }        
    }

    private String[] getRuleTokens(String spacedRule) {
        String[] tokens = spacedRule.split(" ");
        // looking for unary operators
        for ( int i = 0; i < tokens.length; i++ ) {
            // if its unary and there is an "IS" token before, we must change positions
            if ( ( unaryOperators.contains(tokens[i].toUpperCase()) ) &&
                     ( i > 1 ) && ( "IS".equals(tokens[i - 1].toUpperCase()) ) ) {
                // placing VAR name
                tokens[i - 1] = tokens[i - 2];
                tokens[i - 2] = tokens[i];
                tokens[i] = "IS";

            }
        }
        return tokens;
    }
    
    
    public double evaluateFiringStrength( ) {
        // Stack to store the operand values
        Stack<Double> s = new Stack<Double>( );
        
        // Logic to calculate the firing strength
        for(Object o : rpnTokenList) {
            
            // if its a clause, then its value must be calculated and pushed
            if(o instanceof Clause) {
                Clause c = (Clause)o;
                s.push(c.evaluate());
            }
            // if its an operator (AND / OR) the operation is performed and the result 
            // returns to the stack            
            else {
                double y = s.pop();
                double x = 0;
                // unary pops only one value
                if ( unaryOperators.indexOf( o.toString( ) ) < 0 )
                    x = s.pop();
                // operation
                switch ( o.toString( ) ) {
                    case "AND":
                        s.push( normOperator.evaluate( x, y ) );
                        break;
                    case "OR":
                        s.push( conormOperator.evaluate( x, y ) );
                        break;
                    case "NOT":
                        s.push(notOperator.evaluate(y));
                        break;
                }
            }
        }
        
        // result on the top of stack
        return s.pop();
    }

    
    
    
}
