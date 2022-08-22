package JiraTest.JiraTest.jiraAutomation.clients;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class JiraAutomationJavaAPI implements IAutomationJiraClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final JiraConfig jiraConfig;

    private JiraRestClient restClient = null;
    private String lastErrorMessage = null;

    @PostConstruct
    public void init() {
        try {
            restClient =  new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create(jiraConfig.getJiraHost()), jiraConfig.getJiraUser(), jiraConfig.getJiraToken());
        }catch(Exception e){
            log.error("Connection setup to Jira is error", e);
            lastErrorMessage = "Connection setup to Jira is error";
        }
    }

    public void resetLastError(){
        lastErrorMessage = null;
    }

    public JiraRestClient getRestClient() {
        return restClient;
    }

    @PreDestroy
    public void onDestroy() {
        try{
          if (restClient!=null) restClient.close();
        }catch(Exception e){
            log.error("Close Jira connection is error", e);
            lastErrorMessage = "Close JIRA connection is error";
        }
        restClient = null;
    }

    public Map<String, Long> getTaskIdByName(String projectKey) throws AutomationException{
        try {
            Map<String, Long> result = new HashMap<>();
            Project project = restClient.getProjectClient().getProject(projectKey).get();
            project.getIssueTypes().forEach(it -> result.put(it.getName(), it.getId()));
            return result;
        }catch (InterruptedException ie){
            log.error("getTaskIdByName",ie);
            throw new AutomationException("error",ie.getMessage());
        }catch (ExecutionException ee){
            log.error("getTaskIdByName",ee);
            throw new AutomationException("error",ee.getMessage());
        }
    };
    public String createIssue(String projectKey, Long issueType, String summary, Long priorityID) throws AutomationException{
        try {
            IssueInputBuilder issueBuilder = new IssueInputBuilder(projectKey, issueType, summary);
            issueBuilder.setDescription(summary);
            issueBuilder.setPriorityId(priorityID);
            String key = restClient.getIssueClient().createIssue(issueBuilder.build())
                    .get().getKey();
            return key;
        }catch (InterruptedException ie){
            log.error("getTaskIdByName",ie);
            throw new AutomationException("error",ie.getMessage());
        }catch (ExecutionException ee){
            log.error("getTaskIdByName",ee);
            throw new AutomationException("error",ee.getMessage());
        }
    };


}
