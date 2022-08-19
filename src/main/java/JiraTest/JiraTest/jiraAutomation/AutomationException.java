package JiraTest.JiraTest.jiraAutomation;

public class AutomationException extends Exception{
    private String description;

    public AutomationException(String message, Throwable cause, String description) {
        super(message, cause);
        this.description = description;
    }
    public AutomationException(String message, String description) {
        super(message, null);
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
