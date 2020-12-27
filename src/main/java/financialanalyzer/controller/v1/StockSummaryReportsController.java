/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.controller.v1;

import financialanalyzer.stockhistory.StockHistory;
import financialanalyzer.stockhistory.StockHistorySearchProperties;
import financialanalyzer.report.ReportGenerator;
import financialanalyzer.report.ReportGeneratorRegistry;
import financialanalyzer.report.ReportSummary;
import financialanalyzer.report.TopVolumesByAmountGenerator;
import financialanalyzer.stockhistory.StockHistoryRepo;
import financialanalyzer.stockperformance.StockPerformance;
import financialanalyzer.stockperformance.StockPerformanceRepo;
import financialanalyzer.stockperformance.StockPerformanceSearchProperties;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author pldor
 */
@RestController
@RequestMapping("/api/v1/reports/stocks")
public class StockSummaryReportsController {

    private static final Logger logger = LoggerFactory.getLogger(StockSummaryReportsController.class.getName());

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final static String TOP_GAINERS_BY_AMOUNT = "top-gainers-by-amount";
    private final static String TOP_GAINERS_BY_PERCENTAGE = "top-gainers-by-percentage";
    private final static String TOP_LOSERS_BY_AMOUNT = "top-losers-by-amount";
    private final static String TOP_LOSERS_BY_PERCENTAGE = "top-losers-by-percentage";
    private final static String TOP_VOLUMES_BY_AMOUNT = "top-volumes-by-amount";
    private final static String TOP_VOLUMES_BY_AVERAGE = "top-volumes-by-average";

    //@Autowired
    //private StockHistoryRepo stockHistorySearchRepo;

    @Autowired
    private StockPerformanceRepo stockPerformanceSearchRepo;

    @Autowired
    private ReportGeneratorRegistry reportGeneratorRegistry;

    @RequestMapping(value = "/report/{name}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getReportForName(@PathVariable("name") String _name) {
        String dateString = sdf.format(new Date());
        return this.getReportForNameEndingOnDate(_name, dateString);
    }

    @RequestMapping(value = "/report/{name}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getReportForNameEndingOnDate(@PathVariable("name") String _name, @PathVariable("endDate") String _endDate) {

        return this.getReportForNameEndingOnDateForNumberOfDays(_name, _endDate, "30");
    }

    @RequestMapping(value = "/report/{name}/{endDate}/{numberOfDays}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getReportForNameEndingOnDateForNumberOfDays(@PathVariable("name") String _name, @PathVariable("endDate") String _endDate, @PathVariable("numberOfDays") String _numberOfDays) {
        RestResponse response = new RestResponse();
        ReportSummary report = this.getReport(_name, _endDate, Integer.parseInt(_numberOfDays));
        response.setObject(report);
        return response;

    }

    private ReportSummary getReport(String _name, String _endDate, int _numOfDays) {
        ReportGenerator reportGenerator = this.getReportGenerator(_name);
        if (reportGenerator != null) {
            ReportSummary reportSummary = reportGenerator.getReport(this.getStartDate(_endDate, _numOfDays), _endDate);
            return reportSummary;
        }
        return null;
    }

    private ReportGenerator getReportGenerator(String _name) {
        return new TopVolumesByAmountGenerator();
    }

