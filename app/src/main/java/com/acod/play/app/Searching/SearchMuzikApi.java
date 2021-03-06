package com.acod.play.app.Searching;

import com.acod.play.app.Constants;
import com.acod.play.app.Models.SongResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by andrewcodispoti on 2015-05-31.
 */
public class SearchMuzikApi {

    public static ArrayList<SongResult> getSongs(String query) {
        ArrayList<SongResult> tempresults = new ArrayList<SongResult>();

        //Base searching url
        String tempquery = Constants.baseURL+"search?songname=" + URLEncoder.encode(query);

        try {
            String result = readUrl(new URL(tempquery));

            if (result == null){
                result=readUrl(new URL(Constants.backupURL+"search?songname=" + URLEncoder.encode(query)));
            }

            //Get the song elements.
            JSONArray elements = new JSONObject(result).getJSONArray("url");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject currElement = elements.getJSONObject(i);
                String key = currElement.keys().next();
                tempresults.add(new SongResult(key, currElement.get(key).toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempresults;
    }

    /**
     * Helper method to get the data.
     * @param url
     * @return
     * @throws Exception
     */
    public static String readUrl(URL url) throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
