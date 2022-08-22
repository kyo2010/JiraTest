package JiraTest.JiraTest.Configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class JiraConfig {

    @Value("${jira.user}")
    @Getter
    private String jiraUser = "";

    @Value("${jira.token}")
    @Getter
    private String jiraToken = "";

    @Value("${jira.host}")
    @Getter
    private String jiraHost = "";

}
