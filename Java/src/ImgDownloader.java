import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ImgDownloader {
    public static void ImgDownloader() {
        File file = new File("Prefiltered_templates_data2f.json");
        JSONParser parser = new JSONParser();
        DefaultListModel<String> titles = new DefaultListModel<>();

        try {
            String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages&titles=";
            JSONObject obj = (JSONObject) parser.parse(new FileReader(file));
            for (Object key : obj.keySet()) {
                String title = (String) key;
                String encodedTitle = URLEncoder.encode(title, "UTF-8");

                titles.addElement(encodedTitle);
            }
            APIGetImg(titles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void APIGetImg(DefaultListModel<String> titles) {
        try {
            int size = titles.size();
            while (size != 0) {
                String apiUrl = "https://vi.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages&titles=";
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
                            for(Object page:pages.keySet()) {
                                String pageid = page.toString();
                                JSONObject pageId = (JSONObject) pages.get(pageid);
                                if(pageId.containsKey("thumbnail")){
                                    JSONObject thumbnail = (JSONObject) pageId.get("thumbnail");
                                    String source = (String) thumbnail.get("source");
                                    System.out.println(source);
                                    URL src = new URL(source);
                                    try(InputStream in = src.openStream()){
                                        Path destination = Path.of("ImgData/"+pageid+".jpg");
                                        Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                }

                            }

                        }
                        reader.close();
                    }
                    size -= 50;
                } catch (IOException e) {

                } catch (ParseException e) {

                } catch (RuntimeException e) {
                    size--;
                    continue;
                }
            }
        } catch (RuntimeException e) {
            
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        ImgDownloader();
    }
}
