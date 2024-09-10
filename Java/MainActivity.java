package com.example.chatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Declare UI elements and required variables
    private RecyclerView chats; // RecyclerView to display chat messages
    private EditText usermsg; // EditText for user input
    private FloatingActionButton sendmsg; // FloatingActionButton to send the message
    private final String BOT_KEY = "bot"; // Key to identify bot messages
    private final String USER_KEY = "user"; // Key to identify user messages
    private ArrayList<ChatsModel> chatsArrayList; // ArrayList to store chat messages
    private Adapter adapter; // Adapter for the RecyclerView to display messages

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge display for a modern look
        EdgeToEdge.enable(this);

        // Set the content view to the activity's layout
        setContentView(R.layout.activity_main);

        // Apply window insets to handle system bars (e.g., status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the UI elements by finding their IDs in the layout
        chats = findViewById(R.id.chats); // RecyclerView for chats
        usermsg = findViewById(R.id.enter); // EditText for user input
        sendmsg = findViewById(R.id.send); // Button to send the message

        // Initialize the ArrayList to hold the chat messages
        chatsArrayList = new ArrayList<>();

        // Set up the RecyclerView with the Adapter and LayoutManager
        adapter = new Adapter(this, chatsArrayList); // Initialize the adapter with the chat list
        LinearLayoutManager manager = new LinearLayoutManager(this); // Set vertical orientation for RecyclerView
        chats.setLayoutManager(manager); // Apply the LayoutManager to RecyclerView
        chats.setAdapter(adapter); // Set the adapter to the RecyclerView

        // Set an OnClickListener on the send button
        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if user input is empty, show a Toast message if it is
                if (usermsg.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter your Message", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution if no input
                }

                // Get response from the bot after sending user message
                getResponse(usermsg.getText().toString());

                // Clear the EditText after the message is sent
                usermsg.setText("");
            }
        });
    }

    // Method to get response from bot based on the user's message
    private void getResponse(String message) {
        // Add the user's message to the chat list and notify the adapter
        chatsArrayList.add(new ChatsModel(message, USER_KEY));
        adapter.notifyDataSetChanged();

        // Construct the URL for the Brainshop AI API request
        String url = "http://api.brainshop.ai/get?bid=183185&key=MJQJiQPgdxkx3tMy&uid=[uid]&msg=" + message;

        // Base URL of the API
        String BASE_URL = "http://api.brainshop.ai";

        // Initialize Retrofit for network calls
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Set the base URL
                .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON parsing
                .build();

        // Create an instance of the Retrofit API interface
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

        // Make a call to the API using the generated URL
        Call<MsgModel> call = retrofitApi.getMessage(url);

        // Enqueue the call asynchronously to handle the response or failure
        call.enqueue(new Callback<MsgModel>() { // Specify the type for Callback
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    MsgModel model = response.body(); // Get the response body
                    // Add the bot's message to the chat list and notify the adapter
                    chatsArrayList.add(new ChatsModel(model.getCnt(), BOT_KEY));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
                // In case of failure, show a fallback message from the bot
                chatsArrayList.add(new ChatsModel("Please revert your message", BOT_KEY));
                Log.e("API_ERROR", "Error: " + t.getMessage());
                adapter.notifyDataSetChanged();
            }
        });
    }
}