package Crawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ListOfFile {
        public static DefaultListModel<String> keywordsList(){
        DefaultListModel<String> list = new DefaultListModel<>();
        try{
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("PagesName.json"));
            JSONArray keyList = (JSONArray) jsonObject.get("pages");
            for(Object keyword:keyList){
                JSONObject key = (JSONObject) keyword;
                String word = (String) key.get("name");
                list.addElement(word);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            System.out.println(e);
        }
            return list;
    }
}
