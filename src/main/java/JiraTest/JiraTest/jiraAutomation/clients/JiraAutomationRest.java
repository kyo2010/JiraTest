package JiraTest.JiraTest.jiraAutomation.clients;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

public class JiraAutomationRest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
            httpRequest.addHeader("Authorization",getBasicAuthenticationHeader("k.kimlaev@gmail.com","o7TCmMsMnAdAFbTZu1QP1378"));
            response = client.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code :"+ statusCode);
            String json = EntityUtils.toString(response.getEntity());
            System.out.println("Response:"+json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
