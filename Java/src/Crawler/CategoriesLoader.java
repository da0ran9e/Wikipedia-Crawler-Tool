package Crawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Stack;

public class CategoriesLoader {
    public static void PagesNameCollector(){
        int count = 0;
        JSONObject listFile = new JSONObject();
        JSONArray listPg = new JSONArray();

        File folder = new File("Pages");
        if(folder.exists()&&folder.isDirectory()){
            File[] files = folder.listFiles(); // Get an array of all files in the folder

            if (files != null) {
                // Iterate over the files and print their names
                for (File file : files) {
                    if (file.isFile()) {
                        try{
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));

                            String fileName = file.getName();
                            String categoryName = fileName.substring(9, fileName.length()-5);
                            JSONArray pagesList = (JSONArray) jsonObject.get(categoryName);

                            Stack<String> pageStack = new Stack<>();
                            Stack<String> linkStack = new Stack<>();
                            for(Object page:pagesList){
                                JSONObject name = (JSONObject) page;
                                String word = (String) name.get("page");
                                if (word!=null) pageStack.push(word);
                                String link = (String) name.get("link");
                                if (link!=null) linkStack.push(link);
                            }

                            while(!pageStack.empty()&&!linkStack.empty()) {
                                String pageTitle = pageStack.pop();
                                String pageLink = linkStack.pop();

                                JSONObject obj = new JSONObject();
                                if(pageTitle.contains(":")||pageTitle.contains("\\")||pageTitle.contains("/")||
                                        pageTitle.contains("truyền hình")||pageTitle.contains("băng nhạc")||
                                        pageTitle.contains("diễn viên")||pageTitle.contains("Giải")||
                                        pageTitle.contains("Đội tuyển")||pageTitle.contains("Truyền hình")||
                                        pageTitle.contains("thực phẩm")||pageTitle.contains("nhóm nhạc")||
                                        pageTitle.contains("2020")||pageTitle.contains("2021")||
                                        pageTitle.contains("2022")||pageTitle.contains("2023")||
                                        pageTitle.contains("Rap")||pageTitle.contains("Vụ án")||
                                        pageTitle.contains("ca sĩ")|| pageTitle.length()==0)
                                    continue;
                                obj.put("name", pageLink.substring(30));
                                if(listPg.contains(obj))continue;
                                listPg.add(obj);
                                count++;
                                System.out.println("Total "+count+" objects added!");
                            }
                        } catch (FileNotFoundException e) {
                            System.out.println(e);
                        } catch (IOException e) {
                            System.out.println(e);
                        } catch (ParseException e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        }
        listFile.put("pages", listPg);
        try (FileWriter fileWriter = new FileWriter("PagesName.json")) {
                                    fileWriter.write(listFile.toJSONString());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
    }
}
