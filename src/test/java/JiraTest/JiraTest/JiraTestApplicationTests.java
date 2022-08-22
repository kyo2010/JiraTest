package JiraTest.JiraTest;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationJavaAPI;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationRest;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Project;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@ActiveProfiles("test")
class JiraTestApplicationTests {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JiraConfig config;

	@Autowired
	private JiraAutomationJavaAPI jiraAutomation;

	@Autowired
	private JiraAutomationRest jiraAutomationRest;

	@Test
	void unitTest() throws ExecutionException, InterruptedException, AutomationException {
		Assert.hasText(config.getJiraHost(),"Jira host is empty!");
		Assert.hasText(config.getJiraUser(),"Jira user is empty!");
		Assert.hasText(config.getJiraToken(),"Jira token is empty!");
	}

	@Test
	void integrationTest() throws ExecutionException, InterruptedException, AutomationException {
		// Check Java Jira API
		Iterable<BasicProject> projects = jiraAutomation.getRestClient().getProjectClient().getAllProjects().get();
		log.info("Projects : ");
		for (BasicProject prj : projects){
			log.info("  "+prj.getId()+" "+prj.getKey());
		}
		Assert.notNull(projects,"Check project list");
		// Check standard Jira Rest API
		Map<String,Long>issueTypes = jiraAutomationRest.getTaskIdByName("PR");
		log.info("Issue Types : ");
		issueTypes.forEach((k,v)->{log.info("  name:"+k+" id:"+v);});
		Assert.notNull(issueTypes,"Check issues types for PR");
	}
}
