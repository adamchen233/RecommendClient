/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import com.steam.SteamApiTool;
import com.steam.dataStruct.AnHashSet;
import com.steam.dataStruct.Queue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author xiaohui
 */
public class UserSet {
    public static int TODO_QUEUE_MAX_SIZE = 300; //队列最大值，超过该值将停止抓取新用户
    public static int NUM_OF_EACH_FRIEND_MAX = 5; //从每个用户（目标用户除外）的好友抓取的最大用户数

    private Queue todoQueue;
    private AnHashSet userHashSet;
    private String targetSteamid;
    private SteamApiTool steamApiTool;
    private int flag;
    private String key;

    //Constructor
    public UserSet(String targetSteamid,String key) {
        flag = 0;
        todoQueue = new Queue();
        this.targetSteamid = targetSteamid;
        userHashSet = new AnHashSet();
        steamApiTool = new SteamApiTool();
        this.key = key;
    }

    public AnHashSet getUserHashSet() {
        return userHashSet;
    }

    public void getUserSet() {
        steamApiTool.setSteamApiTemplate("https://api.steampowered.com/ISteamUser/GetFriendList/v1/?key=" + key + "&format=xml&steamid=");
        todoQueue.enQueue(targetSteamid);
        while (!todoQueue.isQueueEmpty()) {
            String currentSteamid = (String) todoQueue.deQueue();
            if (todoQueue.getNum() <= TODO_QUEUE_MAX_SIZE) {
                if (!userHashSet.contians(currentSteamid)) {
                    userHashSet.addFriendList(currentSteamid);
                    String xmlString = steamApiTool.executeApi(currentSteamid,true);
                    if (!xmlString.equals("40X")) {
                        dealXml(xmlString, currentSteamid);
                    }
                }
            } else {
                while (!todoQueue.isQueueEmpty()) {
                    if (!userHashSet.contians(currentSteamid)) {
                        userHashSet.addFriendList(currentSteamid);
                    }
                    currentSteamid = (String) todoQueue.deQueue();
                }
            }
        }
        System.out.println("The hashSet size is: " + userHashSet.getFriendListNum());
    }



    private void dealXml(String xmlString, String currentSteamid) {
        int intFlag = 0;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            Element root = document.getRootElement();
            List<Element> friendElelemts = root.element("friends").elements();
            for (Element friend : friendElelemts) {
                //System.out.println(friend.elementText("steamid"));
                todoQueue.enQueue(friend.elementText("steamid"));
                intFlag++;
                if (intFlag > NUM_OF_EACH_FRIEND_MAX && currentSteamid != targetSteamid) {
                    break;
                }
            }
            flag++;
            System.out.println("第" + flag + "条id处理完，这个id有" + friendElelemts.size() + "个好友");
            System.out.println("现在队列里有" + todoQueue.getNum() + "条id需要处理\n");
        } catch (DocumentException ex) {
            Logger.getLogger(UserSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
