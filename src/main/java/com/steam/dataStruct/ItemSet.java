/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import com.steam.dataStruct.AnHashSet;

/**
 *
 * @author xiaohui
 */
public class ItemSet {
    public AnHashSet itemHashSet;

    public AnHashSet getItemHashSet() {
        return itemHashSet;
    }
    
    public ItemSet(User2Item[] User2Items){
        itemHashSet = new AnHashSet();
        for(int i = 0; i < User2Items.length; i ++){
            for(Object[] item : User2Items[i].getItems()){
                if(!itemHashSet.contians(item[0])){
                    itemHashSet.addFriendList(item[0]);
                }
            }
        }
        
    } 
}
