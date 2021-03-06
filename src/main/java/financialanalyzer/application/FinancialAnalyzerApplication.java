/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.application;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import financialanalyzer.config.AppConfig;
import java.io.File;
import javax.annotation.PostConstruct;
import org.slf4j.FactoryFriend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 * @author pldor
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan("financialanalyzer")
@EnableWebMvc
@EnableJms
public class FinancialAnalyzerApplication implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(FinancialAnalyzerApplication.class.getName());
    
    @Autowired
    private AppConfig appConfig;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FinancialAnalyzerApplication.class);
        //app.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext ctx = app.run(args);
        //SpringApplication.run(SearchScraperApplication.class, args);

    }
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @PostConstruct
    private void init() {
        logger.info("Initializing FinancialAnalyzer");
        logger.debug(this.appConfig.getCompanyDownloadDir());
        this.createDirIfNotExists(appConfig.getApplicationHomeDir());
        this.createDirIfNotExists(appConfig.getCompanyDownloadDir());
        this.createDirIfNotExists(appConfig.getCompanyNewsDownloadDir());
        this.createDirIfNotExists(appConfig.getStockHistoryDownloadDir());
        this.createDirIfNotExists(appConfig.getLogDir());
        //this.reInitLogging();
        logger.info("Finished Initializing FinancialAnalyzer");
    }
    
    private void reInitLogging() {
        System.setProperty("log.path", appConfig.getLogDir());
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        Resource logConfigfile = resourceLoader.getResource("classpath:logback-spring.xml");
        try {
            configurator.doConfigure(logConfigfile.getFile());
        } catch (Exception e) {
            logger.error("Error in initializing logging");
        }
    }
    
    private void createDirIfNotExists(String _fileDir) {
        File dirFile = new File(_fileDir);
        
        if (!dirFile.isDirectory() && !dirFile.isFile()) {
            dirFile.mkdirs();
        }
    }
    
}
