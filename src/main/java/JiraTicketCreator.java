// JiraTicketCreator.java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import libs.*;

public class JiraTicketCreator {
    private static final Logger logger = LoggerFactory.getLogger(JiraTicketCreator.class);
    private final Config config;
    private final ObjectMapper objectMapper;

    // ANSI escape codes for coloring console output
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public JiraTicketCreator(Config config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    public void createTicket() {
        String jiraUrl = config.getJira().getUrl();
        String apiEndpoint = jiraUrl + "/rest/api/3/issue";

        // Build the JSON payload
        TicketPayload payload = buildPayload();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiEndpoint);

            // Set headers
            httpPost.setHeader("Content-Type", "application/json");
            String auth = config.getJira().getUsername() + ":" + config.getJira().getApiToken();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            httpPost.setHeader("Authorization", "Basic " + encodedAuth);

            // Convert payload to JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            logger.debug("Sending payload: {}", jsonPayload); // Debug log for payload

            // Execute the request
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    // Read the response body
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBody.append(line);
                    }

                    // Parse the JSON response to extract the ticket key
                    JsonNode rootNode = objectMapper.readTree(responseBody.toString());
                    String ticketKey = rootNode.path("key").asText();

                    // Log the success message with ticket ID in red
                    logger.info("Ticket created successfully. ID: " + ANSI_RED + ticketKey + ANSI_RESET);
                } else {
                    // Read the response body for error details
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                    StringBuilder errorBody = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorBody.append(line);
                    }

                    logger.error("Failed to create ticket. Status Code: {}", statusCode);
                    logger.error("Error Response: {}", errorBody.toString());
                }
            }

        } catch (Exception e) {
            logger.error("Exception occurred while creating JIRA ticket", e);
        }
    }

    private TicketPayload buildPayload() {
        TicketConfig tc = config.getTicket();
        TicketPayload payload = new TicketPayload();
        payload.setFields(new Fields(
                tc.getProjectKey(),
                tc.getIssuetype(),
                tc.getSummary(),
                tc.getDescription(), // Now a String
                tc.getAssignee(),
                tc.getLabels()
        ));
        return payload;
    }

    // Static inner classes to represent the payload structure
    public static class TicketPayload {
        private Fields fields;

        public TicketPayload() {
        }

        public Fields getFields() {
            return fields;
        }

        public void setFields(Fields fields) {
            this.fields = fields;
        }
    }

    public static class Fields {
        private Project project;
        private IssueType issuetype;
        private String summary;
        private ADFDescription description; // Changed from String to ADFDescription
        private Assignee assignee;
        private List<String> labels;

        public Fields() {
        }

        public Fields(String projectKey, String issueTypeName, String summary, String description, String assigneeName, List<String> labelsList) {
            this.project = new Project(projectKey);
            this.issuetype = new IssueType(issueTypeName);
            this.summary = summary;
            this.description = new ADFDescription(description); // Convert String to ADFDescription
            if (assigneeName != null && !assigneeName.isEmpty()) {
                this.assignee = new Assignee(assigneeName);
            }
            if (labelsList != null && !labelsList.isEmpty()) {
                this.labels = labelsList;
            }
        }

        // Getters and Setters
        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        public IssueType getIssuetype() {
            return issuetype;
        }

        public void setIssuetype(IssueType issuetype) {
            this.issuetype = issuetype;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public ADFDescription getDescription() {
            return description;
        }

        public void setDescription(ADFDescription description) {
            this.description = description;
        }

        public Assignee getAssignee() {
            return assignee;
        }

        public void setAssignee(Assignee assignee) {
            this.assignee = assignee;
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }
    }

    public static class Project {
        private String key;

        public Project() {
        }

        public Project(String key) {
            this.key = key;
        }

        // Getters and Setters
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class IssueType {
        private String name;

        public IssueType() {
        }

        public IssueType(String name) {
            this.name = name;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Assignee {
        private String name;

        public Assignee() {
        }

        public Assignee(String name) {
            this.name = name;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Define ADFDescription as a separate static class or in its own file
    public static class ADFDescription {
        private String type;
        private int version;
        private List<ADFContent> content;

        public ADFDescription() {
        }

        public ADFDescription(String text) {
            this.type = "doc";
            this.version = 1;
            this.content = List.of(new ADFContent(text));
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<ADFContent> getContent() {
            return content;
        }

        public void setContent(List<ADFContent> content) {
            this.content = content;
        }

        // Inner classes
        public static class ADFContent {
            private String type;
            private List<ADFText> content;

            public ADFContent() {
            }

            public ADFContent(String text) {
                this.type = "paragraph";
                this.content = List.of(new ADFText(text));
            }

            // Getters and Setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<ADFText> getContent() {
                return content;
            }

            public void setContent(List<ADFText> content) {
                this.content = content;
            }
        }

        public static class ADFText {
            private String type;
            private String text;

            public ADFText() {
            }

            public ADFText(String text) {
                this.type = "text";
                this.text = text;
            }

            // Getters and Setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }
}
