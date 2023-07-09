package Crawler;

import javax.swing.*;

public class Run {
    public static void main(String[] args) {
        // Collect all the categories and pages in the mother category "Thể_loại:Lịch_sử_Việt_Nam" and save to Categories and Pages
        CategoriesAndPages.CategoriesPagesCollector();
        // Get the list of Page and save to PagesName.json
        CategoriesLoader.PagesNameCollector();
        // Get the list
        DefaultListModel<String> list =  ListOfFile.keywordsList();
        // Make API requests to wikipedia to get revision data of pages and save to Categories_data
        WikipediaAPIRequest.APIRevisionsDataRequest(list);
    }
}
