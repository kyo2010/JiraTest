package JiraTest.JiraTest.jiraAutomation.clients.jiraModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssueType {
    private String id;
    private String name;
}
