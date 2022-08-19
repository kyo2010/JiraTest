package JiraTest.JiraTest.jiraAutomation.processes;

import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.IAutomationJiraClient;
import JiraTest.JiraTest.jiraAutomation.IAutomationProcess;
import com.atlassian.jira.rest.client.api.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XMLUploaderProcess implements IAutomationProcess {

    static Map<String, String> ISSUE_TYPES_ALIAS = new HashMap<>();
    static Map<String, Long> PRIORITIES_ID = new HashMap<>();
    static{
        ISSUE_TYPES_ALIAS.put("task","Задача");

        PRIORITIES_ID.put("Highest",1L);
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    class IssueRecord{
        public String id;
        public String type;
        public String priority;
        public String summary = "";

        public IssueRecord(Element elIssue) {
            id = elIssue.getElementsByTagName("id").item(0).getTextContent();
            type = elIssue.getElementsByTagName("type").item(0).getTextContent();
            priority = elIssue.getElementsByTagName("priority").item(0).getTextContent();
        }
        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    String projectKey = null;

    public List<IssueRecord> parseXML() throws AutomationException{
        List<IssueRecord> list = new ArrayList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream("static/xmlJiraProcess/InputFile.xml"));

            if (doc==null) throw new AutomationException("File is not found","XML task file is not found");
            doc.getDocumentElement().normalize();

            projectKey = doc.getDocumentElement().getElementsByTagName("projectKey").item(0).getFirstChild().getNodeValue();
            log.info("[automation] project:"+projectKey);
            if (projectKey==null) throw new AutomationException("Parsing error","Error read projectKey value");

            NodeList xmlIssues = doc.getDocumentElement().getElementsByTagName("issues").item(0).getChildNodes();
            for (int issueIndex=0;  issueIndex<xmlIssues.getLength(); issueIndex++){
                Node xmlIssue = xmlIssues.item(issueIndex);
                if (xmlIssue.getNodeType()==Node.ELEMENT_NODE && xmlIssue.getNodeName().equalsIgnoreCase("issue")) {
                    IssueRecord ir = new IssueRecord((Element) xmlIssue);
                    list.add(ir);
                    log.info("[automation] issue id:" + ir.id + " type:" + ir.type + " priority:" + ir.priority);
                }
            }

            NodeList xmlDescriptions = doc.getDocumentElement().getElementsByTagName("descriptions").item(0).getChildNodes();
            for (int xmlIndex=0;  xmlIndex<xmlDescriptions.getLength(); xmlIndex++){
                Node xmlDescription = xmlDescriptions.item(xmlIndex);
                if (xmlDescription.getNodeType()==Node.ELEMENT_NODE && xmlDescription.getNodeName().equalsIgnoreCase("description")) {
                    Element elDescription = (Element) xmlDescription;
                    String id = elDescription.getElementsByTagName("id").item(0).getTextContent();
                    String summary = elDescription.getElementsByTagName("summary").item(0).getTextContent();
                    IssueRecord foundIssue = null;
                    for (IssueRecord ir : list){
                        if (ir.id.equalsIgnoreCase(id)){
                            foundIssue = ir;
                            break;
                        }
                    }
                    if (foundIssue==null) {
                        log.info("[automation] Issue is nt found, description id:" + id + " summary:" + summary);
                    }  else{
                        foundIssue.setSummary(summary);
                        log.info("[automation] setup summary id:" + id + " summary:" + summary);
                    }
                }
            }
        }catch(AutomationException ae){
            throw ae;
        }catch(Exception e){
            String error_info = "Parse xml file is error.";
            log.error(error_info, e);
            throw new AutomationException("Error", e.getCause(), error_info);
        }
        return list;
    }

    @Override
    public void execute(IAutomationJiraClient client) throws AutomationException {
        try {
            List<IssueRecord> issueList = parseXML();
            Map<String, Long> tasksIdByName = client.getTaskIdByName(projectKey);

            for (IssueRecord issue: issueList){
                Long issueType = tasksIdByName.get(issue.type);
                Long priorityID = PRIORITIES_ID.get(issue.priority);
                if (issueType==null) issueType = tasksIdByName.get(ISSUE_TYPES_ALIAS.get(issue.type));
                if (issueType==null) throw new AutomationException("Automation error","Issue type is not found. issue type : "+issue.type);
                if (priorityID==null) throw new AutomationException("Automation error","Priority id is not detected. Priority name : "+issue.priority);
                client.createIssue(projectKey,issueType,issue.summary,priorityID);
            }
        }catch(AutomationException ae) {
            throw ae;
        }catch(RestClientException rce){
            throw new AutomationException("Automation is error",rce.getMessage());
        }catch(Exception e){
          String error_info = "Parse xml file is error.";
          log.error(error_info, e);
          throw new AutomationException("Error", e.getCause(), error_info);
        }
    }
}
