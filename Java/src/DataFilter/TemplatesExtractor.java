package DataFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class TemplatesExtractor {
    public static List<String> extractTemplate(String input) {
        StringBuilder tb = new StringBuilder();
        List<String> substrings = new ArrayList<>();
        Stack<Integer> startIndexes = new Stack<>();

        tb.append(' ');
        tb.append(input);
        tb.append(' ');
        input = tb.toString();

        for (int i=1; i<input.length()-1; i++) {
            if (input.charAt(i-1) != '{' && input.charAt(i) == '{' && input.charAt(i+1) == '{' && input.charAt(i+2) != '}') {
                startIndexes.push(i);
            }
            if (input.charAt(i) == '}' && input.charAt(i+1) == '}') {
                try{
                    int startIndex = startIndexes.pop();
                    substrings.add(input.substring(startIndex, i+2));
                } catch (Exception e) {
                    continue;
                }
            }
        }


        return substrings;
    }
    public static void templateFilter(){
        try{
            File filteredJson = new File("jsonformatter.json");
            List<String> filteredList = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONObject ojb = (JSONObject) parser.parse(new FileReader(filteredJson));
            JSONArray filteredArr = (JSONArray) ojb.get("used");
            for(Object templ:filteredArr){
                JSONObject o = new JSONObject((Map) parser.parse((String) templ.toString()));
                String tampName = o.keySet().toString();
                System.out.println(tampName.substring(1, tampName.length()-1));
                filteredList.add(tampName.substring(1, tampName.length()-1));
            }
            File folder = new File("/data/Raw/Category_data");
            if(folder.exists()&&folder.isDirectory()) {
                File[] listOfFiles = folder.listFiles();
                for (File file : listOfFiles) {
                    JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                    String pageId = (String) jsonObject.keySet().iterator().next();
                    String pageContent = jsonObject.get(pageId).toString();

                    List<String> templates = extractTemplate(pageContent);
                    JSONObject resultJson = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    for (String template : templates) {
                        for(String check:filteredList){
                            if(template.length()>check.length()+4){
                                String tempName = template.substring(2,check.length()+2);
                                if(tempName.equals(check)){
                                    System.out.println(tempName + " -eq-> " + check);
                                    jsonArray.add(template);
                                }

                            }
                        }

                    }
                    resultJson.put(pageId, jsonArray);

                    File resultFile = new File("/data/Raw/Templates_data//" + file.getName());
                    System.out.println(resultFile.getAbsolutePath());
                    try(FileWriter fileWriter = new FileWriter(resultFile)) {
                        fileWriter.write(resultJson.toJSONString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
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
