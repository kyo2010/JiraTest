package JiraTest.JiraTest;

import JiraTest.JiraTest.Configs.JiraConfig;
import JiraTest.JiraTest.jiraAutomation.AutomationException;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationJavaAPI;
import JiraTest.JiraTest.jiraAutomation.clients.JiraAutomationRest;
import JiraTest.JiraTest.jiraAutomation.clients.HttpRestExecuter;
import JiraTest.JiraTest.jiraAutomation.processes.XMLUploaderProcess;
import JiraTest.JiraTest.jiraAutomation.processes.xmlStructures.XMLIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

	@Autowired
	private XMLUploaderProcess xmlUploaderProcess;

	@Autowired
	@MockBean
	private HttpRestExecuter restExecuter;

	@Test
	void configTest() {
		Assert.hasText(config.getJiraHost(),"Jira host is empty!");
		Assert.hasText(config.getJiraUser(),"Jira user is empty!");
		Assert.hasText(config.getJiraToken(),"Jira token is empty!");
	}

	@Test
	void checkXMLReader() throws AutomationException {
		List<XMLIssue> issues = xmlUploaderProcess.parseXML();
		Assert.state(issues.size()==3,"Check xml issues count");
	}


	void checkRestApi() throws AutomationException, JSONException {
		Mockito.when(restExecuter.executeTask("rest/api/2/project/PR",null)).thenReturn(
				"{ issueTypes: [ { id: 3,name:'Task' }] }");

		Map<String,Long>issueTypes = jiraAutomationRest.getTaskIdByName("PR");
		log.info("Issue Types : ");
		issueTypes.forEach((k,v)->{log.info("  name:"+k+" id:"+v);});

		Assert.state(issueTypes.size()==1,"Check issueType size");
	}

	void checkJiraConnection() throws ExecutionException, InterruptedException, AutomationException {
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
