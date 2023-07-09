package DataFilter;

public class Run {
    public static void main(String[] args) {
        // Extract information of templates in each page and save to Template_data using custom file "jsonformatter.json" to make sure we only get the useful templates
        TemplatesExtractor.templateFilter();
        // Get all data pairs (key-value) in template and save to a unified file "Prefiltered_templates_data.json"
        TemplateParametersExtractor.TemplateFilter();
        // Divide the data to objects and save to Data folder using custom file "objects.json" to determine which class each entity belong to
        DataDivider.sortingByParameters();
        //Final step is rename key to a unique name
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/human");
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/holiday");
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/dynasty");
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/location");
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/weapon");
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/war");
        KeysFilter.UniqueKeys("/data/Raw/Data/SortByPara/tourist location");
    }
}
