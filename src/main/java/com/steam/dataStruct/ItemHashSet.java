/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author xiaohui
 */
public class ItemHashSet {
    private String appid;
    private HashMap<String, Integer> hashMap4ThisAppid;

    public String getAppid() {
        return appid;
    }

    public HashMap<String, Integer> getHashMap4ThisAppid() {
        return hashMap4ThisAppid;
    }
    
    public ItemHashSet(String appid, HashMap<String, Integer> hashMap4ThisAppid){
        this.appid = appid;
        this.hashMap4ThisAppid = hashMap4ThisAppid;
    }
}
