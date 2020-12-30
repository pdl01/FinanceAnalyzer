/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.health;

/**
 *
 * @author pldor
 */
public class StockHistoryDateCount {
    private String recordDateAsString;
    private long companiesWithStockHistory;

    public String getRecordDateAsString() {
        return recordDateAsString;
    }

    public void setRecordDateAsString(String recordDateAsString) {
        this.recordDateAsString = recordDateAsString;
    }

    public long getCompaniesWithStockHistory() {
        return companiesWithStockHistory;
    }

    public void setCompaniesWithStockHistory(long companiesWithStockHistory) {
        this.companiesWithStockHistory = companiesWithStockHistory;
    }
    
}
