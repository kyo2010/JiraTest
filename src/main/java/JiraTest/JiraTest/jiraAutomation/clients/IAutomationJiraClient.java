package JiraTest.JiraTest.jiraAutomation.clients;

import JiraTest.JiraTest.jiraAutomation.AutomationException;

import java.util.Map;

public interface IAutomationJiraClient {
    /** Receive all project IssueTypes, name->id */
    Map<String, Long> getTaskIdByName(String projectKey) throws AutomationException;
    /** Create Issue */
    String createIssue(String projectKey, Long issueType, String summary, Long priorityID) throws AutomationException;
}
