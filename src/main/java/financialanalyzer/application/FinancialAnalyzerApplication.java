/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.application;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
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

}
