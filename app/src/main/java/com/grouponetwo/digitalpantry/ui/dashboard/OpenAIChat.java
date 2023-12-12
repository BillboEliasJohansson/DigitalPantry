package com.grouponetwo.digitalpantry.ui.dashboard;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OpenAIChat {
    static String response2;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String TAG = "OpenAIChat";

    public static String makeOpenAIRequest(TextView textView, Context context, String apiKey, String base64Image) {
        // Create headers
        String authorizationHeader = "Bearer " + apiKey;

        // Build JSON payload
        JSONObject payload = buildJsonPayload(apiKey, base64Image);

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Make a POST request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, OPENAI_API_URL, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());

                        try {
                            // Extract content from the response
                            JSONArray choicesArray = response.getJSONArray("choices");
                            if (choicesArray.length() > 0) {
                                JSONObject firstChoice = choicesArray.getJSONObject(0);
                                JSONObject message = firstChoice.getJSONObject("message");
                                String content = message.getString("content");
                                textView.setText(content);
                            } else {
                                // Handle the case when no choices are found in the response
                                Log.e(TAG, "No choices found in the response.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.toString());
                        textView.setText("Timeout Error");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", authorizationHeader);
                return headers;
            }
        };

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
        return response2;
    }

    private static JSONObject buildJsonPayload(String apiKey, String base64Image) {
        try {
            // Build JSON payload
            JSONObject contentObject = new JSONObject()
                    .put("type", "text")
                    .put("text", "Bilden visar ett kvitto från en matbutik. Svara endast med en lista av varje produkt följt av antal på detta sätt :produkt:antal: skriv om produktnamnen till typen av produkt. Till exempel Gouda till Ost. Skånemejerier 1l lättmjölk till endast Mjölk");

            JSONObject imageUrlObject = new JSONObject()
                    .put("url", "data:image/jpeg;base64," + base64Image);

            JSONArray contentArray = new JSONArray()
                    .put(contentObject)
                    .put(new JSONObject()
                            .put("type", "image_url")
                            .put("image_url", imageUrlObject));

            JSONObject payload = new JSONObject()
                    .put("model", "gpt-4-vision-preview")
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", contentArray)))
                    .put("max_tokens", 300);

            return payload;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
