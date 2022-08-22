package JiraTest.JiraTest.jiraAutomation.clients;


import JiraTest.JiraTest.jiraAutomation.AutomationException;
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

import java.io.IOException;
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

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public static void main (String[] args){
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response;
        try {
            // priority list
            String uri = "https://kkv2022.atlassian.net/rest/api/2/priority";
            // IssueType List
            //String uri = "https://kkv2022.atlassian.net/rest/api/2/issuetype";
            // Project list
            //"https://kkv2022.atlassian.net/rest/api/2/project/10001"
            //String uri = "https://kkv2022.atlassian.net/rest/api/2/project";
            // Project Info
            //String prjKey = "PR";
            //String uri = "https://kkv2022.atlassian.net/rest/api/2/project/"+prjKey;
            //  https://developer.atlassian.com/cloud/jira/platform/rest/v2/api-group-issue-priorities/
            HttpGet httpRequest = new HttpGet(uri);
            String jira_token = System.getenv().get("JIRA_TOKEN");
            httpRequest.addHeader("Authorization",getBasicAuthenticationHeader("k.kimlaev@gmail.com",jira_token));
            response = client.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code :"+ statusCode);
            String json = EntityUtils.toString(response.getEntity());
            System.out.println("Response:"+json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Long> getTaskIdByName(String projectKey) throws AutomationException {
        Map<String, Long> result = new HashMap<>();
        try {
            JSONObject project = restExecuter.executeTask("rest/api/2/project/" + projectKey, null);
            JSONArray types = project.getJSONArray("issueTypes");
            for (int i=0; i<types.length(); i++){
                JSONObject type = types.getJSONObject(i);
                result.put(type.getString("name"),Long.parseLong(type.getString("id")));
            }
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

          JSONObject createdIssue = restExecuter.executeTask("rest/api/2/issue",payloadString);

          return createdIssue.get("key").toString();
        }catch(Exception e){
            log.error("error",e);
            throw new AutomationException("Reading result is error",e.getMessage());
        }
    }
}