    /*
    private ReportSummary getTopVolumesByAmount(String _endDate, int _numOfDays) {
        ReportSummary report = new ReportSummary();
        report.setName("Top Volumes By Amount");
        report.setEndDate(_endDate);
        report.setStartDate(this.getStartDate(_endDate, _numOfDays));
        return report;
    }

    private ReportSummary getTopVolumesByAverage(String _endDate, int _numOfDays) {
        ReportSummary report = new ReportSummary();
        report.setName("Top Volumes By Average");
        report.setEndDate(_endDate);
        report.setStartDate(this.getStartDate(_endDate, _numOfDays));
        return report;
    }

   
    private ReportSummary getTopGainersByAmount(String _endDate, int _numOfDays) {
        ReportSummary report = new ReportSummary();
        report.setName("Top Gainers By Amount");
        report.setEndDate(_endDate);
        report.setStartDate(this.getStartDate(_endDate, _numOfDays));
        return report;
    }

    private ReportSummary getTopLosersByAmount(String _endDate, int _numOfDays) {
        ReportSummary report = new ReportSummary();
        report.setName("Top Losers By Amount");
        report.setEndDate(_endDate);
        report.setStartDate(this.getStartDate(_endDate, _numOfDays));
        return report;
    }

    private ReportSummary getTopGainersByPercentage(String _endDate, int _numOfDays) {
        ReportSummary report = new ReportSummary();
        report.setName("Top Gainers By Percentage");
        report.setEndDate(_endDate);
        report.setStartDate(this.getStartDate(_endDate, _numOfDays));
        return report;
    }

    private ReportSummary getTopLosersByPercentage(String _endDate, int _numOfDays) {
        ReportSummary report = new ReportSummary();
        report.setName("Top Losers By Percentage");
        report.setEndDate(_endDate);
        report.setStartDate(this.getStartDate(_endDate, _numOfDays));
        return report;
    }
     */
    private String getStartDate(String _endDateString, int _numOfDays) {
        try {
            Date endDate = sdf.parse(_endDateString);
            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.setTime(endDate);
            endDateCalendar.add(Calendar.DAY_OF_YEAR, -1 * Math.abs(_numOfDays));
            String startDateString = sdf.format(endDateCalendar.getTime());
            return startDateString;
        } catch (ParseException ex) {
            logger.error("Parse Exception", ex);
            return null;
        }

    }


    private List<StockHistory> buildDailyReport(String _reportId, String _endDate, int _start, int _numItems) {
        for (ReportGenerator reportGenerator : this.reportGeneratorRegistry.getReportGenerators()) {
            if (reportGenerator.getId().equalsIgnoreCase(_reportId)) {
                return reportGenerator.getReport(_endDate, _start, _numItems);
            }
        }
        return null;
    }


    @RequestMapping(value = "/stockPerformance/{reportType}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getStockPerformanceForDay(@PathVariable("endDate") String endDate, @PathVariable("reportType") String reportType) {
        return this.getStockPerformanceForDayStartingWith(endDate, reportType, 0);
    }

    @RequestMapping(value = "/stockPerformance/{reportType}/{endDate}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getStockPerformanceForDayStartingWith(@PathVariable("endDate") String endDate, @PathVariable("reportType") String reportType, @PathVariable("start") int _start) {
        return this.getStockPerformanceForDayStartingWithNumResults(endDate, reportType, _start, 25);
    }

    @RequestMapping(value = "/stockPerformance/{reportType}/{endDate}/{start}/{numResults}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getStockPerformanceForDayStartingWithNumResults(@PathVariable("endDate") String endDate, @PathVariable("reportType") String reportType, @PathVariable("start") int _start, @PathVariable("numResults") int _numResults) {
        RestResponse restResponse = new RestResponse();
        List<StockPerformance> stockPerformanceItems = new ArrayList<>();

        StockPerformanceSearchProperties shsp = new StockPerformanceSearchProperties();
        shsp.setSearchDate(endDate);
        shsp.setStartResults(_start);
        shsp.setNumResults(_numResults);

        String[] reportFilter = reportType.split("-");
        shsp.setSortField(reportFilter[0]);
        shsp.setSortOrder(reportFilter[1]);
        List<StockPerformance> shs = this.stockPerformanceSearchRepo.searchForStockPerformance(shsp);
        if (shs != null) {
            stockPerformanceItems.addAll(shs);
        }

        restResponse.setObject(stockPerformanceItems);
        return restResponse;

    }
    @RequestMapping(value = "/dailyReport/{reportName}/{endDate}/{start}/{numberOfItems}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getDailyReportStartingWithNumResults(@PathVariable("reportName") String reportName,@PathVariable("endDate") String endDate, @PathVariable("start") int _start, @PathVariable("numberOfItems") int _numberOfItems) {
        RestResponse restResponse = new RestResponse();
        List<StockHistory> stockHistories = this.buildDailyReport(reportName, endDate, _start, _numberOfItems);

        restResponse.setObject(stockHistories);
        return restResponse;
    }

    @RequestMapping(value = "/dailyReport/{reportName}/{endDate}/{start}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getDailyReportStartingWith(@PathVariable("reportName") String reportName,@PathVariable("endDate") String endDate, @PathVariable("start") int _start) {
        return this.getDailyReportStartingWithNumResults(reportName,endDate, _start, 25);
    }

    @RequestMapping(value = "/dailyReport/{reportName}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse getDailyReport(@PathVariable("reportName") String reportName,@PathVariable("endDate") String endDate) {
        return this.getDailyReportStartingWith(reportName,endDate, 0);
    }
}
