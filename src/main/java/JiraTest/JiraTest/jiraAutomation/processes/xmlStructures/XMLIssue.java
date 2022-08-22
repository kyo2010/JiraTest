package JiraTest.JiraTest.jiraAutomation.processes.xmlStructures;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("issue")
public class XMLIssue {
    private String id;
    private String type;
    private String priority;

    private XMLDescription description = null;
}
