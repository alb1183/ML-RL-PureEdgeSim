/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.fuzzydt;

import com.fdtkit.fuzzy.data.Dataset;
import com.fdtkit.fuzzy.utils.LeafDescriptor;
import com.fdtkit.fuzzy.utils.LeafDeterminer;
import com.fdtkit.fuzzy.utils.PreferenceMeasure;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MHJ
 */
public class FuzzyDecisionTree  {


    
    
    protected PreferenceMeasure preferenceMeasure;
    protected LeafDeterminer leafDeterminer;

    public FuzzyDecisionTree(PreferenceMeasure preferenceMeasure, LeafDeterminer leafDeterminer) {
        this.preferenceMeasure = preferenceMeasure;
        this.leafDeterminer = leafDeterminer;
    }

    public TreeNode buildTree(Dataset dataset) {
        String[] attrs = new String[dataset.getAttributesCount() - 1];
        
        for(int i = 0; i < dataset.getAttributesCount(); i++) {
            if(!dataset.getAttribute(i).getName().equals(dataset.getClassName())) {
                attrs[i] = dataset.getAttribute(i).getName();
            }
        }
        
        return growTree(dataset, attrs, null);         
    }

    protected TreeNode growTree(Dataset dataset, String[] attrs, String [] args) {
        if(attrs.length == 0) {
            return new TreeNode(NodeType.LEAF, "UnKnown");
        }
        String className = dataset.getClassName();
        if(args == null) {
            
            String bestAttr = preferenceMeasure.getBestAttribute(dataset);
            TreeNode root = new TreeNode(NodeType.ATTRIBUTE, bestAttr);
            List<String> terms = dataset.getAttribute(bestAttr).getLinguisticTerms();
            List<String> classTerms = dataset.getAttribute(className).getLinguisticTerms(); 
            
            for(String term : terms) {
                boolean canBelongeToClass = false;
                double bestDOT = Double.MIN_VALUE;
                String bestClass = "";
                for (String classTerm : classTerms) {
                    LeafDescriptor ld = leafDeterminer.getLeafDescriptor(dataset, new String[] {bestAttr, term, className, classTerm });
                    if(ld.isLeaf()) {
                        canBelongeToClass = true;
                        if(ld.getDegreeOfTruth() > bestDOT) {
                            bestDOT = ld.getDegreeOfTruth();
                            bestClass = classTerm;
                        }                
                    }
                }
                if(canBelongeToClass) {
                    TreeNode c = new TreeNode(NodeType.VALUE, term);
                    TreeNode c2 = new TreeNode(NodeType.LEAF, bestClass);
                    c2.setValue(bestDOT);
                    c.addChild(c2);
                    root.addChild(c);                    
                }
                else {
                    //Remove the best attribute
                    String[] newAttrs = new String[attrs.length - 1];
                    for(int j = 0, k = 0; j < attrs.length; j++) {
                        if(!attrs[j].equals(bestAttr)) {
                            newAttrs[k++] = attrs[j];
                        }
                    }

                    //get the new args
                    String[] newArgs = new String[2];
                    newArgs[0] = bestAttr;
                    newArgs[1] = term;
                    

                    TreeNode c = growTree(dataset, newAttrs, newArgs);
                    if(c != null) {
                        TreeNode node = new TreeNode(NodeType.VALUE, term);
                        root.addChild(node);                        
                        node.addChild(c);
                    }
                   
                }                
            }
            return root;
        }
        else {
            String bestAttr = "";
            if(attrs.length > 1) {
                
                bestAttr = preferenceMeasure.getBestAttribute(dataset, attrs, args);
                

                if("".equals(bestAttr)) {
                    
                    String[] args1 = new String[args.length + 2];
                    System.arraycopy(args, 0, args1, 0, args.length);
                    args1[args.length] = className;
                    List<String> classTerms = dataset.getAttribute(className).getLinguisticTerms();
                    String bestClass = "";
                    double bestDOT = -1;
                    boolean canBeClass = false;
                    for (String classTerm : classTerms) {
                        args1[args.length + 1] = classTerm;                    
                        LeafDescriptor ld = leafDeterminer.getLeafDescriptor(dataset, args1);
                        if(ld.getDegreeOfTruth() > bestDOT) {
                            bestDOT = ld.getDegreeOfTruth();
                            bestClass = classTerm;
                            canBeClass = ld.isLeaf();
                        }
                    }
                    if(canBeClass) {
                        TreeNode c2 = new TreeNode(NodeType.LEAF, bestClass);
                        c2.setValue(bestDOT);
                        return c2;
                    }
                    else {
                        return null;
                    }
                }
                else {
                    TreeNode root = new TreeNode(NodeType.ATTRIBUTE, bestAttr);
                    List<String> terms = dataset.getAttribute(bestAttr).getLinguisticTerms();
                    List<String> classTerms = dataset.getAttribute(className).getLinguisticTerms();
                    String [] args2 = new String[args.length + 4];
                    System.arraycopy(args, 0, args2, 2, args.length);
                    args2[0] = bestAttr;
                    args2[args2.length - 2] = className;
                
                    for(String term : terms) {
                        boolean canBelongeToClass = false;
                        args2[1] = term;
                        for (String classTerm : classTerms) {
                            args2[args2.length - 1] = classTerm;
                            LeafDescriptor ld = leafDeterminer.getLeafDescriptor(dataset, args2);
                            if (ld.isLeaf()) {
                                TreeNode c = new TreeNode(NodeType.VALUE, term);
                                TreeNode c2 = new TreeNode(NodeType.LEAF, classTerm);
                                c2.setValue(ld.getDegreeOfTruth());
                                c.addChild(c2);
                                root.addChild(c);
                                canBelongeToClass = true;
                                break;
                            }                         
                        }

                        if(!canBelongeToClass) {
                            //Remove the best attribute
                            String[] newAttrs = new String[attrs.length - 1];
                            for(int j = 0, k = 0; j < attrs.length; j++) {
                                if(!attrs[j].equals(bestAttr)) {
                                    newAttrs[k++] = attrs[j];
                                }
                            }
                            //get the new args
                            String[] newArgs = new String[args.length + 2];
                            System.arraycopy(args, 0, newArgs, 2, args.length);
                            newArgs[0] = bestAttr;
                            newArgs[1] = term;


                            TreeNode c = growTree(dataset, newAttrs, newArgs);
                            if(c != null) { 
                                TreeNode node = new TreeNode(NodeType.VALUE, term);
                                root.addChild(node);                                
                                node.addChild(c);
                            }

                        }            

                    } 
                
                    return root;
                }
            }
            else {
                bestAttr = attrs[0];
                TreeNode root = new TreeNode(NodeType.ATTRIBUTE, bestAttr);
                List<String> terms = dataset.getAttribute(bestAttr).getLinguisticTerms();
                List<String> classTerms = dataset.getAttribute(className).getLinguisticTerms();
                String [] args2 = new String[args.length + 4];
                System.arraycopy(args, 0, args2, 2, args.length);
                args2[0] = bestAttr;
                args2[args2.length - 2] = className;
                boolean canBeClass = false;
                for(String term : terms) {
                    LeafDescriptor ld;
                    args2[1] = term;
                    double maxTruth = Double.MIN_VALUE;
                    String bestClass = "";
                    
                    for(String classTerm : classTerms) {
                        args2[args2.length - 1] = classTerm;
                        ld = leafDeterminer.getLeafDescriptor(dataset, args2);
                        if (ld.getDegreeOfTruth() > maxTruth) {
                            maxTruth = ld.getDegreeOfTruth();
                            bestClass = classTerm;
                            canBeClass = ld.isLeaf();
                        }                          
                    }
                    if(canBeClass) {
                        TreeNode node = new TreeNode(NodeType.VALUE, term);
                        root.addChild(node);
                        TreeNode c = new TreeNode(NodeType.LEAF, bestClass);

                        c.setValue(maxTruth);
                        node.addChild(c);                        
                    }
                }
                if(root.getChildrenCount() > 0) {
                    return root;
                }
                else {
                    return null;
                }
            }
        }
               
        
    }
    
