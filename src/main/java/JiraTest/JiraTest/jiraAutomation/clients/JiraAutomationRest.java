package JiraTest.JiraTest.jiraAutomation.clients;


import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.jiraModels.JiraIssueType;
import JiraTest.JiraTest.jiraAutomation.clients.jiraModels.JiraProject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Clien for Jira Rest API v2.0
 * please see documentation bellow:
 * https://developer.atlassian.com/cloud/jira/platform/rest/v2/api-group-issue-priorities/
 * */
@Component
@RequiredArgsConstructor
public class JiraAutomationRest implements IAutomationJiraClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final HttpRestExecuter restExecuter;
    private ObjectMapper mapper = new ObjectMapper();

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    @Override
    public Map<String, Long> getTaskIdByName(String projectKey) throws AutomationException {
        Map<String, Long> result = new HashMap<>();
        try {
            String response = restExecuter.executeTask("rest/api/2/project/" + projectKey, null);
            JiraProject jProj = mapper.readValue(response,JiraProject.class);
            jProj.getIssueTypes().forEach(type->result.put(type.getName(),Long.parseLong(type.getId())));
        }catch(Exception e){
           log.error("error",e);
           throw new AutomationException("Reading result is error",e.getMessage());
        }
        return result;
    }

    @Override
    public String createIssue(String projectKey, Long issueTypeId, String summary, Long priorityID) throws AutomationException {
        try {
          JsonNodeFactory jnf = JsonNodeFactory.instance;
          ObjectNode payload = jnf.objectNode();
          ObjectNode fields = payload.putObject("fields");
          ObjectNode project = fields.putObject("project");
          project.put("key",projectKey);
          fields.put("summary", summary);
          fields.put("description","created base on Jira rest API");
          ObjectNode issuetype = fields.putObject("issuetype");
          issuetype.put("id",issueTypeId);
          ObjectNode priority = fields.putObject("priority");
          priority.put("id",""+priorityID);

          String payloadString = payload.toString();

          JSONObject createdIssue = new JSONObject(restExecuter.executeTask("rest/api/2/issue",payloadString));

          return createdIssue.get("key").toString();
        }catch(Exception e){
            log.error("error",e);
            throw new AutomationException("Reading result is error",e.getMessage());
        }
    }
}
