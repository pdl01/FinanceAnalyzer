/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.report;

/**
 *
 * @author pldor
 */
public interface ReportGenerator {
    public ReportSummary getReport(String _startDate,String _endDate);
}