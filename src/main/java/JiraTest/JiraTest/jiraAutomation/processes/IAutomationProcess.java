package JiraTest.JiraTest.jiraAutomation.processes;

import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.IAutomationJiraClient;

public interface IAutomationProcess {
    void execute(IAutomationJiraClient client) throws AutomationException;
}
