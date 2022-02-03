/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fdtkit.fuzzy.utils;

import com.fdtkit.fuzzy.data.Dataset;

/**
 *
 * @author MHJ
 */
public interface LeafDeterminer {
    LeafDescriptor getLeafDescriptor(Dataset dataset, String [] evidances);  
}
