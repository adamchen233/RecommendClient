/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam;

import com.steam.dataStruct.UserSet;
import com.steam.dataStruct.ItemSet;
import com.steam.dataStruct.Item2User;
import com.steam.dataStruct.User2Item;
import static com.steam.RunTest.K;
import com.steam.dataStruct.ItemHashSet;
import com.steam.dataStruct.ItemSimilarityHashSet;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author xiaohui
 */
public class RecommendTool {
    public static int NUM_OF_THE_RESULT = 10;
    
    private String targetSteamid;
    private UserSet userSet;
    private User2Item[] user2Items;
    private ItemSet itemSet;
    private SteamApiTool steamApiTool;
    private Item2User[] item2Users;
    private String key;

    public RecommendTool(String targetSteamid, UserSet userSet, User2Item[] user2Items, ItemSet itemSet,String key) {
        this.targetSteamid = targetSteamid;
        this.userSet = userSet;
        this.user2Items = user2Items;
        this.itemSet = itemSet;
        this.key = key;
        this.steamApiTool = new SteamApiTool();
    }

    public List<Map.Entry<String, Double>> recommnendByUserCF() {
        //使用userCF算法获得推荐的物品
        //1.由【用户-物品个关系数组】获取【物品-用户个关系数组】
        item2Users = new Item2User[itemSet.getItemHashSet().getFriendListNum()];

        int k = 0;
        for (Object itemID : itemSet.getItemHashSet().getFriendList()) {
            item2Users[k] = new Item2User((String) itemID, user2Items);
            k++;
        }

        //2.由【物品-用户个关系数组】获取userMatrix
        HashMap<String, Integer> targetHashMap = new HashMap();
        for (Object user : userSet.getUserHashSet().getFriendList()) {
            targetHashMap.put((String) user, 0);
        }
        for (Item2User item2User : item2Users) {
            if (item2User.getUsers().contains(targetSteamid)) {
                for (Object user : item2User.getUsers()) {
                    if (!targetSteamid.equals((String) user)) {
                        targetHashMap.put((String) user, targetHashMap.get(user) + 1);
                    }
                }
            }
        }

        //3.由userMatrix计算全部用户与目标用户的兴趣相似度（余弦相似度计算）
        HashMap<String, Integer> targetNumEachHashMap = new HashMap();
        HashMap<String, Double> targetIntersetHashMap = new HashMap();

        for (User2Item user2Item : user2Items) {
            targetNumEachHashMap.put(user2Item.getSteamid(), user2Item.getItems().size());
        }

        for (User2Item user2Item : user2Items) {
            if (!user2Item.getSteamid().equals(targetSteamid)) {
                String anotherSteamid = user2Item.getSteamid();
                double similarity = targetHashMap.get(anotherSteamid) / (Math.sqrt(targetNumEachHashMap.get(targetSteamid) * targetNumEachHashMap.get(anotherSteamid)));
                targetIntersetHashMap.put(anotherSteamid, similarity);
            }
        }

        //4.计算目标用户对所有物品（各自）的感兴趣程度
        HashMap<String, Double> targetIntersetHashMapFilter = new HashMap();

        List<Map.Entry<String, Double>> list_Data = new ArrayList<>(targetIntersetHashMap.entrySet());

        Collections.sort(list_Data, new ComparatorForSD());

        int n = 0;
        for (Map.Entry<String, Double> interset : list_Data) {
            if (!interset.getValue().equals(Double.NaN)) {
                targetIntersetHashMapFilter.put(interset.getKey(), interset.getValue());
                n++;
            }
            if (n >= K) {
                break;
            }
        }

        System.out.println(targetIntersetHashMapFilter);

        HashMap<String, Double> interest2Item = new HashMap();
        for (Object item : itemSet.getItemHashSet().getFriendList()) {
            double interest2thisItem = 0.0;
            double interestTemp = 0.0;
            int valueOfInterest = 0;
            for (Map.Entry<String, Double> intersetFilter : targetIntersetHashMapFilter.entrySet()) {
                for (User2Item user2Item : user2Items) {
                    if (user2Item.getSteamid().equals(intersetFilter.getKey()) && user2Item.containsItem(item)) {
                        for (Object[] itemAndValue : user2Item.getItems()) {
                            if (itemAndValue[0].equals(item)) {
                                valueOfInterest = (int) itemAndValue[1];
                            }
                        }
                        interestTemp = targetIntersetHashMapFilter.get(intersetFilter.getKey());
                    }
                }
                interest2thisItem += interestTemp * valueOfInterest;
            }
            interest2Item.put((String) item, interest2thisItem);
        }

        List<Map.Entry<String, Double>> finalTopN = new ArrayList<>(interest2Item.entrySet());

        Collections.sort(finalTopN, new ComparatorForSD());
        System.out.println();
        
        //
        
        //&filters=price_overview
        steamApiTool.setSteamApiTemplate("http://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key=" + key + "&format=xml&appid=");
        System.out.println("为id  " + targetSteamid + "  (UserCF算法)推荐的游戏列表以及推荐指数：");
        System.out.println("参数： K = " + K + 
                ", TODO_QUEUE_MAX_SIZE = " + userSet.TODO_QUEUE_MAX_SIZE + 
                ", NUM_OF_EACH_FRIEND_MAX = " + userSet.NUM_OF_EACH_FRIEND_MAX + 
                ", NUM_OF_THE_RESULT = " + NUM_OF_THE_RESULT);
        
        
        showResult(finalTopN);
        
        return finalTopN;
    }
    
