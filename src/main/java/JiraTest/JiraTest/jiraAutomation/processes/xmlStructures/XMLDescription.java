package JiraTest.JiraTest.jiraAutomation.processes.xmlStructures;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("description")
public class XMLDescription {
    private String id;
    private String summary;
}
