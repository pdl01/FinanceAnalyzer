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
public class TopVolumesByAverageGenerator implements ReportGenerator{

    @Override
    public ReportSummary getReport(String _startDate, String _endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getReportAudioScript(String _date, int numOfItemsToInclude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockHistory> getReport(String _date, int _start, int _numResults) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
