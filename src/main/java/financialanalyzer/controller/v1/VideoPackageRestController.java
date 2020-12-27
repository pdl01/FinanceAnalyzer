/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package financialanalyzer.controller.v1;

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

    @RequestMapping(value = "/audioscript/{date}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public void generateSummaryScriptForItem() {

    }
    @RequestMapping(value = "/audioreport/{date}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public void generateSummaryAudioForItem() {

    }
    @RequestMapping(value = "/videoreport/{date}/{reportName}", method = RequestMethod.GET, produces = "application/json")
    public void geneateSummaryVideoForItem() {

    }
}
