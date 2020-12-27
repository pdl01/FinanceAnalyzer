/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.report;

import financialanalyzer.controller.v1.StockSummaryReportsController;
import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockhistory.StockHistoryReportSearchRepo;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.search.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author pldor
 */
@Component
public class TopVolumesByAmountGenerator implements ReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopVolumesByAmountGenerator.class.getName());

    @Autowired
    private StockHistoryReportSearchRepo stockHistoryReportSearchRepo;
    @Autowired
    private StockHistoryRepo stockHistorySearchRepo;
    @Override
    public ReportSummary getReport(String _startDate, String _endDate) {
        SearchRequest searchRequest = new SearchRequest(StockHistoryReportSearchRepo.STOCK_HISTORY_INDEX);

        this.stockHistoryReportSearchRepo.searchForReport(searchRequest);
        ReportSummary summary = null;
        return summary;
    }

    @Override
    public String getReportAudioScript(String _date, int numOfItemsToInclude) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getId() {
        return "volumes";
    }

    @Override
    public List<StockHistory> getReport(String _date, int _start, int _numResults) {
        LOGGER.debug("Inside getReport");
        List<StockHistory> stockHistories = new ArrayList<>();

        StockHistorySearchProperties shsp = new StockHistorySearchProperties();
        shsp.setSearchDate(_date);
        shsp.setStartResults(_start);
        shsp.setNumResults(_numResults);
        shsp.setSortField("volume");
        shsp.setSortOrder("DESC");
        List<StockHistory> shs = this.stockHistorySearchRepo.searchForStockHistory(shsp);
        if (shs != null) {
            stockHistories.addAll(shs);
        }
        return stockHistories;
    }

}
