/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author xiaohui
 */
public class AnHashSet {

    private Set<Object> friendList = new HashSet<Object>();

    public Set<Object> getFriendList() {
        return friendList;
    }
    
    public void addFriendList(Object friend) {
        friendList.add(friend);
    }

    public void removeFriendList(Object friend) {
        friendList.remove(friend);
    }

    public int getFriendListNum() {
        return friendList.size();
    }
    
    public boolean contians(Object t) {
        return friendList.contains(t);
    }
}
