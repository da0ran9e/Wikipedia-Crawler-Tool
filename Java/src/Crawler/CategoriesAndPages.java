package Crawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class CategoriesAndPages {
    public static void CategoriesPagesCollector(){
        String url="https://vi.wikipedia.org/wiki/Thể_loại:Lịch_sử_Việt_Nam";

        System.setProperty("webdriver.edge.driver", "msedgedriver.exe");
        WebDriver driver = new EdgeDriver();

        Stack<String> links = new Stack<>();
        links.push(url);
        do{
            driver.get(links.pop());

            JSONObject categoryKeywords = new JSONObject();
            JSONObject contentKeywords = new JSONObject();
            try {
                String pageSrc = driver.getPageSource();

                Document doc = Jsoup.parse(pageSrc);
                String name = doc.select(".mw-page-title-main").text();
                Elements categoryElements = doc.select(".CategoryTreeItem");
                Elements contentList = doc.select(".mw-content-ltr");
                Elements contentElements = contentList.select("li");

                File categoriesFilePath = new File("/data/Raw/Categories//"+"categories_of_"+name+".json");
                File pagesFilePath = new File("/data/Raw/Pages//"+"pages_of_"+name+".json");

                if(categoriesFilePath.exists()) continue;

                JSONArray categories = new JSONArray();
                for(Element category:categoryElements){
                    JSONObject categoryName = new JSONObject();
                    JSONObject categoryLink = new JSONObject();

                    Elements tag = category.select("a");
                    String link = "https://vi.wikipedia.org"+tag.attr("href");
                    links.push(link);

                    categoryName.put("category", tag.text());
                    categoryLink.put("link", link);
                    categories.add(categoryName);
                    categories.add(categoryLink);
                }
                System.out.println(links);
                categoryKeywords.put(name, categories);
                try(FileWriter file = new FileWriter(categoriesFilePath)){
                    file.write(categoryKeywords.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                    JSONArray contents = new JSONArray();
                for(Element contentElement:contentElements){
                    JSONObject contentName = new JSONObject();
                    JSONObject contentLink = new JSONObject();

                    Elements tag = contentElement.select("a");

                    contentName.put("page", tag.attr("title"));
                    contentLink.put("link", "https://vi.wikipedia.org"+tag.attr("href"));
                    contents.add(contentName);
                    contents.add(contentLink);
                }
                contentKeywords.put(name, contents);
                try(FileWriter file = new FileWriter(pagesFilePath)){
                    file.write(contentKeywords.toJSONString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        } while(!links.empty());
    }

    public static String seleniumPackup(String url){
        String pageSrc = new String();
        System.setProperty("webdriver.edge.driver", "msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.get(url);
        try{
            String pgSrc = driver.getPageSource();
            Document doc = Jsoup.parse(pgSrc);
            Elements elements = doc.select("pre");
            pageSrc = elements.text();
            System.out.println(pageSrc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        driver.close();
        return pageSrc;
    }
}

