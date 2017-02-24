package makers.ar_d.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_MAPS = 11;
    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main, "You have been sign out..", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }if (item.getItemId() == R.id.menu_share_location) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivityForResult(intent, REQUEST_MAPS);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Successfully signed in, Selamat datang..", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
        }else if (requestCode == REQUEST_MAPS) {
            EditText input = (EditText) findViewById(R.id.input);
            double latitude = data.getDoubleExtra("lat", 0);
            double longitude = data.getDoubleExtra("long", 0);
            input.setText(latitude + ", " + longitude);
        } else {
            Snackbar.make(activity_main, "We couldn't sign you. Coba lagi..", Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
                input.requestFocus();
            }
        });

        //Check if not sign-in then navigate Signin page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(activity_main, "Selamat datang " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //Load content
            displayChatMessage();
        }

    }

    private void displayChatMessage() {

        ListView listOfMessage = (ListView) findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                CircleImageView messageImage;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageImage = (CircleImageView) v.findViewById(R.id.messenger_image);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (h:mm:ss)", model.getMessageTime()));
                if (model.getPhotoUrl() == null) {
                    messageImage
                            .setImageDrawable(ContextCompat
                                    .getDrawable(MainActivity.this,
                                            R.drawable.ic_action_image));
                } else {
                    Glide.with(MainActivity.this)
                            .load(model.getPhotoUrl())
                            .into(messageImage);
                }

            }
        };
        listOfMessage.setAdapter(adapter);
    }
}