    public void printTree(TreeNode root, String tabs) {
        if(root.getNodeType() == NodeType.ATTRIBUTE ) {
            System.out.println(tabs + "|" + root.getTitle() + "|");
        }
        List<TreeNode> children = root.getChildren();
        for(int i =0; i < root.getChildrenCount(); i++) {
            TreeNode node = children.get(i);
            if(node.isLeaf()) {
                System.out.println(tabs + "\t\t" + "[" + node.getTitle() + "](" + String.format("%.2f", node.getValue()) + ")");
            }
            else if(node.getNodeType() == NodeType.VALUE) {
                System.out.println(tabs + "\t" + "<" + node.getTitle() + ">");
                printTree(node, "\t" + tabs);
            }
            else {
                printTree(node, "\t" + tabs);
            }
            
            
        }
    }
    
    public double getAverageofAccuracy(TreeNode root) {
        if(root.isLeaf()) {
            return root.getValue();
        }
        double s = 0;
        List<TreeNode> children = root.getChildren();
        for(int i =0; i < root.getChildrenCount(); i++) {
            TreeNode node = children.get(i);
             if(node.getNodeType() == NodeType.VALUE) {
            }
            else {
                s += getAverageofAccuracy(node);
            }
        }  
        return s;
    }
    
    public void saveTreeToFile(TreeNode root, String tabs, String fileName) {
        FileWriter fw = null;
        try {
            String s = getStringTree(root, tabs);
            File f = new File(fileName);
            fw = new FileWriter(f);
            fw.write(s, 0, s.length());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FuzzyDecisionTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FuzzyDecisionTree.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(FuzzyDecisionTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    } 
    
    private String getStringTree(TreeNode root, String tabs) {
        String s = "";
        if(root.getNodeType() == NodeType.ATTRIBUTE ) {
            s += (tabs + "|" + root.getTitle() + "|\r\n");
        }
        List<TreeNode> children = root.getChildren();
        
        for(int i =0; i < root.getChildrenCount(); i++) {
            TreeNode node = children.get(i);
            if(node.isLeaf()) {
                s+=(tabs + "\t\t" + "[" + node.getTitle() + "](" + String.format("%.2f", node.getValue()) + ")") + "\r\n";
            }
            else if(node.getNodeType() == NodeType.VALUE) {
                s += tabs + "\t" + "<" + node.getTitle() + ">\r\n";
                s += getStringTree(node, "\t" + tabs);
            }
            else {
                s+= getStringTree(node, "\t" + tabs);
            }
            
            
        }
        return s;
    }
    
    public double getAccuracy(String[] rules) {
        double s = 0;
        
        for(String rule: rules) { 
            double v = Double.parseDouble(rule.substring(rule.indexOf("(") + 1, rule.length() - 1).replace(",", "."));
            s += v * 100;
        }
        
        return s / rules.length;
        
    }
    
    public String[] generateRules(TreeNode node) {

        String rules = generateRules(node, "");
        return rules.substring(0, rules.length() - 1).split("\n");            
        
    }
    
    private String generateRules(TreeNode node, String prefix) {
        if(node == null) {
            return null;
        }
        if(node.isLeaf()) {
            return String.format("%sTHEN %s (%.4f)\n" , prefix, node.getTitle(), node.getValue());
        }
        else if(node.isRoot()) {
            String rules = "";
            List<TreeNode> childs = node.getChildren();
            for(TreeNode child : childs) {
                rules += generateRules(child.getChildren().get(0), String.format("IF %s IS %s ", node.getTitle(), child.getTitle()));
            }

            return rules;
        }
        else if(node.getNodeType() == NodeType.ATTRIBUTE) {
            String rules = "";
            List<TreeNode> childs = node.getChildren();
            for(TreeNode child : childs) {
                rules += generateRules(child.getChildren().get(0), prefix + String.format("AND %s IS %s ", node.getTitle(), child.getTitle()));

            }

            
            return rules;                       
        }
        return "";
    }    
    
//    public String simplifyRules() {
//        return null;
//    }
    
//    public String simplifyRule(Dataset dataset, String rule, String className) {
//        
//        String terms = rule.substring(3, rule.indexOf(" THEN")).trim();
//        String cls = rule.substring(rule.indexOf("THEN") + 5, rule.indexOf("(")).trim();
//        
//        String[] condations = terms.split(" AND ");
//        String [] evidinces = new String[condations.length * 2 + 2];
//        int i = 0;
//        for(int k = 0; k < condations.length; i+=2, k++) {
//            String[] condation = condations[k].split(" IS ");
//            evidinces[i] = condation[0];
//            evidinces[i + 1] = condation[1];
//        }
//        evidinces[i] = className;
//        evidinces[i + 1] = cls;
//
//        
//        double dot = leafDeterminer.getLeafDescriptor(dataset, evidinces).getDegreeOfTruth();
//        double dot2 = dot;
//        boolean isSimplified = false;
//        while((dot >= this.truthLevel && dot >= dot2) && evidinces.length >= 6) {
//            String[] newEvidinces = new String[evidinces.length - 2];
//            newEvidinces[newEvidinces.length - 2] = evidinces[evidinces.length - 2];
//            newEvidinces[newEvidinces.length - 1] = evidinces[evidinces.length - 1];
//            i = 0;
//            for(; i < evidinces.length - 2; i+=2) {
//                for(int k = 0, j = 0; k < evidinces.length - 2; k+=2) {
//                    if(k != i) {
//                        newEvidinces[j] = evidinces[k];
//                        newEvidinces[j + 1] = evidinces[k + 1];
//                        j += 2;
//                    }
//                }
//                
//                dot = degreeOfClassificationTruth(dataset, newEvidinces);
//                if(dot > this.truthLevel && dot > dot2) {
//                    isSimplified = true;
//                    break;
//                }
//            }            
//            
//            if(isSimplified) {
//                evidinces = newEvidinces;
//            }
//            else
//                break;
//        } 
//        if(isSimplified) {
//            String newRule = "IF " + evidinces[0] + " IS " + evidinces[1];
//            for (i = 2; i < evidinces.length - 2; i+= 2) {
//                newRule += " AND " + evidinces[i] + " IS " + evidinces[i + 1];
//            }
//            newRule += " THEN " + cls + String.format(" (%.2f)", dot);
//        
//            return newRule;
//        }
//        else {
//            return rule;
//        }
//    }
    
    public double[] classify(int inputIdx, Dataset dataset, String className, String[] rules) {
    	
        List<String> classTerms = dataset.getAttribute(className).getLinguisticTerms();
        double[] classVals = new double[classTerms.size()];
        for(String rule : rules) {
            String terms = rule.substring(3, rule.indexOf(" THEN")).trim();
            String cls = rule.substring(rule.indexOf("THEN") + 5, rule.indexOf("(")).trim();
            int classIdx = dataset.getAttribute(className).getLinguisticTermIndex(cls);
            String[] condations = terms.split(" AND ");
            String [] evidinces = new String[condations.length * 2];        
            
            for(int k = 0, i = 0; k < condations.length; i+=2, k++) {
                String[] condation = condations[k].split(" IS ");
                evidinces[i] = condation[0];
                evidinces[i + 1] = condation[1];
            }     
            double v = dataset.getFuzzyValue(inputIdx, evidinces[0], evidinces[1]);

          
            for(int i = 2; i < evidinces.length; i+=2) { 
                v = Math.min(v, dataset.getFuzzyValue(inputIdx, evidinces[i], evidinces[i + 1])); 
            }   
            classVals[classIdx] = Math.max(v, classVals[classIdx]);
        }
        return classVals;
    }    


}
