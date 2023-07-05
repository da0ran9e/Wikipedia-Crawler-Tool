import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class WikiList {
        public static void PersonList(){
                try {
                    String link = "https://vi.wikipedia.org/w/index.php?search=m%E1%BA%A5t+sinh+ng%C6%B0%E1%BB%9Di+OR+qu%C3%AA+OR+m%E1%BA%A5t+OR+sinh+OR+t%C3%AAn&title=%C4%90%E1%BA%B7c+bi%E1%BB%87t%3AT%C3%ACm+ki%E1%BA%BFm&profile=advanced&fulltext=1&advancedSearch-current=%7B%22fields%22%3A%7B%22plain%22%3A%5B%22m%E1%BA%A5t%22%2C%22sinh%22%5D%2C%22or%22%3A%5B%22ng%C6%B0%E1%BB%9Di%22%2C%22qu%C3%AA%22%2C%22m%E1%BA%A5t%22%2C%22sinh%22%2C%22t%C3%AAn%22%5D%7D%7D&ns0=1&ns4=1&ns8=1&ns14=1&ns100=1&ns710=1&ns828=1&ns2300=1";
                    Document document = Jsoup.connect(link).get();
                    //Document document = Jsoup.parse(SeleniumTest.seleniumEdge());
                    Elements resultsInfo = document.select(".results-info");
                    System.out.println(resultsInfo.attr("data-mw-num-results-total"));
                    Elements elements = document.select(".mw-search-results li");
                    System.out.println(elements.size());

                    for (Element element : elements) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    public static void main(String[] args) {
            PersonList();
    }
}
