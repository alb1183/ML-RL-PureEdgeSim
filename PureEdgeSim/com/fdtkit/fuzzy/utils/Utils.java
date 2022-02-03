/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

import java.util.Arrays;

/**
 *
 * @author Mohammed H. Jabreel
 */
public class Utils {
    
    public static double vagueness(double[] vals) {
        double vag = 0;
        for(double v : vals) {
            vag += (v * ln(v) + (1 - v) * ln(1 - v));
        }
        if (vag == 0)  {
            return 0;
        }
        vag /= (vals.length);
        return vag * -1;
    }
    
    public static double getMax(double[] arr) {
        double max = arr[0];
        for(double a : arr) {
            if(a > max) {
                max = a;
            }
        }
        
        return max;
    }
    
    public static double ln(double x) {
        if (x == 0) {
            return 0;
        }
        return Math.log(x) / Math.log(2);
    }
    
    public static void normalizeWith(double[] arr, double v) {
        for(int i = 0; i < arr.length; i++) {
            arr[i] /= v;
        }
    }
    
    public static double subSetHood(double[] a, double[] b) {
        double s = sum(a);
        if(s == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return sumOfMinimum(a, b) / sum(a);
    }
    
    public static double sum(double [] a) {
        double s = 0.0;
        for(double v : a) {
            s+= v;
        }
        return s;
    }
    
    public static double average(double [] a) {
        return sum(a) / a.length;
    }
    
    public static double ambiguity(double [] a) {
        
        int n = a.length;
        Arrays.sort(a);
        double[] ap = new double[n + 1];
        for(int i = 0; i <n;i++) {
            ap[i] = a[n - i - 1];
        }
        double s = 0;
        for(int i = 0; i <n; i++) {
            s += (ap[i] - ap[i + 1]) * Math.log(i + 1);
        }
        
        return s;
    }
    
    public static double normalizedTermMu(double ui, double[] uis) {
        double max = getMax(uis);
        return ui / max;
    }
    
    public static double ambiguityOfAttribute() {
        return 0;
    }
    
    public static double ambiguity(double [] a, double alpha) {
        int n = a.length;
        for(int i = 0; i < n; i++) {
            a[i] = a[i] < alpha ? 0 : a[i];
        }
        Arrays.sort(a);
        Utils.normalizeWith(a, a[n - 1]);

        double s = a[0] * Math.log((double)n);
        for(int j = n - 1, k = 1; j > 0; j--, k++) {
            s += (a[j] - a[j - 1]) * Math.log(k);
        } 
        return s;
    }    
    
    public static double sumOfMinimum(double[] a, double[] b) {
        
        if(a.length != b.length) {
            throw new IndexOutOfBoundsException("The length of two arrays must be equal");
        }
        
        double s = 0.0;
        for(int i = 0, n = a.length; i < n; i++) {
            s += a[i] < b[i] ? a[i] : b[i];
        }
        
        return s;
        
    }
    
    public static double[] min(double[] a, double[] b) {
        if(a.length != b.length) {
            throw new IndexOutOfBoundsException("The length of two arrays must be equal");
        }
        
        int n  = a.length;
        double[] c = new double[n];
        for(int i = 0; i < n; i++) {
            c[i] = a[i] < b[i] ? a[i] : b[i];
        }        
        
        return c;
    }
    
    public static Double [] toDoubleArray(String[] arr) {
        int n = arr.length;
        Double[] da = new Double[n];
        int i = 0;
        for(String e : arr) {
            da[i++] = Double.parseDouble(e);
        }
        return da;
    }  
    
    public static Double [] toDoubleArray(String[] arr, int startFrom) {
        int n = arr.length;
        Double[] da = new Double[n - startFrom];
        int i = 0;
        for(int k = startFrom; k < n; k++) {
            da[i++] = Double.parseDouble(arr[k]);
        }
        return da;
    }  
    public static Double [] toDoubleArray(String[] arr, int startFrom, int to) {
        int n = arr.length;
        Double[] da = new Double[to - startFrom + 1];
        int i = 0;
        for(int k = startFrom; k <= to; k++) {
            da[i++] = Double.parseDouble(arr[k]);
        }
        return da;
    } 
    
    public static double entropy(double[] vals) {
        double entropy = 0;
        for(double v : vals) {
            entropy += v * ln(v);
        }
        return -entropy;
    }

/*    
    
    public static void loadAndFuzzifyData(String fileName, String fuzzyMembershipFileName, Database dbase, Dataset dataset) {
        File f = new File(fuzzyMembershipFileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            while((line = br.readLine()) != null) {
                if(line.startsWith("#LV")) {
                    String[] lvDetails = line.substring(4).split(" ");
                    String line2 = "";
                    LinguisticVariable lv = null;
                    
                    switch(lvDetails[1]) {
                        case "Numerical":
                            lv = new LinguisticVariable(lvDetails[0], Double.parseDouble(lvDetails[2]), Double.parseDouble(lvDetails[3]));
                            
                            while(true) {
                                line2 = br.readLine();
                                if(line2 == null || line2.startsWith(";")) {
                                    break;
                                }
                                String[] fsDetails = line2.trim().split(" ");
                                String[] arg = fsDetails[1].split(",");
                                TrapezoidalFunction fuzzyFunction = null;
                                if(arg.length == 4) {
                                    fuzzyFunction = new TrapezoidalFunction( Double.parseDouble(arg[0]), //a 
                                                                                                Double.parseDouble(arg[1]), //b
                                                                                                Double.parseDouble(arg[2]), //c
                                                                                                Double.parseDouble(arg[3]) //d
                                                                            );                                    
                                }
                                else if(arg.length == 3) {
                                    fuzzyFunction = new TrapezoidalFunction( Double.parseDouble(arg[0]), Double.parseDouble(arg[1]), Double.parseDouble(arg[2]));                                            

                                }
                                else if(arg.length == 2) {
                                    if(fsDetails.length == 3) {
                                        if("RE".equals(fsDetails[2])) {
                                            fuzzyFunction = new TrapezoidalFunction( Double.parseDouble(arg[0]), Double.parseDouble(arg[1]), EdgeType.Right);                                            
                                        }
                                        else if("LE".equals(fsDetails[2])) {
                                            fuzzyFunction = new TrapezoidalFunction( Double.parseDouble(arg[0]), Double.parseDouble(arg[1]), EdgeType.Left);                                            
                                        }
                                         
                                    }
                                    else {
                                        fuzzyFunction = new TrapezoidalFunction( Double.parseDouble(arg[0]), //a 
                                                                                                    Double.parseDouble(arg[1]), //b
                                                                                                    Double.parseDouble(arg[2])
                                                                                );                                         
                                    }                                    
                                }

                                FuzzySet fs = new FuzzySet( fsDetails[0], fuzzyFunction );
                                lv.addLabel(fs);
                            }
                            
                            dbase.addVariable(lv);
                            
                            // add an attribute to the data set
                            dataset.addAttribute(new Attribute(lv.getName(), lv.getLableNames()));
                            break;
                        case "Categorical":
                            lv = new LinguisticVariable(lvDetails[0], 0, 10);
                            
                            while(true) {
                                line2 = br.readLine();
                                if(line2 == null || line2.startsWith(";")) {
                                    break;
                                }
                                String[] fsDetails = line2.trim().split(" ");
                                
                                SingletonFunction fuzzyFunction = new SingletonFunction(Double.parseDouble(fsDetails[1]));
                                FuzzySet fs = new FuzzySet( fsDetails[0], fuzzyFunction );
                                lv.addLabel(fs);
                            }  
                            dbase.addVariable(lv);
                            // add an attribute to the data set
                            dataset.addAttribute(new Attribute(lv.getName(), lv.getLableNames()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown data type of LV " + lvDetails[1]);
                    }
                }
                else {
                    throw new IllegalArgumentException("Pad format (Uknown term)!!!" );                    
                }
            }
            
            //Read Crisp Data
            f = new File(fileName);
            br = new BufferedReader(new FileReader(f));
            line = br.readLine();
            String[] attrNames = line.split(CSV_SEPRATOR);
            
            
            while((line = br.readLine()) != null) {
                String[] vals = line.split(CSV_SEPRATOR);
                Double[] dVals = Utils.toDoubleArray(vals, 1, vals.length - 1);
                Row row = new Row(dVals, dataset);
                dataset.addRow(row);
                // fuzzyfication
                for(int i = 0, k = 1; i < dVals.length; i++, k++) {
                    List<String>  terms = dataset.getAttribute(attrNames[k]).getLinguisticTerms();
                    for(String term : terms) {
                        double fuzzyValue = dbase.getVariable(attrNames[k]).getLabelMembership(term, dVals[i].doubleValue());
                        row.setFuzzyValue(attrNames[k], term, fuzzyValue);
                    }
                }
                        
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Task1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Task1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
*/ 
    
    
}
