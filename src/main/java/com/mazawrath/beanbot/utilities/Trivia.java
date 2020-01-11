package com.mazawrath.beanbot.utilities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Trivia {
    public JSONObject getTrivia() {
        try {
            URL url = new URL("https://opentdb.com/api.php?amount=1&type=multiple&encode=base64");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if (con.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(content.toString());
            } else {
                return null;
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
