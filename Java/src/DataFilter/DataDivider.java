package DataFilter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DataDivider {
    public static void sortingByParameters() {
        File file = new File("/data/Raw/Data//Objects.json");
        JSONParser parser = new JSONParser();

        try {
            JSONObject objects = (JSONObject) parser.parse(new FileReader(file));

            for (Object object : objects.keySet()) {
                String objectName = object.toString();

                File newFolder = new File("/data/Raw/Data/SortByPara//" + objectName);
                if (!newFolder.exists()) {
                    boolean created = newFolder.mkdir();
                }
                JSONObject newFileObj = new JSONObject();

                JSONObject obj = (JSONObject) objects.get(object);
                List<String> keyList = new ArrayList<>();

                for (Object key : obj.keySet()) {
                    String keyString = key.toString();
                    keyList.add(keyString);
                }

                File dataFile = new File("/data/Raw/Prefiltered_templates_data.json");
                JSONObject dataObjs = (JSONObject) parser.parse(new FileReader(dataFile));
                for(Object dataObj:dataObjs.keySet()){
                    String dataName = dataObj.toString();
                    JSONObject dataObjName = (JSONObject) dataObjs.get(dataName);
                    label:for(Object data: dataObjName.keySet()){
                        String dataId = data.toString();
                        JSONObject parameters = (JSONObject) dataObjName.get(dataId);
                        for(Object parameter:parameters.keySet()){
                            String parameterString = parameter.toString();
                            if(keyList.contains(parameterString)) {
                                JSONObject newObj = new JSONObject();
                                newObj.put(dataName, dataObjName);
                                newFileObj.put(dataName, dataObjName);
                                System.out.println(parameterString);
                                break label;
                            }
                        }
                    }
                }
                File newFile = new File("/data/Raw/Data/SortByPara/" + objectName+"//data.json");
                System.out.println(newFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(newFile)) {
                    fileWriter.write(newFileObj.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
