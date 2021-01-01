/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.report;

import financialanalyzer.stockhistory.StockHistory;
import java.util.List;

/**
 *
 * @author pldor
 */
public interface ReportGenerator {
    public String getId();
    public ReportSummary getReport(String _startDate,String _endDate);
    public List<StockHistory> getReport(String _date,int _start,int _numResults);
    public String getReportAudioScript(String _date,int numOfItemsToInclude);
    public String getReportTags(String _date,int numOfItemsToInclude);
}
