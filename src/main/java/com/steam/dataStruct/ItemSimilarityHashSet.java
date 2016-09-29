/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import java.util.HashMap;

/**
 *
 * @author xiaohui
 */
public class ItemSimilarityHashSet {
    private String appid;
    private HashMap<String, Double> hashMap4ThisAppid;

    public String getAppid() {
        return appid;
    }

    public HashMap<String, Double> getHashMap4ThisAppid() {
        return hashMap4ThisAppid;
    }
    
    public ItemSimilarityHashSet(String appid, HashMap<String, Double> hashMap4ThisAppid){
        this.appid = appid;
        this.hashMap4ThisAppid = hashMap4ThisAppid;
    }
}
