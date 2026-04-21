package FirstBlock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitHubParser {
    public static String getGitHubRepoInfo() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/users/EtherealPatience/repos"))
                .GET()
                .header("User-Agent", "MyJavaApp")
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseGitHubInfo() {

        try {
            String response = getGitHubRepoInfo().substring(1, getGitHubRepoInfo().length() - 1);
            String[] repos = response.split(Pattern.quote("},{"));

            List<RepositoryInfo> list = Arrays.stream(repos)
                    .map(s -> new RepositoryInfo(
                            extractValue(s, "name"),
                            extractValue(s, "description"),
                            extractInt(s, "stargazers_count"))
                    )
                    .collect(Collectors.toList());

            System.out.println(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractValue(String s, String key) {
        String searchPattern = "\"" + key + "\":";
        int startIndex = s.indexOf(searchPattern) + searchPattern.length() + 1;
        int endIndex;
        if (s.startsWith("null", startIndex - 1))
            return "null";
        else {
            endIndex = s.indexOf("\",", startIndex);
            return s.substring(startIndex, endIndex);
        }
    }

    public static int extractInt(String s, String key) {
        String searchPattern = "\"" + key + "\":";
        int startIndex = s.indexOf(searchPattern) + searchPattern.length();
        int endIndex = s.indexOf(",", startIndex);
        return Integer.parseInt(s.substring(startIndex, endIndex));
    }

}
