package libs;

public class Config {
    private JiraConfig jira;
    private TicketConfig ticket;

    public Config() {
    }

    public JiraConfig getJira() {
        return jira;
    }

    public TicketConfig getTicket(){
        return ticket;
    }

    public void setJira(JiraConfig jira) {
        this.jira = jira;
    }

    public void setTicket(TicketConfig ticket) {
        this.ticket = ticket;
    }
}