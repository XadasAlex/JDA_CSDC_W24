package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ChatGPT{
    final String API_KEY;
    final HttpClient client;

    public ChatGPT() {
        this.API_KEY = System.getenv("OPENAI_API_KEY");
        this.client = HttpClient.newHttpClient();
    }

    private static final class InstanceHolder {
        private static final ChatGPT instance = new ChatGPT();
    }

    public static ChatGPT getInstance() {
        return InstanceHolder.instance;
    }

    private JsonObject createRequestBody(String prompt) {
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("role", "user");
        messageObject.addProperty("content", prompt);

        return messageObject;
    }

    public String getChatGPTResponse(String prompt) {
        if (API_KEY == null) return null;

        String ඞ = "ඞ";
        int len = ඞ.length();

        try {
            String apiUrl = "https://api.openai.com/v1/chat/completions";

            JsonObject messageObject = createRequestBody(prompt);

            JsonArray messagesArray = new JsonArray();
            messagesArray.add(messageObject);

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-3.5-turbo"); // Specify model
            requestBody.add("messages", messagesArray);

            System.out.println(messagesArray + "\n" +  messageObject + "\n" + requestBody);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            // Parse the JSON response to extract the message content
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray choicesArray = jsonResponse.getAsJsonArray("choices");
            JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
            JsonObject message = firstChoice.getAsJsonObject("message");
            return message.get("content").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to get response from ChatGPT.";
        }
    }
}