    public void recommnendByItemCF() {
        //使用itemCF算法获得推荐的物品
        //1.由【用户-物品个关系数组】直接获取itemMatrix
        List<ItemHashSet> ItemHashSetList = new ArrayList();
        
        for (User2Item user2Item : user2Items) {
            if (user2Item.getSteamid().equals(targetSteamid)) {
                for(Object[] item : user2Item.getItems()){
                    ItemHashSetList.add(new ItemHashSet((String) item[0],new HashMap()));
                }
                break;
            }
        }
        for(ItemHashSet itemHashSet : ItemHashSetList){
            for(Object item : itemSet.getItemHashSet().getFriendList()){
                itemHashSet.getHashMap4ThisAppid().put((String) item, 0);
            }
        }
        for (ItemHashSet itemHashSet : ItemHashSetList) {
            for (User2Item user2Item : user2Items) {
                if (!user2Item.getSteamid().equals(targetSteamid)) {
                    for (Object item : itemSet.getItemHashSet().getFriendList()) {
                        if (user2Item.containsItem(itemHashSet.getAppid()) && user2Item.containsItem(item)) {
                            itemHashSet.getHashMap4ThisAppid().put((String) item, itemHashSet.getHashMap4ThisAppid().get((String)item) + 1);
                        }
                    }
                }
            }
        }
        
        
        //2.由直接获取itemMatrix计算物品之间的相似度（余弦相似度计算 + 未归一化）
        List<ItemSimilarityHashSet> itemSimilarityHashSetList = new ArrayList();
        for(ItemHashSet itemHashSet : ItemHashSetList){
            itemSimilarityHashSetList.add(new ItemSimilarityHashSet(itemHashSet.getAppid(), new HashMap()));
        }
        for(ItemSimilarityHashSet itemSimilarityHashSet : itemSimilarityHashSetList){
            for(Object item : itemSet.getItemHashSet().getFriendList()){
                int numOfTargetItemHave = 0;
                int numOfItemHave = 0;
                for(Item2User item2User : item2Users){
                    if(item2User.getAppid().equals(itemSimilarityHashSet.getAppid())){
                        numOfTargetItemHave = item2User.getUsers().size();
                    }
                    if(item2User.getAppid().equals(item)){
                        numOfItemHave = item2User.getUsers().size();
                    }
                }
                
                double itemSimilarity = Double.NaN;
                        
                for(ItemHashSet itemHashSet : ItemHashSetList){
                    if(itemHashSet.getAppid().equals(itemSimilarityHashSet.getAppid())){
                        itemSimilarity = itemHashSet.getHashMap4ThisAppid().get((String)item) / (Math.sqrt(numOfTargetItemHave * numOfItemHave));
                    }
                }
                itemSimilarityHashSet.getHashMap4ThisAppid().put((String) item, itemSimilarity);
            }
        }
        
        
//        System.out.println(itemSimilarityHashSetList.size());
//        for(ItemSimilarityHashSet itemSimilarityHashSet : itemSimilarityHashSetList){
//            System.out.println(itemSimilarityHashSet.getAppid());
//        }
        
        
        //3.计算目标用户对所有物品（各自）的感兴趣程度
        HashMap<String,Double> finalHashMap = new HashMap();
        
        for (Object item : itemSet.getItemHashSet().getFriendList()) {
            double itemInterest = 0;
            double itemInterestEach = Double.NaN;
            int valueOfInterest = 0;
            HashMap<String,Double> rowHashMap = new HashMap();
            
            for (ItemSimilarityHashSet itemSimilarityHashSet : itemSimilarityHashSetList) {
                rowHashMap.put(itemSimilarityHashSet.getAppid(), itemSimilarityHashSet.getHashMap4ThisAppid().get(item));
            }
            
            List<Map.Entry<String, Double>> listSimilarityRow = new ArrayList<>(rowHashMap.entrySet());
            Collections.sort(listSimilarityRow, new ComparatorForSD());

            int m = 0;
            for (Map.Entry<String, Double> TopK : listSimilarityRow) {
                itemInterestEach = TopK.getValue();
                for (User2Item user2Item : user2Items) {
                    for (Object[] itemArray : user2Item.getItems()) {
                        if (itemArray[0].equals(TopK.getKey())) {
                            valueOfInterest = (int) itemArray[1];
                            break;
                        }
                    }

                }

                itemInterest += itemInterestEach * valueOfInterest;

                m++;
                if (m >= K) {
                    break;
                }
            }
            

            finalHashMap.put((String) item, itemInterest);
        }

        List<Map.Entry<String, Double>> finalTopN = new ArrayList<>(finalHashMap.entrySet());
        Collections.sort(finalTopN, new ComparatorForSD());
        
        System.out.println("为id  " + targetSteamid + "  (ItemCF算法)推荐的游戏列表以及推荐指数：");
        System.out.println("参数： K = " + K + 
                ", TODO_QUEUE_MAX_SIZE = " + userSet.TODO_QUEUE_MAX_SIZE + 
                ", NUM_OF_EACH_FRIEND_MAX = " + userSet.NUM_OF_EACH_FRIEND_MAX + 
                ", NUM_OF_THE_RESULT = " + NUM_OF_THE_RESULT);
        showResult(finalTopN);
        
        
    } 
    
    
    private void showResult(List<Map.Entry<String, Double>> finalTopN){
        int m = 0;
        SAXReader reader = new SAXReader();
        for (Map.Entry<String, Double> topN : finalTopN) {
            for (User2Item user2Item : user2Items) {
                if (user2Item.getSteamid().equals(targetSteamid) && !user2Item.containsItem(topN.getKey())) {
//                        String xmlString = steamApiTool.executeApi(topN.getKey(), false);
//                        Document document = reader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
//                        Element root = document.getRootElement();

                    System.out.print("id: " + topN.getKey() + "  --  推荐指数：" + topN.getValue() + " -- ");
                    System.out.println("Url: http://store.steampowered.com/app/" + topN.getKey() + "/");
                    m++;
                }
            }
            if (m >= NUM_OF_THE_RESULT) {
                break;
            }
        }
    }
    
    
    class ComparatorForSD implements Comparator<Map.Entry<String, Double>> {

        @Override
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            if (o2.getValue() != null && o1.getValue() != null && o2.getValue().compareTo(o1.getValue()) > 0) {
                return 1;
            } else if (o2.getValue() != null && o1.getValue() != null && o2.getValue().compareTo(o1.getValue()) == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
