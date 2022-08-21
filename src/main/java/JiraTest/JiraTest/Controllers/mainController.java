package JiraTest.JiraTest.Controllers;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationJavaAPI;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationRest;
import JiraTest.JiraTest.jiraAutomation.processes.XMLUploaderProcess;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
public class mainController {

    private final JiraConfig jiraConfig;
    private final JiraAutomationJavaAPI jiraAutomationJavaAPI;
    private final JiraAutomationRest jiraAutomationRestAPI;
    private final XMLUploaderProcess xmlUploaderProcess;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String failRestResult = "";

    @RequestMapping("/controlPanel")
    public String controlPanel(@RequestParam(name="mode", required = false)String mode, Model model){
        log.info("Control panel page" );
        String result = "";
        failRestResult = "";
        model.addAttribute("host",jiraConfig.getJiraHost());
        model.addAttribute("user",jiraConfig.getJiraUser());
        try{
        if ("projectList".equalsIgnoreCase(mode)){
            try {
                Iterable<BasicProject> prjs = jiraAutomationJavaAPI.getRestClient().getProjectClient().getAllProjects().get();
                result = "*** Project list ***<br/>";
                for (BasicProject prj : prjs){
                    String prjInfo = "Project id:"+ prj.getId()+" key:"+prj.getKey()+" url:"+prj.getSelf();
                    result+=prjInfo+"<br/>";
                }
            }catch(Exception e){
                log.error("mode:"+mode+" is error",e);
                result = "Project list reading error...";
            }
            log.info("mode:"+mode+" res:"+result);
        }
        if ("getMetaData".equalsIgnoreCase(mode)){
            try {
                Iterable<IssueType> types = jiraAutomationJavaAPI.getRestClient().getMetadataClient().getIssueTypes().get();
                Iterable<Priority> list = jiraAutomationJavaAPI.getRestClient().getMetadataClient().getPriorities().get();
                result = "*** Issue type list ***<br/>";
                for (IssueType t : types){
                    String info = "Type id:"+ t.getId()+" name:"+t.getName()+" dsc:"+t.getDescription();
                    result+=info+"<br/>";
                }
                result += "<br/>*** Priority ***<br/>";
                for (Priority priority : list){
                    String info = "priority id:"+ priority.getId()+" name:"+priority.getName();
                    result+=info+"<br/>";
                }

            }catch(Exception e){
                log.error("mode:"+mode+" is error",e);
                result = "Issue type list reading error... "+failRestResult;
            }
            log.info("mode:"+mode+" res:"+result);
        }
        if ("projectInfo".equalsIgnoreCase(mode)){
            try {
                Project prj = jiraAutomationJavaAPI.getRestClient().getProjectClient().getProject("PR").get();
                result = "*** Project Info ***<br/>";
                if (prj!=null){
                    String prjInfo = "Project id:"+ prj.getId()+" key:"+prj.getKey();
                    result+=prjInfo+"<br/>";
                    for(IssueType it : prj.getIssueTypes()){
                        result+="Issue Type:"+it.getId()+" name:"+it.getName()+"<br/>";
                    };
                }
            }catch(Exception e){
                log.error("mode:"+mode+" is error",e);
                result = "project info reading is error... "+failRestResult;
            }

            // Test Rest API
            try {
              jiraAutomationRestAPI.getTaskIdByName("PR");
            }catch (AutomationException ae){
                log.error("mode:"+mode+" is error",ae);
                result = ae.getMessage()+":"+ae.getDescription();
            }

            log.info("mode:"+mode+" res:"+result);
        }
        if ("createIssue".equalsIgnoreCase(mode)){
                IssueInputBuilder issueBuilder = new IssueInputBuilder("PR", (long)10004,"Test summary");
                issueBuilder.setDescription("Test Description");
                issueBuilder.setPriorityId((long)1);
                BasicIssue issue = jiraAutomationJavaAPI.getRestClient().getIssueClient().createIssue(issueBuilder.build()).get();
                String key = issue.getKey();
                result = "Issue has been created : "+key;
            log.info("mode:"+mode+" res:"+result);
        }
        if ("execute_process".equalsIgnoreCase(mode)){
            try {
                xmlUploaderProcess.execute(jiraAutomationJavaAPI);
                result = "ok";
            }catch(AutomationException ae){
                result = ae.getMessage()+" "+ae.getDescription();
            }
        }
        }catch (InterruptedException ie){
            log.error("getTaskIdByName",ie);
            result = "InterruptedException:"+ie.getMessage();
        }catch (ExecutionException ee){
            log.error("getTaskIdByName",ee);
            result = "ExecutionException:"+ee.getMessage();
        }
        model.addAttribute("result",result);
        return "controlPanel";
    }

    @GetMapping("/test")
    public @ResponseBody String test(){
        return "Test page";
    }

}
