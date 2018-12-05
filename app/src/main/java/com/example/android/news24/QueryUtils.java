package com.example.android.news24;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.news24.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /** Tag for the log messages */
    private static final String TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian API dataset and return a list of {@link News} article objects
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create object for URL
        URL url = createUrl(requestUrl);
        Log.i(TAG, "fetchNewsData: News Data Is Fetched!!!!!!");

        // Http Request
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "fetchNewsData: Problem making the HTTP request.", e);
        }

        // Find fields in JSON
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return object
        return news;
    }

    /**
     * Returns new URL object from the given string URL. --used from QuakeReport App
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * -- used from Quake Report App
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     * -- used from Quake Report App
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that have been built up from parsing
     * the given JSON response. -- used some code from Quake Report App
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // create an empty ArrayList to add news articles to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create JSONObject from response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONObject key "response"
            JSONObject jsonResponse = baseJsonResponse.getJSONObject("response");

            // Extract Array
            JSONArray newsArticleArray = jsonResponse.getJSONArray("results");

            // Create objects for each article
            for (int i = 0; i < newsArticleArray.length(); i++) {

                // Get particular news article
                JSONObject currentNewsArticle = newsArticleArray.getJSONObject(i);

                // Extract "sectionName" value
                String section = currentNewsArticle.getString("sectionName");

                // Extract web title value
                String title = currentNewsArticle.getString("webTitle");

                // Extract "webURL" value
                String url = currentNewsArticle.getString("webUrl");

                // Extract "webPublicationDate" value
                String webPublicationDate = currentNewsArticle.getString("webPublicationDate");
                // Split the date
                String[] webDate;
                webDate = webPublicationDate.split("T");
                String date = webDate[0];

                // Extract "fields" value
                JSONObject fields = currentNewsArticle.getJSONObject("fields");
                // Get value from JSONObject fields key called "byline;
                String author = fields.optString("byline");


                // Create new object with the JSON response
                News newsArticles = new News(title, date, section, author, url);

                // Add to the list of news articles
                news.add(newsArticles);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of news articles
        return news;
    }

}