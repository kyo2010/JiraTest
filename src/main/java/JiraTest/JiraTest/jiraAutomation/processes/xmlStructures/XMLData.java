package JiraTest.JiraTest.jiraAutomation.processes.xmlStructures;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonRootName("data")
public class XMLData {
    private String projectKey;
    private List<XMLIssue> issues = new ArrayList();
    private List<XMLDescription> descriptions=new ArrayList();
}
