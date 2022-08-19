package JiraTest.JiraTest.jiraAutomation;

import java.util.Map;

public interface IAutomationJiraClient {

    Map<String, Long> getTaskIdByName(String projectKey) throws AutomationException;

    String createIssue(String projectKey, Long issueType, String summary, Long priorityID) throws AutomationException;

}
