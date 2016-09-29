/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam.dataStruct;

import com.steam.SteamApiTool;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
import static java.lang.Thread.sleep;

/**
 *
 * @author xiaohui
 */
public class User2Item {
    public static int SLEEP_TIME = 500;

    private String steamid;
    private SteamApiTool steamApiTool;
    private String key = "Your Steam key";
    
    private List<Object[]> items; //list元素代表每个游戏，Object数组为pair，由appid和r（对该游戏的喜欢程度，公式1 + time/60/5【max=9】）
    public String getSteamid() {
        return steamid;
    }
    public boolean containsItem(Object appid){
        boolean flag = false;
        for(Object[] item : items){
            if(item[0].equals(appid)){
                flag = true;
                break;
            }
        }
        
        return flag;
    }
    
    public List<Object[]> getItems() {
        return items;
    }

    public User2Item() {
        
    }
    
    public User2Item(String steamid) {
        this.steamid = steamid;
        this.items = new ArrayList();
        steamApiTool = new SteamApiTool();
    }

    public void setUserItem() {
        steamApiTool.setSteamApiTemplate("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" + key + "&format=xml&steamid=");
        String xmlString = steamApiTool.executeApi(steamid,true);
        dealXml(xmlString);
    }
    

    //通过xml字符串处理得游戏列表给自己
    private void dealXml(String xmlString) {

        boolean isPlayRecently = false;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            Element root = document.getRootElement();
            if (root.element("games") != null) {
                List<Element> messageElements = root.element("games").elements("message");
                for (Element message : messageElements) {
                    isPlayRecently = false;
                    if(message.element("playtime_2weeks") == null){
                        isPlayRecently = true;
                    }
                    Object[] tempItem = new Object[2];
                    tempItem[0] = message.elementText("appid");
                    tempItem[1] = 1 
                            + ((Integer.parseInt(message.elementText("playtime_forever")) / 300 > 9) ? 9 : Integer.parseInt(message.elementText("playtime_forever")) / 300) 
                            + ((isPlayRecently)?5:0);
                    items.add(tempItem);
                }
            }

        } catch (DocumentException ex) {
            Logger.getLogger(User2Item.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(User2Item.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
