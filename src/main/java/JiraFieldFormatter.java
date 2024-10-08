import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraFieldFormatter {

    public static String convertToJiraDescription(String filePath) {
        StringBuilder jiraDescription = new StringBuilder();

        try {
            // Read all lines from the file
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Convert formatting
            content = convertBoldText(content);
            content = convertItalicText(content);
            content = convertHeadings(content);
            content = convertBlockquotes(content);
            content = convertCodeBlocks(content);
            content = convertInlineCode(content);
            content = convertHorizontalRules(content);
            content = convertLists(content);
            content = convertParagraphs(content);

            jiraDescription.append(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jiraDescription.toString();
    }

    // Convert bold text
    private static String convertBoldText(String content) {
        return content.replaceAll("\\*\\*(.*?)\\*\\*", "*$1*");
    }

    // Convert italic text
    private static String convertItalicText(String content) {
        return content.replaceAll("_(.*?)_", "_$1_");
    }

    // Convert headings (supports # for headings in input text)
    private static String convertHeadings(String content) {
        content = content.replaceAll("(?m)^#{3} (.+)", "h3. $1");
        content = content.replaceAll("(?m)^#{2} (.+)", "h2. $1");
        content = content.replaceAll("(?m)^# (.+)", "h1. $1");
        return content;
    }

    // Convert blockquotes
    private static String convertBlockquotes(String content) {
        return content.replaceAll("(?m)^> (.+)", "bq. $1");
    }

    // Convert code blocks (wrap ```code``` or ``` in {code} blocks for Jira)
    private static String convertCodeBlocks(String content) {
        // Replace triple backticks with {code} blocks for multiline code
        content = content.replaceAll("(?s)```\\s*(.*?)\\s*```", "{code}$1{code}");
        return content;
    }

    // Convert inline code (wrap `code` in {{code}} for Jira)
    private static String convertInlineCode(String content) {
        return content.replaceAll("`(.*?)`", "{{$1}}");
    }

    // Convert horizontal rules (--- or *** to ---- for Jira)
    private static String convertHorizontalRules(String content) {
        return content.replaceAll("(?m)^[-*]{3,}$", "----");
    }

    // Convert unordered and ordered lists
    private static String convertLists(String content) {
        // Unordered lists (- or *) to Jira format
        content = content.replaceAll("(?m)^[-*] (.*)", "- $1");

        // Ordered lists (1. 2. 3. etc.)
        Pattern orderedListPattern = Pattern.compile("(?m)^\\d+\\. (.*)");
        Matcher matcher = orderedListPattern.matcher(content);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "1. " + matcher.group(1));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    // Convert paragraphs by ensuring double newlines
    private static String convertParagraphs(String content) {
        return content.replaceAll("(?m)^(.+?)$(\\n(?!\\n))", "$1\n\n");
    }

    // Main method for testing
    public static void main(String[] args) {
        String filePath = "src/main/resources/bug_description.txt"; // Replace with your file path
        String jiraDescription = convertToJiraDescription(filePath);

        if (jiraDescription != null) {
            System.out.println("Converted Jira Description:");
            System.out.println(jiraDescription);
        } else {
            System.out.println("Failed to convert file to Jira description.");
        }
    }
}
