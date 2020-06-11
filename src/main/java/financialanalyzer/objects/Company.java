
package financialanalyzer.objects;

import java.util.List;

/**
 *
 * @author pldor
 */
public class Company {
    private String id;
    private String name;
    private String stockSymbol;
    private String stockExchange;
    private List<String> sectors;
    private List<String> industries;
    private String enhancementVersion;
    private boolean downloadNews;
    private boolean downloadStocks;
    
    public String getId() {
        return this.stockExchange+"-"+this.stockSymbol;
    }

    public void setId(String id) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public List<String> getSectors() {
        return sectors;
    }

    public void setSectors(List<String> sectors) {
        this.sectors = sectors;
    }

    public List<String> getIndustries() {
        return industries;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public String getEnhancementVersion() {
        return enhancementVersion;
    }

    public void setEnhancementVersion(String enhancementVersion) {
        this.enhancementVersion = enhancementVersion;
    }

    public boolean isDownloadNews() {
        return downloadNews;
    }

    public void setDownloadNews(boolean downloadNews) {
        this.downloadNews = downloadNews;
    }

    public boolean isDownloadStocks() {
        return downloadStocks;
    }

    public void setDownloadStocks(boolean downloadStocks) {
        this.downloadStocks = downloadStocks;
    }
    
    
}
