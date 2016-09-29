/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steam;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author xiaohui
 */
public class UserSteamApiTool {
    String steamApiTemplate;
    
    public void setSteamApiTemplate(String steamApiTemplate){
        this.steamApiTemplate = steamApiTemplate;
    }
    
    public String executeApi(String steamid, boolean isShowApi) {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpGet httpget = new HttpGet(steamApiTemplate + steamid);
            if(isShowApi){
                System.out.println("executing request " + httpget.getURI());
            }
            CloseableHttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();
            //System.out.println(response.getStatusLine().getStatusCode());
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                //System.out.println("Response content length: " + entity.getContentLength());
                //System.out.println("Response content: " + EntityUtils.toString(entity));
                return EntityUtils.toString(entity);
            }
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "40X";
    }
    
}

