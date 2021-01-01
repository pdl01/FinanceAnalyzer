/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.controller.v1;

import financialanalyzer.report.ReportGenerator;
import financialanalyzer.report.ReportGeneratorRegistry;
import financialanalyzer.stockhistory.StockHistory;
import java.util.List;
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
@RequestMapping("/api/v1/publicpackage")
public class VideoPackageRestController {
    @Autowired
    private ReportGeneratorRegistry reportGeneratorRegistry;
    
    @RequestMapping(value = "/audioscript/{endDate}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse generateSummaryScriptForItem(@PathVariable("reportName") String _reportName,@PathVariable("endDate") String _endDate) {
        RestResponse restResponse = new RestResponse();
        String script = this.buildDailyScriptForReport(_reportName, _endDate,10);

        restResponse.setObject(script);
        return restResponse;
    }
    
    @RequestMapping(value = "/videoTags/{endDate}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse generateVideoTagsForItem(@PathVariable("reportName") String _reportName,@PathVariable("endDate") String _endDate) {
        RestResponse restResponse = new RestResponse();
        String reportTags = "";
        for (ReportGenerator reportGenerator : this.reportGeneratorRegistry.getReportGenerators()) {
            if (reportGenerator.getId().equalsIgnoreCase(_reportName)) {
                reportTags =  reportGenerator.getReportTags(_endDate, 10);
            }
        }
        

        restResponse.setObject(reportTags);
        return restResponse;
    }
    
    @RequestMapping(value = "/audioreport/{endDate}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse generateSummaryAudioForItem(@PathVariable("reportName") String _reportName,@PathVariable("endDate") String _endDate) {
        RestResponse restResponse = new RestResponse();

        return restResponse;
    }

    @RequestMapping(value = "/videoreport/{endDate}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public RestResponse geneateSummaryVideoForItem(@PathVariable("reportName") String _reportName,@PathVariable("endDate") String _endDate) {
        RestResponse restResponse = new RestResponse();
 
        return restResponse;
    }


    private String buildDailyScriptForReport(String _reportId, String _endDate, int _numItems) {
        for (ReportGenerator reportGenerator : this.reportGeneratorRegistry.getReportGenerators()) {
            if (reportGenerator.getId().equalsIgnoreCase(_reportId)) {
                return reportGenerator.getReportAudioScript(_endDate, _numItems);
            }
        }
        return null;
    }

}
