/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam;

import com.steam.RecommendTool;
import com.steam.dataStruct.ItemSet;
import com.steam.dataStruct.User2Item;
import com.steam.dataStruct.UserSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xiaohui
 */
public class RunTest {

    public static int K = 30;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	String key = "Your Steam key";
        String targetSteamid = "The target Steamid";
        //传入目标ID，然后获取用户集（从目标ID好友网宽带优先搜索得的约500的ID）
        UserSet userSet = new UserSet(targetSteamid,key);
        userSet.getUserSet();

        //创建User2Items对象的数组，根据用户集循环遍历调用API获取各自的游戏
        User2Item[] user2Items = new User2Item[userSet.getUserHashSet().getFriendListNum()];
        int i = 0;
        for (Object user : userSet.getUserHashSet().getFriendList()) {
            System.out.print(i + "  --  ");
            user2Items[i] = new User2Item((String) user);
            user2Items[i].setUserItem();
            i++;
        }

        //根据User2Items对象数组获取不重复的itemSet
        ItemSet itemSet = new ItemSet(user2Items);
        
        
        
        //以上3个数据源【用户集】，【物品集】和【用户-物品个关系数组】
        //下面开始使用2种CF算法来获取推荐信息
        
        RecommendTool recommendTool = new RecommendTool(targetSteamid,userSet,user2Items,itemSet,key);
        
        //使用userCF算法获得推荐的物品
        recommendTool.recommnendByUserCF();
        
        //使用ItemCF算法获得推荐的物品
        //recommendTool.recommnendByItemCF();
        
        System.out.println();
        System.out.println("改结果是由" + itemSet.getItemHashSet().getFriendListNum() + "个游戏中选取的");
        

    }
}
