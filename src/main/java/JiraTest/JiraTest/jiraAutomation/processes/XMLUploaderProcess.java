package JiraTest.JiraTest.jiraAutomation.processes;

import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.IAutomationJiraClient;
import JiraTest.JiraTest.jiraAutomation.processes.xmlStructures.XMLData;
import JiraTest.JiraTest.jiraAutomation.processes.xmlStructures.XMLDescription;
import JiraTest.JiraTest.jiraAutomation.processes.xmlStructures.XMLIssue;
import com.atlassian.jira.rest.client.api.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component
public class XMLUploaderProcess implements IAutomationProcess {

    static Map<String, String> ISSUE_TYPES_ALIAS = new HashMap<>();
    static Map<String, Long> PRIORITIES_ID = new HashMap<>();
    static{
        ISSUE_TYPES_ALIAS.put("task","Задача");
        PRIORITIES_ID.put("Highest",1L);
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    String projectKey = null;

    public List<XMLIssue> parseXML() throws AutomationException{
        XmlMapper xmlMapper = new XmlMapper();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("static/xmlJiraProcess/InputFile.xml");
            XMLData data = xmlMapper.readValue(is, XMLData.class);
            projectKey = data.getProjectKey();
            for (XMLDescription description : data.getDescriptions()){
               for (XMLIssue issue : data.getIssues()){
                 if (description.getId().equalsIgnoreCase(issue.getId())){
                     issue.setDescription(description);
                     break;
                 }
               }
            }
            return  data.getIssues();
        }catch(Exception e){
            String error_info = "Parse xml file is error.";
            log.error(error_info, e);
            throw new AutomationException("Error", e.getCause(), error_info);
        }
    }

    @Override
    public void execute(IAutomationJiraClient client) throws AutomationException {
        try {
            List<XMLIssue> issueList = parseXML();
            Map<String, Long> tasksIdByName = client.getTaskIdByName(projectKey);

            for (XMLIssue issue: issueList){
                Long issueType = tasksIdByName.get(issue.getType());
                Long priorityID = PRIORITIES_ID.get(issue.getPriority());
                if (issueType==null) issueType = tasksIdByName.get(ISSUE_TYPES_ALIAS.get(issue.getType()));
                if (issueType==null) throw new AutomationException("Automation error","Issue type is not found. issue type : "+issue.getType());
                if (priorityID==null) throw new AutomationException("Automation error","Priority id is not detected. Priority name : "+issue.getPriority());
                client.createIssue(projectKey,issueType,issue.getDescription()==null?"":issue.getDescription().getSummary(),priorityID);
            }
        }catch(AutomationException ae) {
            throw ae;
        }catch(RestClientException rce){
            throw new AutomationException("Automation is error",rce.getMessage());
        }catch(RuntimeException rte) {
            String error_info = "JIRA API exception";
            log.error(error_info, rte);
            throw new AutomationException("Error", rte.getCause(), error_info);
        }catch(Exception e) {
            String error_info = "Parse xml file is error.";
            log.error(error_info, e);
            throw new AutomationException("Error", e.getCause(), error_info);
        }
    }
}
