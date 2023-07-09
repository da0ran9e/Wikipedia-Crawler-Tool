package Crawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class WikipediaAPIRequest {
    public static void APIRevisionsDataRequest(DefaultListModel<String> titles) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=";
        try {
            int size = titles.size();
            while (size != 0) {
                try {
                    for (int i = 1; i <= 50; i++) {
                        String title = titles.get(size - i);
                        apiUrl += title + "|";
                    }
                    apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
                    URL url = new URL(apiUrl);
                    //System.out.println(url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Create a BufferedReader to read the response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();

                        // Read the response line by line
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                            System.out.println(line);

                            JSONParser parser = new JSONParser();
                            Object obj = parser.parse(response.toString());
                            JSONObject jsonObject = (JSONObject) obj;

                            JSONObject results = (JSONObject) jsonObject.get("query");
                            JSONObject pages = (JSONObject) results.get("pages");
                            while (pages.keySet().iterator().hasNext()) {
                                String pageid = (String) pages.keySet().iterator().next();
                                JSONObject pageId = (JSONObject) pages.get(pageid);
                                String title = (String) pageId.get("title");
                                //title must be valid for name the file
                                if (title.contains("\"") || title.contains("/") || title.contains("\\") || title.contains(":") || title.contains("*") || title.contains("?") || title.contains("<") || title.contains(">") || title.contains("|")) {
                                    pages.remove(pageid);
                                    continue;
                                }
                                //end of check name file
                                JSONArray revisions = (JSONArray) pageId.get("revisions");
                                File filePath = new File("/data/Raw/Category_data//" + URLDecoder.decode(String.valueOf(title), "UTF-8") + ".json");
                                JSONObject data = new JSONObject();
                                String mainContent = new String();
                                for (Object row : revisions) {
                                    JSONObject revision = (JSONObject) row;
                                    mainContent = (String) revision.get("*");
                                }
                                System.out.println(filePath);
                                data.put(pageid, mainContent);
                                try (FileWriter file = new FileWriter(filePath)) {
                                    file.write(data.toJSONString());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                pages.remove(pageid);
                            }

                        }
                        reader.close();
                    }
                    size -= 50;
                    apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=";
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (RuntimeException e) {
                    size--;
                    continue;
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
