package libs;

import java.util.List;

public class TicketConfig {
    private String projectKey;
    private String summary;
    private String description;
    private String issuetype;
    private String assignee;
    private List<String> labels;


    public TicketConfig() {
    }

    public String getProjectKey() {
        return projectKey;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getDescription() {
        return description;
    }

    public String getIssuetype() {
        return issuetype;
    }

    public String getSummary() {
        return summary;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIssuetype(String issuetype) {
        this.issuetype = issuetype;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}