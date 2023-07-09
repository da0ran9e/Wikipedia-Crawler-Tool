package DataFilter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class KeysFilter {
    public static void UniqueKeys(String path){
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
                System.out.println(e);
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
