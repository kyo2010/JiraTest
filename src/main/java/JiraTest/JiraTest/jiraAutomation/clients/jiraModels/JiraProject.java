package JiraTest.JiraTest.jiraAutomation.clients.jiraModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraProject {
    List<JiraIssueType> issueTypes = new ArrayList();;
}
