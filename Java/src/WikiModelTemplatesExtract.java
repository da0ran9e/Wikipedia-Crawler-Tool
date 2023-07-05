import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.swing.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public class WikiModelTemplatesExtract {
    public static List<String> extractSubstrings(String input) {
        List<String> substrings = new ArrayList<>();

        int startIndex = input.indexOf("{{");
        if (startIndex != -1) {
            int extractBeginIndex = startIndex;
            int extractEndIndex = startIndex;
            do{
                extractBeginIndex = input.indexOf("{{",extractBeginIndex+2);
                extractEndIndex = input.indexOf("}}", extractEndIndex+2);
                if(extractBeginIndex>extractEndIndex){
                    substrings.add(input.substring(startIndex,extractEndIndex+2));
                    startIndex = extractBeginIndex;
                } else if (extractBeginIndex==-1) {
                    substrings.add(input.substring(startIndex,extractEndIndex+2));
                    break;
                }
            }while(extractBeginIndex!=-1||extractEndIndex!=-1);
        }
        return substrings;
    }

    public static List<String> extractSubstringsV1(String input) {
        List<String> substrings = new ArrayList<>();
        List<Integer> beginIndexes = new ArrayList<>(1000);
        List<Integer> endIndexes = new ArrayList<>(1000);

        int startIndex = -2;
        int endIndex = -2;
        while (startIndex!=-1) {
            startIndex = input.indexOf("{{", startIndex+2);
            endIndex = input.indexOf("}}", endIndex+2);
            if (startIndex != -1) {
                beginIndexes.add(startIndex);
                //System.out.println(startIndex);
            }
            if (endIndex != -1) {
                endIndexes.add(endIndex);
                //System.out.println(endIndex);
            }
        }
        System.out.println(beginIndexes.size() + " " + endIndexes.size());
        return substrings;
    }

    public static List<String> extractSubstringsV2(String input) {
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

    public static Map<String, String> parseInputString(String input) {
        Map<String, String> resultMap = new HashMap<>(100);
        String[] lines = input.split("\\n\\|");
        for (String line : lines) {
            //System.out.println(line);
            if (line.contains("=")) {
                //line = line.substring(1).trim();
                String key = StringProcessorTools.removeUnnecessarySpaces(line.substring(0, line.indexOf("=")));
                String value = StringProcessorTools.removeUnnecessarySpaces(line.substring(line.indexOf("=")+1));

                resultMap.put(key, value);
                //System.out.println(key +" -> "+ value );
            }
        }

        return resultMap;
    }
    public static void filtering() throws IOException, ParseException {
        File folder = new File("Category_data");
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                String pageId = (String) jsonObject.keySet().iterator().next();
                String pageContent = jsonObject.get(pageId).toString();

                List<String> templates = extractSubstringsV2(pageContent);
                JSONObject resultJson = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (String template : templates) {
                    jsonArray.add(template);
                }
                resultJson.put(pageId, jsonArray);

                File resultFile = new File("Templates_data2//" + file.getName());
                System.out.println(resultFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(resultFile)) {
                    fileWriter.write(resultJson.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
public static void filteringV1() throws IOException, ParseException{
        File folder = new File("Category_data");
        List<String> files = new ArrayList<>();
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                String name = file.getName();
                files.add(name);
                //System.out.println(name);
            }
        }
        DefaultListModel<String> list = SeleniumTest.keywordsListV3();
        for(int i=0; i< list.getSize(); i++){
            String name = StringProcessorTools.Underscore2Space(URLDecoder.decode(list.getElementAt(i), "UTF-8")+".json");
            //System.out.println(name);
            if(files.contains(name)){
                files.remove(name);
            }
        }
        System.out.println(files.size());
        for(String file : files) {
            System.out.println(file);
        }
    }
    public static void filteringV2() throws IOException, ParseException {
        File folder = new File("Templates_data");
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                String pageId = (String) jsonObject.keySet().iterator().next();
                JSONArray templates = (JSONArray) jsonObject.get(pageId);

                JSONObject resultJson = new JSONObject();
                JSONObject templateJson = new JSONObject();
                for (Object template : templates) {
                    String temp = template.toString();
                    if(temp.contains("|")){
                        JSONArray jsonArray = new JSONArray();
                        Map<String, String> map = parseInputString(temp);
                        String tempName = temp.substring(temp.indexOf("{{")+2, temp.indexOf("|"));
                        System.out.println(tempName);
                        JSONObject json = new JSONObject();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            json.put(entry.getKey(), entry.getValue());
                        }
                        jsonArray.add(json);
                        templateJson.put(tempName, jsonArray);
                    }
                }
                resultJson.put(pageId, templateJson);


                File resultFile = new File("Prefiltered_templates_data//" + file.getName());
                //System.out.println(resultFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(resultFile)) {
                    fileWriter.write(resultJson.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
    public static void filteringV3() throws IOException, ParseException {
        File folder = new File("Templates_data");
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            List<String> templatesList = new ArrayList<>();
            Map<String, Integer> templatesUsing = new HashMap<>();
            for (File file : listOfFiles) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                String pageId = (String) jsonObject.keySet().iterator().next();
                JSONArray templates = (JSONArray) jsonObject.get(pageId);

//                JSONObject resultJson = new JSONObject();
//                JSONObject templateJson = new JSONObject();
                for (Object template : templates) {
                    String temp = template.toString();
                    if(temp.indexOf("{{")!=-1&&temp.indexOf("|")!=-1){
                        String tempName = temp.substring(2, temp.indexOf("|")).replaceAll("\\n$", "").trim();;
                        if(!templatesList.contains(tempName)){
                            templatesList.add(tempName);
                        }
                        if(!templatesUsing.containsKey(tempName)){
                        templatesUsing.put(tempName, 1);
                        }
                        else{
                            templatesUsing.replace(tempName, templatesUsing.get(tempName)+1);
                        }
                    }
                }
            }
                Collections.sort(templatesList);
                System.out.println(templatesUsing);
                JSONObject mapTemplate = new JSONObject();
                JSONArray templatesArray = new JSONArray();

                // Convert HashMap to List of Map entries
                List<Map.Entry<String, Integer>> list = new ArrayList<>(templatesUsing.entrySet());

        // Sort the List by values using a custom Comparator
                Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                // Sort in ascending order (change o1 and o2 to reverse the order)
                    return o1.getValue().compareTo(o2.getValue());
                    }
                });

        // Create a LinkedHashMap to preserve the sorted order
                LinkedHashMap<String, Integer> sortedHashMap = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : list) {
                    sortedHashMap.put(entry.getKey(), entry.getValue());
                }

        // Print the sorted HashMap
                for (Map.Entry<String, Integer> entry : sortedHashMap.entrySet()) {
                    JSONObject json = new JSONObject();
                    json.put(entry.getKey(), entry.getValue());
                    templatesArray.add(json);
                }
                mapTemplate.put("used", templatesArray);
                File resultFile = new File("sorted_templates_used.json");
                //System.out.println(resultFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(resultFile)) {
                    fileWriter.write(mapTemplate.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }

    }
    public static void filteringV4() throws IOException, ParseException {

         File filteredJson = new File("jsonformatter.txt");
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

File folder = new File("Category_data");
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                String pageId = (String) jsonObject.keySet().iterator().next();
                String pageContent = jsonObject.get(pageId).toString();

                List<String> templates = extractSubstringsV2(pageContent);
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

                File resultFile = new File("Templates_data2f//" + file.getName());
                System.out.println(resultFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(resultFile)) {
                    fileWriter.write(resultJson.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }
    public static void filteringV5() throws IOException, ParseException {
        File folder = new File("Templates_data2f");
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                String pageId = (String) jsonObject.keySet().iterator().next();
                JSONArray templates = (JSONArray) jsonObject.get(pageId);

                JSONObject resultJson = new JSONObject();
                JSONObject templateJson = new JSONObject();
                for (Object template : templates) {
                    String temp = template.toString();
                    if(temp.contains("|")){
                        JSONArray jsonArray = new JSONArray();
                        Map<String, String> map = parseInputString(temp);
                        String tempName = temp.substring(temp.indexOf("{{")+2, temp.indexOf("|"));
                        System.out.println(tempName);
                        JSONObject json = new JSONObject();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            json.put(entry.getKey(), entry.getValue());
                        }
                        jsonArray.add(json);
                        templateJson.put(tempName, jsonArray);
                    }
                }
                resultJson.put(pageId, templateJson);


                File resultFile = new File("Prefiltered_templates_data2//" + file.getName());
                //System.out.println(resultFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(resultFile)) {
                    fileWriter.write(resultJson.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
    public static void filteringV6() throws IOException, ParseException {
        File folder = new File("Templates_data2f");
        JSONObject resultJson = new JSONObject();
        if(folder.exists()&&folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                JSONObject templateJson = new JSONObject();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
                String pageId = (String) jsonObject.keySet().iterator().next();
                JSONArray templates = (JSONArray) jsonObject.get(pageId);

                for (Object template : templates) {
                    String temp = template.toString();
                    if(temp.contains("|")){
                        Map<String, String> map = parseInputString(temp);
                        JSONObject json = new JSONObject();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            json.put(entry.getKey(), entry.getValue());
                            templateJson.put(pageId, json);
                        }
                    }
                }
                resultJson.put(file.getName().substring(0, file.getName().length()-5), templateJson);
                System.out.println(templateJson);
            }
        }
                File resultFile = new File("Prefiltered_templates_data2f.json");
                //System.out.println(resultFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(resultFile)) {
                    fileWriter.write(resultJson.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

    }

    public static void main(String[] args) {
        List<Map<String, String>> mappedTemplates = new ArrayList<>();
        try{
            filteringV6();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
