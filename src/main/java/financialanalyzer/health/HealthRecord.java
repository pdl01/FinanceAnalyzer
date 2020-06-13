/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.health;

import java.util.Date;
import java.util.List;

/**
 *
 * @author phil
 */
public class HealthRecord {

    private Date recordDate;
    private String recordDateAsString;

    private List<String> companiesWithoutNewsItemsInPast3Days;
    private List<String> companiesWithoutNewsItemsInPast7Days;
    private List<String> companiesWithoutNewsItemsInPast30Days;

    private List<String> companiesWithoutStockHistoriesInPast7Days;
    private long totalNumberOfUserAnalyzedNewsItems;
    private long totalNumberOfUserUnanalyzedNewsItems;
    private long totalNumberOfSystemAnalyzedNewsItems;
    private long totalNumberOfSystemUnanalyzedNewsItems;

    public Date getRecordDate() {
        return recordDate;
    }

    public long getTotalNumberOfUserAnalyzedNewsItems() {
        return totalNumberOfUserAnalyzedNewsItems;
    }

    public void setTotalNumberOfUserAnalyzedNewsItems(long totalNumberOfUserAnalyzedNewsItems) {
        this.totalNumberOfUserAnalyzedNewsItems = totalNumberOfUserAnalyzedNewsItems;
    }

    public long getTotalNumberOfUserUnanalyzedNewsItems() {
        return totalNumberOfUserUnanalyzedNewsItems;
    }

    public void setTotalNumberOfUserUnanalyzedNewsItems(long totalNumberOfUserUnanalyzedNewsItems) {
        this.totalNumberOfUserUnanalyzedNewsItems = totalNumberOfUserUnanalyzedNewsItems;
    }

    public long getTotalNumberOfSystemAnalyzedNewsItems() {
        return totalNumberOfSystemAnalyzedNewsItems;
    }

    public void setTotalNumberOfSystemAnalyzedNewsItems(long totalNumberOfSystemAnalyzedNewsItems) {
        this.totalNumberOfSystemAnalyzedNewsItems = totalNumberOfSystemAnalyzedNewsItems;
    }

    public long getTotalNumberOfSystemUnanalyzedNewsItems() {
        return totalNumberOfSystemUnanalyzedNewsItems;
    }

    public void setTotalNumberOfSystemUnanalyzedNewsItems(long totalNumberOfSystemUnanalyzedNewsItems) {
        this.totalNumberOfSystemUnanalyzedNewsItems = totalNumberOfSystemUnanalyzedNewsItems;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordDateAsString() {
        return recordDateAsString;
    }

    public void setRecordDateAsString(String recordDateAsString) {
        this.recordDateAsString = recordDateAsString;
    }

    public List<String> getCompaniesWithoutNewsItemsInPast3Days() {
        return companiesWithoutNewsItemsInPast3Days;
    }

    public void setCompaniesWithoutNewsItemsInPast3Days(List<String> companiesWithoutNewsItemsInPast3Days) {
        this.companiesWithoutNewsItemsInPast3Days = companiesWithoutNewsItemsInPast3Days;
    }

    public List<String> getCompaniesWithoutNewsItemsInPast7Days() {
        return companiesWithoutNewsItemsInPast7Days;
    }

    public void setCompaniesWithoutNewsItemsInPast7Days(List<String> companiesWithoutNewsItemsInPast7Days) {
        this.companiesWithoutNewsItemsInPast7Days = companiesWithoutNewsItemsInPast7Days;
    }

    public List<String> getCompaniesWithoutNewsItemsInPast30Days() {
        return companiesWithoutNewsItemsInPast30Days;
    }

    public void setCompaniesWithoutNewsItemsInPast30Days(List<String> companiesWithoutNewsItemsInPast30Days) {
        this.companiesWithoutNewsItemsInPast30Days = companiesWithoutNewsItemsInPast30Days;
    }

    public List<String> getCompaniesWithoutStockHistoriesInPast7Days() {
        return companiesWithoutStockHistoriesInPast7Days;
    }

    public void setCompaniesWithoutStockHistoriesInPast7Days(List<String> companiesWithoutStockHistoriesInPast7Days) {
        this.companiesWithoutStockHistoriesInPast7Days = companiesWithoutStockHistoriesInPast7Days;
    }

}
