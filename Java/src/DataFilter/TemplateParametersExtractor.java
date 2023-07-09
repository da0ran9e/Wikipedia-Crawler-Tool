package DataFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class TemplateParametersExtractor {
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
    public static void TemplateFilter() {
        try{
            File folder = new File("/data/Raw/Templates_data");
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
            File resultFile = new File("data/Raw/Prefiltered_templates_data.json");
            //System.out.println(resultFile.getAbsolutePath());
            try(FileWriter fileWriter = new FileWriter(resultFile)) {
                fileWriter.write(resultJson.toJSONString());
            } catch (IOException e) {
                throw new RuntimeException(e);
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
