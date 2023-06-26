import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class RestApi {


    public static void main(String[] args) throws Exception{

        Gson gson = new Gson();
        Transcript transcript = new Transcript();

        transcript.setAudio_url(Constants.AUDIO_URL);

        String jsonRequest = gson.toJson(transcript);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(Constants.POST_URL))
                .header("Authorization", Constants.API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();


        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        transcript = gson.fromJson(postResponse.body(), Transcript.class);

        if("queued".equals(transcript.getStatus())){
            System.out.println("Request is queued");
        }

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(Constants.POST_URL+"/"+transcript.getId()))
                .header("Authorization", Constants.API_KEY)
                .GET()
                .build();

        //ignore this
        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println("Request status: "+transcript.getStatus());
            if ("completed".equals(transcript.getStatus())){
                System.out.println("Transcription result:");
                System.out.println(transcript.getText());
                break;
            }else if("error".equals(transcript.getStatus())){
                System.out.println("Unexpected error");
                break;
            }
            Thread.sleep(1000);
        }



    }


}
