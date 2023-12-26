import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.stub.PredictionServiceStubSettings;
import com.google.cloud.vertexai.generativeai.preview.ContentMaker;
import com.google.cloud.vertexai.generativeai.preview.GenerativeModel;
import com.google.cloud.vertexai.generativeai.preview.PartMaker;
import com.google.cloud.vertexai.generativeai.preview.ResponseHandler;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MultimodalMultiImage {

    public static void main(String[] args) throws IOException {
        // TODO(developer): Replace these variables before running the sample.
        String projectId = "seto-goagent0";
        String location = "us-central1";
        String modelName = "gemini-pro-vision";
        String json = "seto-goagent0-4154086f0ca5.json";

        multimodalMultiImage(projectId, location, modelName, json);
    }

    // Generates content from multiple input images.
    public static void multimodalMultiImage(String projectId, String location, String modelName, String json)
            throws IOException {
        // Initialize client that will be used to send requests. This client only needs
        // to be created once, and can be reused for multiple requests.
//        List<String> defaultScopes =
//                PredictionServiceStubSettings.defaultCredentialsProviderBuilder().getScopesToApply();
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(json)).createScoped(defaultScopes);
//        try (VertexAI vertexAI = new VertexAI(projectId, location, credentials)) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);

            Content content = ContentMaker.fromMultiModalData(
                    PartMaker.fromMimeTypeAndData("image/png", readImageFile(
                            "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark1.png")),
                    "city: Rome, Landmark: the Colosseum",
                    PartMaker.fromMimeTypeAndData("image/png", readImageFile(
                            "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark2.png")),
                    "city: Beijing, Landmark: Forbidden City",
                    PartMaker.fromMimeTypeAndData("image/png", readImageFile(
                            "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark3.png"))
            );

            GenerateContentResponse response = model.generateContent(content);

            String output = ResponseHandler.getText(response);
            System.out.println(output);
        }
    }

    // Reads the image data from the given URL.
    public static byte[] readImageFile(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } else {
            throw new RuntimeException("Error fetching file: " + responseCode);
        }
    }
}