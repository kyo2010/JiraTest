package JiraTest.JiraTest.Controllers;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationJavaAPI;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationRest;
import JiraTest.JiraTest.jiraAutomation.processes.XMLUploaderProcess;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final JiraConfig jiraConfig;
    private final JiraAutomationJavaAPI jiraAutomationJavaAPI;
    private final JiraAutomationRest jiraAutomationRestAPI;
    private final XMLUploaderProcess xmlUploaderProcess;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/controlPanel")
    public String controlPanel(@RequestParam(name = "mode", required = false) String mode, Model model) {
        log.info("Control panel page");
        String result = "";
        model.addAttribute("host", jiraConfig.getJiraHost());
        model.addAttribute("user", jiraConfig.getJiraUser());
        if ("execute_process".equalsIgnoreCase(mode)) {
            try {
                xmlUploaderProcess.execute(jiraAutomationJavaAPI);
                result = "ok";
            } catch (AutomationException ae) {
                result = ae.getMessage() + " " + ae.getDescription();
            }
        }
        if ("execute_process2".equalsIgnoreCase(mode)) {
            try {
                xmlUploaderProcess.execute(jiraAutomationRestAPI);
                result = "ok";
            } catch (AutomationException ae) {
                result = ae.getMessage() + " " + ae.getDescription();
            }
        }
        model.addAttribute("result", result);
        return "controlPanel";
    }

    @GetMapping("/test")
    public @ResponseBody String test() {
        return "Test page";
    }

}
