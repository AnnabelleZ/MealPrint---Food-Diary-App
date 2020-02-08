package com.manduannabelle.www.fooddiary;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class FeedbackActivity extends UtilityActivity {
    private EditText editTextTo;
    private EditText editTextSubject;
    private EditText editTextMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        setupUI(findViewById(R.id.feedback_form));

        editTextTo = findViewById(R.id.edit_text_to);
        editTextSubject = findViewById(R.id.edit_text_subject);
        editTextMessage = findViewById(R.id.edit_text_message);

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener((View v) -> sendMail());

        ImageButton back = findViewById(R.id.feedback_back);
        back.setOnClickListener((View v) ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }

    private void sendMail() {
        String recipientList = editTextTo.getText().toString();
        String[] recipients = recipientList.split(",");

        String subject = editTextSubject.getText().toString();
        String message = editTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}
