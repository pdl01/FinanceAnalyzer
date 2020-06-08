package financialanalyzer.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author phil
 */
public abstract class AbstractSearchProperties {

    protected int numResults = 100;
    protected int startResults = 0;

    protected String id;
    protected String companyName;
    protected String stockExchange;
    protected String stockSymbol;
    protected String companyId;
    protected List<String> searchDates;
    protected List<String> industries;
    protected List<String> sectors;
    
    protected String sortField;
    protected String sortOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortField() {
        return sortField;
    }

    public List<String> getSearchDates() {
        return searchDates;
    }

    public void setSearchDates(List<String> searchDates) {
        this.searchDates = searchDates;
    }
    public void setSearchDate(String searchDate) {
        this.searchDates = new ArrayList<>();
        this.searchDates.add(searchDate);
    }
    
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public int getStartResults() {
        return startResults;
    }

    public void setStartResults(int startResults) {
        this.startResults = startResults;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<String> getIndustries() {
        return industries;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public List<String> getSectors() {
        return sectors;
    }

    public void setSectors(List<String> sectors) {
        this.sectors = sectors;
    }

}
