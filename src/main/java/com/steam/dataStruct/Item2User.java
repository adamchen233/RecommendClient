/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xiaohui
 */
public class Item2User {
    private String appid;
    private List<Object> users; 
    
    public String getAppid() {
        return appid;
    }
    public List<Object> getUsers() {
        return users;
    }
    
    public Item2User(String appid, User2Item[] user2Items){
        this.appid = appid;
        this.users = new ArrayList();
        for (User2Item user2Item : user2Items) {
            if (user2Item.containsItem(this.appid)) {
                this.users.add(user2Item.getSteamid());
            }
        }
    }
}
