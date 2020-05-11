/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.config;

import java.io.File;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author pldor
 */
@Configuration
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties",ignoreResourceNotFound = true)
@PropertySource(value = "file:${app_home}/config/application-${spring.profiles.active}.properties",ignoreResourceNotFound = true)
public class AppConfig {
    
    @Value("${app_home}")
    private String applicationHomeDir;

    public String getApplicationHomeDir() {
        return applicationHomeDir;
    }

    //@Value("${dir.download.stockHistory}")
    //private String stockHistoryDownloadDir;
    //public final static String stockHistoryDownloadDir = "/tmp/fa/work/stockdownloads";
    //@Value("${dir.download.companies}")
    //private String companyDownloadDir;
    //public final static String companyDownloadDir = "/tmp/fa/work/companydownloads";
    //@Value("${dir.download.companyreport}")
    //private String companyReportDir;
    //public final static String companyReportDownloadDir = "/tmp/fa/work/companyReportDownloads";
    //@Value("${dir.download.companynews}")
    //private String companyNewsDownloadDir;
    //public final static String companyNewsDownloadDir = "/tmp/fa/work/companyNewsDownloads";
    //@Value("${dir.sentimentanalysis.companynews.positive}")
    //private String companyNewsPostiveSentimentAnalysisRefDir;
    public void setApplicationHomeDir(String applicationHomeDir) {
        this.applicationHomeDir = applicationHomeDir;
    }

    @Value("${provider.stockhistory.preferred: default}")
    private String preferredStockHistoryProvider;

    @Value("${provider.companynews.preferred: default}")
    private String preferredCompanyNewsProvider;

    @Value("${provider.companyname.preferred: default}")
    private String preferredCompanyNameProvider;

    public final static SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public String getLogDir() {
        return this.applicationHomeDir + File.separator + "logs";
    }
    public String getStockHistoryDownloadDir() {
        return this.applicationHomeDir + File.separator + "work" + File.separator + "downloads" + File.separator + "stockhistory";
        //return stockHistoryDownloadDir;
    }

    //public void setStockHistoryDownloadDir(String stockHistoryDownloadDir) {
    //    this.stockHistoryDownloadDir = stockHistoryDownloadDir;
    //}

    public String getCompanyDownloadDir() {
        return this.applicationHomeDir + File.separator + "work" + File.separator + "downloads" + File.separator + "company";
        //return companyDownloadDir;
    }

    //public void setCompanyDownloadDir(String companyDownloadDir) {
    //    this.companyDownloadDir = companyDownloadDir;
    //}

    public String getCompanyReportDir() {
        return this.applicationHomeDir + File.separator + "work" + File.separator + "downloads" + File.separator + "companyreports";
    }

    //public void setCompanyReportDir(String companyReportDir) {
    //    this.companyReportDir = companyReportDir;
    //}

    public String getCompanyNewsDownloadDir() {
        return this.applicationHomeDir + File.separator + "work" + File.separator + "downloads" + File.separator + "companynews";
    }

    public String getCompanyNewsPostiveSentimentAnalysisRefDir() {
        return this.applicationHomeDir + File.separator + "sentimentanalysis" + File.separator + "companynews" + File.separator + "postive";
    }

    //public void setCompanyNewsPostiveSentimentAnalysisRefDir(String companyNewsPostiveSentimentAnalysisRefDir) {
    //    this.companyNewsPostiveSentimentAnalysisRefDir = companyNewsPostiveSentimentAnalysisRefDir;
    //}

    //public void setCompanyNewsDownloadDir(String companyNewsDownloadDir) {
    //    this.companyNewsDownloadDir = companyNewsDownloadDir;
    //}

    public String getPreferredStockHistoryProvider() {
        return preferredStockHistoryProvider;
    }

    public void setPreferredStockHistoryProvider(String preferredStockHistoryProvider) {
        this.preferredStockHistoryProvider = preferredStockHistoryProvider;
    }

    public String getPreferredCompanyNewsProvider() {
        return preferredCompanyNewsProvider;
    }

    public void setPreferredCompanyNewsProvider(String preferredCompanyNewsProvider) {
        this.preferredCompanyNewsProvider = preferredCompanyNewsProvider;
    }

    public String getPreferredCompanyNameProvider() {
        return preferredCompanyNameProvider;
    }

    public void setPreferredCompanyNameProvider(String preferredCompanyNameProvider) {
        this.preferredCompanyNameProvider = preferredCompanyNameProvider;
    }
    
    
    
}
