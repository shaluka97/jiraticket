// Main.java
import libs.*;

public class Main {
    public static void main(String[] args) {
        // Load configuration
        Config config = ConfigLoader.loadConfig("ticket.yaml");

        System.out.println("JIRA URL: " + config.getJira().getUrl());
        System.out.println("Project Key: " + config.getTicket().getProjectKey());
        System.out.println("Summary: " + config.getTicket().getSummary());

        // Create JIRA ticket
        JiraTicketCreator creator = new JiraTicketCreator(config);
        creator.createTicket();
    }
}
