package com.example.henriqueribeirodealmeida.observador;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class QueryUtils {

    public final static String URL = "https://www.observador.soulcodejr.com/php/upload.php";
    private final static String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {}

    public static String postImage(Bitmap image){
        String response = "";

        Bitmap imageSmall = Bitmap.createScaledBitmap(image, (int) (image.getWidth() * 0.3), (int) (image.getHeight() * 0.3), false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageSmall.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String request = "encoded_image=data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.e(LOG_TAG, request);
        // Create URL object
        URL url = createUrl(URL);

        // Describe the request property which will be used in the HttpURLConnection
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Accept","text/plain");

        // Perform HTTP request to the URL and send a JSON string to be posted
        try {
            response = makeHttpPostRequest(url, request, header);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        return response;
    }

    private static String makeHttpPostRequest(java.net.URL url, String request,
                                              HashMap<String, String> header) throws IOException {

        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(30000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");

            if (!header.isEmpty()) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            urlConnection.setDoOutput(true);

            //Send request
            PrintStream printStream = new PrintStream(urlConnection.getOutputStream());
            printStream.println(request);

            String code = String.valueOf(urlConnection.getResponseCode());
            Log.e(LOG_TAG, code);

            //Get response
            response = readFromStream(urlConnection.getInputStream());

            urlConnection.connect();

        } catch (IOException e) {
            return response;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    @NonNull
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

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("NETWORKING", "Error with creating URL ", e);
        }
        return url;
    }
}
