package JiraTest.JiraTest.jiraAutomation.clients;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class HttpRestExecuter {

    private final JiraConfig jiraConfig;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public String executeTask(String request, String payload)throws AutomationException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response;
        try {
            String uri = jiraConfig.getJiraHost() +"/"+ request;
            // TODO : replace to Unirest, HttpResponse<JsonNode> response
            if (payload==null) {
                HttpGet httpRequest = new HttpGet(uri);
                httpRequest.addHeader("Authorization", getBasicAuthenticationHeader(jiraConfig.getJiraUser(), jiraConfig.getJiraToken()));
                response = client.execute(httpRequest);
            }else{
                HttpPost httpRequest = new HttpPost(uri);
                httpRequest.addHeader("Authorization", getBasicAuthenticationHeader(jiraConfig.getJiraUser(), jiraConfig.getJiraToken()));
                httpRequest.addHeader("Accept", "application/json");
                httpRequest.addHeader("Content-Type", "application/json");
                StringEntity stringEntity = new StringEntity(payload);
                log.info("Payload:"+payload);
                stringEntity.setContentEncoding("UTF-8");
                stringEntity.setContentType("application/json");
                httpRequest.setEntity(stringEntity);
                response = client.execute(httpRequest);
            }
            int statusCode = response.getStatusLine().getStatusCode();
            // TODO: extract error from JSON  (statusCode : 401 - authorization, 404 - not access)
            if (statusCode==401) throw new AutomationException("Request error","Token is not valid");
            if (statusCode==404) throw new AutomationException("Request error","You don't have permission to read this object");
            log.info("Response Code :"+ statusCode);
            String json = EntityUtils.toString(response.getEntity());
            log.info("Response:"+json);
            return json;
        } catch (Exception e) {
            log.error("API execution is error",e);
            throw new AutomationException("API execution is error",e.getCause(),e.getMessage());
        }
    }

}
