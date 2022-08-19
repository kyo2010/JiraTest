package JiraTest.JiraTest.Controllers;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationAPI;
import JiraTest.JiraTest.jiraAutomation.processes.XMLUploaderProcess;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequiredArgsConstructor
public class mainController {

    private final JiraConfig jiraConfig;
    private final JiraAutomationAPI jiraAutomation;
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
        if ("projectList".equalsIgnoreCase(mode)){
            try {
                Promise<Iterable<BasicProject>> promiseList = jiraAutomation.getRestClient().getProjectClient().getAllProjects();
                result = "*** Project list ***<br/>";
                Iterable<BasicProject> prjs = promiseList.claim();
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
                Promise<Iterable<IssueType>> promiseList = jiraAutomation.getRestClient().getMetadataClient().getIssueTypes();
                Promise<Iterable<Priority>> promisePriorityList = jiraAutomation.getRestClient().getMetadataClient().getPriorities();
                promiseList.fail(e->{ failRestResult = e.getMessage();} );
                promisePriorityList.fail(e->{ failRestResult = e.getMessage();} );
                result = "*** Issue type list ***<br/>";
                Iterable<IssueType> types = promiseList.claim();
                for (IssueType t : types){
                    String info = "Type id:"+ t.getId()+" name:"+t.getName()+" dsc:"+t.getDescription();
                    result+=info+"<br/>";
                }
                Iterable<Priority> list = promisePriorityList.get();
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
                //Promise<Project> promiseProject = jiraAutomation.getRestClient().getProjectClient().getProject(URI.create("https://kkv2022.atlassian.net/rest/api/2/project/10001"));
                Promise<Project> promiseProject = jiraAutomation.getRestClient().getProjectClient().getProject("PR");
                promiseProject.fail(e->{ failRestResult = e.getMessage();} );
                result = "*** Project Info ***<br/>";
                Project prj = promiseProject.claim();
                if (prj!=null){
                    String prjInfo = "Project id:"+ prj.getId()+" key:"+prj.getKey();
                    result+=prjInfo+"<br/>";
                    for(IssueType it : prj.getIssueTypes()){
                        result+="Issue Type:"+it.getId()+" name:"+it.getName()+"<br/>";
                    };
                   // prj.
                }
            }catch(Exception e){
                log.error("mode:"+mode+" is error",e);
                result = "project info reading is error... "+failRestResult;
            }
            log.info("mode:"+mode+" res:"+result);
        }
        if ("createIssue".equalsIgnoreCase(mode)){
            Promise<BasicIssue> promiseIssue = null;
            try {
                IssueInputBuilder issueBuilder = new IssueInputBuilder("PR", (long)10004,"Test summary");
                issueBuilder.setDescription("Test Description");
                issueBuilder.setPriorityId((long)1);
                promiseIssue = jiraAutomation.getRestClient().getIssueClient().createIssue(issueBuilder.build());
                promiseIssue.fail(e->{ failRestResult = e.getMessage();} );
                String key = promiseIssue.claim().getKey();
                result = "Issue has been created : "+key;
            }catch(Exception e){
                log.error("mode:"+mode+" is error",e);
                result = "Create issue error. "+failRestResult;
            }
            log.info("mode:"+mode+" res:"+result);
        }
        if ("execute_process".equalsIgnoreCase(mode)){
            try {
                xmlUploaderProcess.execute(jiraAutomation);
                result = "ok";
            }catch(AutomationException ae){
                result = ae.getMessage()+" "+ae.getDescription();
            }
        }
        model.addAttribute("result",result);
        return "controlPanel";
    }

    @GetMapping("/test")
    public @ResponseBody String test(){
        return "Test page";
    }

}
