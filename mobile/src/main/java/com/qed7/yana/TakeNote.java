package com.qed7.yana;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.actions.NoteIntents;

import java.util.Random;

public class TakeNote extends AppCompatActivity {
    private static final Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_note);

        Intent intent = getIntent();

        String subtext = intent.getStringExtra(Intent.EXTRA_TEXT);

        notify("YANA", subtext);

        EditText text = (EditText) findViewById(R.id.noteField);
        text.setText(subtext);

        NoteDbHelper.NoteDbWrapper ndb = new NoteDbHelper(getApplicationContext()).getDb();
        ndb.newUNote().updateText(subtext);
    }

    private void notify(String subject, String text){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(subject)
                        .setContentText(text);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(rnd.nextInt(), mBuilder.build());
    }
}
