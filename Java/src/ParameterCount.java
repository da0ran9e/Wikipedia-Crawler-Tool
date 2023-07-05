import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class ParameterCount {
    static class ValueComparator implements Comparator<String> {
        private final Map<String, Object> base;

        ValueComparator(Map<String, Object> base) {
            this.base = base;
        }

        @Override
        public int compare(String key1, String key2) {
            Integer value1 = (Integer) base.get(key1);
            Integer value2 = (Integer) base.get(key2);

            return value1.compareTo(value2);
        }
    }
    public static void FirstCheck(){
        File file = new File("CustomParameters//Objects.json");
        JSONParser parser = new JSONParser();

        try{
            JSONObject objects = (JSONObject) parser.parse(new FileReader(file));
            JSONObject artifact = (JSONObject) objects.get("artifact");
	        JSONObject human = (JSONObject) objects.get("human");
            JSONObject customer = (JSONObject) objects.get("customer");
            JSONObject location = (JSONObject) objects.get("location");
            JSONObject figure = (JSONObject) objects.get("figure");
            JSONObject policy = (JSONObject) objects.get("policy");
            JSONObject temple = (JSONObject) objects.get("temple");
            JSONObject war = (JSONObject) objects.get("war");
            JSONObject group = (JSONObject) objects.get("group");
            JSONObject art = (JSONObject) objects.get("art");
            JSONObject park = (JSONObject) objects.get("park");
            JSONObject politicalEntity = (JSONObject) objects.get("political entity");
            JSONObject touristLocation = (JSONObject) objects.get("tourist location");
            JSONObject holiday = (JSONObject) objects.get("holiday");
            JSONObject historicalLocation = (JSONObject) objects.get("historical location");
            JSONObject dynasty = (JSONObject) objects.get("dynasty");
            JSONObject building = (JSONObject) objects.get("building");

            List<String> keyList = new ArrayList<>();
            for(Object object:objects.keySet()){
                JSONObject obj = (JSONObject) objects.get(object);
                for(Object key:obj.keySet()){
                    String keyString = key.toString();
                    if(!keyList.contains(keyString)){
                        keyList.add(keyString);
                    }else {
                        System.out.println("\""+keyString+"\": \"\",");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("EOF");
        } catch (ParseException e) {
            System.out.println("ParseException");
        }

    }

    public static void sortingByParameters() {
        File file = new File("CustomParameters//Objects.json");
        JSONParser parser = new JSONParser();

        try {
            JSONObject objects = (JSONObject) parser.parse(new FileReader(file));

            for (Object object : objects.keySet()) {
                String objectName = object.toString();

                File newFolder = new File("CustomParameters/SortByPara//" + objectName);
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

                File dataFile = new File("Prefiltered_templates_data2f.json");
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
                File newFile = new File("CustomParameters/SortByPara/" + objectName+"//"+objectName+".json");
                System.out.println(newFile.getAbsolutePath());
                try(FileWriter fileWriter = new FileWriter(newFile)) {
                    fileWriter.write(newFileObj.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject sortJSONObjectByValues(JSONObject jsonObject) {
        // Convert JSONObject to a List of Map entries
        List<Map.Entry<String, Object>> entryList = new ArrayList<>(jsonObject.entrySet());

        // Sort the List based on long values
        entryList.sort(Comparator.comparingLong(entry -> (int) entry.getValue()));

        // Create a new JSONObject using the sorted Map entries
        JSONObject sortedJsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : entryList) {
            sortedJsonObject.put(entry.getKey(), entry.getValue());
        }

        return sortedJsonObject;
    }
    public static void totalParameters(String path){
        try{
            File folder = new File(path);
            JSONObject newFile = new JSONObject();
            if(folder.exists()&&folder.isDirectory()) {
                File[] listOfFiles = folder.listFiles();

                for (File file : listOfFiles) {
                    JSONParser parser = new JSONParser();
                    JSONObject fileObj = (JSONObject) parser.parse(new FileReader(file));
                    for(Object nameObj: fileObj.keySet()){
                        JSONObject objName =  (JSONObject) fileObj.get(nameObj);
                        String objId = (String) objName.keySet().iterator().next();
                        JSONObject objParameters = (JSONObject) objName.get(objId);
                        for(Object parameter:objParameters.keySet()) {
                            String paraString = parameter.toString();
                            if(!newFile.keySet().contains(paraString)){
                                newFile.put(paraString, 1);
                            }
                            else {
                                int value = (int) newFile.get(paraString);
                                newFile.replace(paraString, value+ 1);
                            }
                        }
                    }
                }
            }
            JSONObject sortedNewFile = sortJSONObjectByValues(newFile);

            File newF = new File(path+"//totalParameter.json");
            System.out.println(newF.getAbsolutePath());
            try(FileWriter fileWriter = new FileWriter(newF)) {
                fileWriter.write(sortedNewFile.toJSONString());
            } catch (IOException e) {
                System.out.println("error");
            }
        } catch (FileNotFoundException e) {
            System.out.println("error");
        } catch (IOException e) {
            System.out.println("error");
        } catch (ParseException e) {
            System.out.println("error");
        }

    }

    public static void LinearFinalData(String path){
        try{
            File folder = new File(path);
            JSONObject newFile = new JSONObject();
            if(folder.exists()&&folder.isDirectory()) {
                File dictionaryFile = new File(path + "//combine.json");
                JSONParser parser = new JSONParser();
                JSONObject dictionary = (JSONObject) parser.parse(new FileReader(dictionaryFile));

                File firstDataFile = new File(path + "//data.json");
                JSONObject firstData = (JSONObject) parser.parse(new FileReader(firstDataFile));

                for (Object nameObj : firstData.keySet()) {
                    JSONObject newNameObj = new JSONObject();
                    JSONObject newIdObj = new JSONObject();
                    String name = nameObj.toString();
                    JSONObject objName = (JSONObject) firstData.get(name);
                    String objId = (String) objName.keySet().iterator().next();
                    JSONObject objParameters = (JSONObject) objName.get(objId);

                    for (Object dicObj : dictionary.keySet()) {
                        String newParameter = dicObj.toString();
                        JSONObject newParameterObj = (JSONObject) dictionary.get(newParameter);
                        StringBuilder tb = new StringBuilder();
                        for (Object parameter : newParameterObj.keySet()) {
                            Object values = objParameters.get(parameter);
                            if (values != null&&values!=" ") {
                                tb.append("*");
                                tb.append(values);
                                tb.append(" ");
                                System.out.println(values);
                            }
                        }
                        String newParameterString = tb.toString();
                        newIdObj.put(newParameter, newParameterString);
                        newNameObj.put(objId, newIdObj);
                    }
                    newFile.put(name, newNameObj);
                }
            }


            File newF = new File(path+"//FinalData.json");
            System.out.println(newF.getAbsolutePath());
            try(FileWriter fileWriter = new FileWriter(newF)) {
                fileWriter.write(newFile.toJSONString());
            } catch (IOException e) {
                System.out.println("error");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
//        FirstCheck();
//        sortingByParameters();
        //totalParameters("CustomParameters/SortByPara/human");
//        totalParameters("CustomParameters/SortByPara/artifact");
//        totalParameters("CustomParameters/SortByPara/customer");
//        totalParameters("CustomParameters/SortByPara/location");
//        totalParameters("CustomParameters/SortByPara/figure");
//        totalParameters("CustomParameters/SortByPara/policy");
//        totalParameters("CustomParameters/SortByPara/temple");
//        totalParameters("CustomParameters/SortByPara/war");
//        totalParameters("CustomParameters/SortByPara/group");
//        totalParameters("CustomParameters/SortByPara/art");
//        totalParameters("CustomParameters/SortByPara/park");
//        totalParameters("CustomParameters/SortByPara/politicalEntity");
//        totalParameters("CustomParameters/SortByPara/touristLocation");
//        totalParameters("CustomParameters/SortByPara/holiday");
//        totalParameters("CustomParameters/SortByPara/historicalLocation");
//        totalParameters("CustomParameters/SortByPara/dynasty");
//        totalParameters("CustomParameters/SortByPara/building");
        LinearFinalData("CustomParameters/SortByPara/human");
    }
}
