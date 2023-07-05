import org.asynchttpclient.ListenableFuture;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.text.Normalizer;
import java.util.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class WikipediaAPIRequest {
    public static List<JSONObject> APISearchRequest(String query) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&list=search&prop=info&format=json&srsearch=" + StringProcessorTools.Space2Underscores(StringProcessorTools.convertVi2En(StringProcessorTools.removeDiacritics(query)));
        List<JSONObject> foundedValue = new ArrayList<>();

        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();

                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;
                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONArray rows = (JSONArray) results.get("search");
                for (Object row : rows) {
                    JSONObject result = (JSONObject) row;
                    long pageID = (long) result.get("pageid");
                    String title = (String) result.get("title");
                    String snippet = (String) result.get("snippet");
                    JSONObject matched =new JSONObject();
                    matched.put("pageid", pageID);
                    matched.put("title", title);
                    matched.put("snippet", snippet);
                    foundedValue.add(matched);
                    System.out.println(matched.toString());
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return foundedValue;
    }

    public static void APIImageRequest(String title, long pageid) {
        String apiUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=original&titles=" + StringProcessorTools.Space2Underscores(StringProcessorTools.convertVi2En(StringProcessorTools.removeDiacritics(title))) + "&origin=*";
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();

                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;
                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                JSONObject original = (JSONObject) pageId.get("original");
                String src = (String) original.get("source");
                long width = (long) original.get("width");
                long height = (long) original.get("height");
                System.out.println(src + " : " + width + "x" + height);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray APICategoriesDataRequest(String title) {
        String apiUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=categories&format=json&titles=" + StringProcessorTools.Space2Underscores(StringProcessorTools.convertVi2En(StringProcessorTools.removeDiacritics(title)));
        String categories = new String();
        JSONArray category = new JSONArray();
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                category = (JSONArray) pageId.get("categories");
                categories = (String) pageId.get("categories");
                System.out.println(categories);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    public static String APIRevisionsDataRequest(String title) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + StringProcessorTools.Space2Underscores(StringProcessorTools.removeDiacritics(title));
        String mainContent = new String();
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
//                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                JSONArray revisions = (JSONArray) pageId.get("revisions");
                for (Object row : revisions) {
                    JSONObject revision = (JSONObject) row;
                    mainContent = (String) revision.get("*");
                    System.out.println(mainContent);
                }
            }
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        return mainContent;
    }
    public static String APIRevisionsDataRequestV2(String title) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + StringProcessorTools.Space2Underscores(title);
        String mainContent = new String();
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
//                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                JSONArray revisions = (JSONArray) pageId.get("revisions");
                for (Object row : revisions) {
                    JSONObject revision = (JSONObject) row;
                    mainContent = (String) revision.get("*");
                    System.out.println(mainContent);
                }
            }
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        return mainContent;
    }
    public static String APIRevisionsDataRequestV1(String title) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + StringProcessorTools.Space2Underscores(StringProcessorTools.convertVi2En(StringProcessorTools.removeDiacritics(title)));
        String mainContent = new String();
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
//                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                JSONArray revisions = (JSONArray) pageId.get("revisions");
                for (Object row : revisions) {
                    JSONObject revision = (JSONObject) row;
                    mainContent = (String) revision.get("*");
                    System.out.println(mainContent);
                }
            }
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        return mainContent;
    }
    public static List<String> APIRevisionsDataRequestV3(String title) {
        String apiUrl = null;
        try{
            apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        List<String> mainContents = new ArrayList<>();
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
//                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                JSONArray revisions = (JSONArray) pageId.get("revisions");
                for (Object row : revisions) {
                    JSONObject revision = (JSONObject) row;
                }
            }
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        return mainContents;
    }
    public static String APIRevisionsDataRequestV4(String title) throws UnsupportedEncodingException {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + title;
        String mainContent = new String();
        File filePath = new File("Category_data//"+ URLDecoder.decode(title, "UTF-8") +".json");
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
//                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                JSONArray revisions = (JSONArray) pageId.get("revisions");
                JSONObject data = new JSONObject();
                for (Object row : revisions) {
                    JSONObject revision = (JSONObject) row;
                    mainContent = (String) revision.get("*");
                    System.out.println(mainContent);
                    data.put(pageid, mainContent);
                }
                 try(FileWriter file = new FileWriter(filePath)) {
                    file.write(data.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        return mainContent;
    }
    public static String APIRevisionsDataRequestFinal(String title) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + title;
        String mainContent = new String();
        try {
                String response = SeleniumTest.seleniumPackup(apiUrl);
                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONObject pages = (JSONObject) results.get("pages");
//                JSONObject pageId = (JSONObject) pages.get("" + pageid);
                String pageid = (String) pages.keySet().iterator().next();
                JSONObject pageId = (JSONObject) pages.get(pageid);
                JSONArray revisions = (JSONArray) pageId.get("revisions");
                for (Object row : revisions) {
                    JSONObject revision = (JSONObject) row;
                    mainContent = (String) revision.get("*");
                    System.out.println(mainContent);
                }
        } catch (ParseException e) {
            return null;
        }
        return mainContent;
    }

    public static String testDateAnalyze(String title, long pageid) {
        String apiUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + StringProcessorTools.Space2Underscores(StringProcessorTools.convertVi2En(StringProcessorTools.removeDiacritics(title)));
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();
                return response.toString();
            }
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static String APIBestMatchedSearchRequest(String query) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&list=search&prop=info&format=json&srsearch=" + StringProcessorTools.Space2Underscores(StringProcessorTools.convertVi2En(StringProcessorTools.removeDiacritics(query)));
        String keyword = new String();
        try {
            URL url = new URL(apiUrl);
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
                }
                reader.close();

                // Print the response
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) obj;
                JSONObject results = (JSONObject) jsonObject.get("query");
                JSONArray rows = (JSONArray) results.get("search");

                JSONObject bestMatched = (JSONObject) rows.get(0);
                keyword = bestMatched.get("title").toString();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return keyword;
    }
    public static void APIRevisionsDataRequestV5(DefaultListModel<String> titles) {
        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=";
        try {
            int size = titles.size()-28697;
            while (size!=0) {
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
                                if(title.contains("\"") || title.contains("/") || title.contains("\\") || title.contains(":") || title.contains("*") || title.contains("?") || title.contains("<") || title.contains(">") || title.contains("|")){
                                    pages.remove(pageid);
                                    continue;
                                }
                                //end of check name file
                                JSONArray revisions = (JSONArray) pageId.get("revisions");
                                File filePath = new File("Category_data//" + URLDecoder.decode(String.valueOf(title), "UTF-8") + ".json");
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

//        String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + title;
//        String mainContent = new String();
//        File filePath = new File("Category_data//"+ URLDecoder.decode(title, "UTF-8") +".json");
//        try {
//            URL url = new URL(apiUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                // Create a BufferedReader to read the response
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line;
//                StringBuilder response = new StringBuilder();
//
//                // Read the response line by line
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                reader.close();
//                // Print the response
//                JSONParser parser = new JSONParser();
//                Object obj = parser.parse(response.toString());
//                JSONObject jsonObject = (JSONObject) obj;
//
//                JSONObject results = (JSONObject) jsonObject.get("query");
//                JSONObject pages = (JSONObject) results.get("pages");
////                JSONObject pageId = (JSONObject) pages.get("" + pageid);
//                String pageid = (String) pages.keySet().iterator().next();
//                JSONObject pageId = (JSONObject) pages.get(pageid);
//                JSONArray revisions = (JSONArray) pageId.get("revisions");
//                JSONObject data = new JSONObject();
//                for (Object row : revisions) {
//                    JSONObject revision = (JSONObject) row;
//                    mainContent = (String) revision.get("*");
//                    System.out.println(mainContent);
//                    data.put(pageid, mainContent);
//                }
//                 try(FileWriter file = new FileWriter(filePath)) {
//                    file.write(data.toJSONString());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        } catch (IOException e) {
//            return null;
//        } catch (ParseException e) {
//            return null;
//        }
//        return mainContent;
    }
}
