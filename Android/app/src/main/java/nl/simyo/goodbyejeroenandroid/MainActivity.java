package nl.simyo.goodbyejeroenandroid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements WordIncreaserListener {

    ArrayList<Word> words;
    WordsAdapter wordsAdapter;
    ListViewCompat listview;
    private DatabaseReference databaseReference;
    KProgressHUD loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Jeroen’s Bullshit Bingo");
        setSupportActionBar(toolbar);

        words = new ArrayList<Word>();

        //
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        wordsAdapter = new WordsAdapter(this, words);
        listview = (ListViewCompat) findViewById(R.id.listview);
        listview.setAdapter(wordsAdapter);

        loader = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        UpdateManager.register(this);
        CrashManager.register(this);
    }

    private void getData() {
        showLoader();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!loader.isShowing())
                    showLoader();
                words.clear();
                for (DataSnapshot element : dataSnapshot.getChildren()) {
                    System.out.print(element + " ");
                    words.add(new Word(element.getKey(), ((Long) element.getValue()).intValue()));
                }

                Collections.sort(words, new Comparator<Word>() {
                    @Override
                    public int compare(Word w1, Word w2) {
                        final Integer w1Count = w1.getCount();
                        final Integer w2Count = w2.getCount();
                        if (w1Count.equals(w2Count)) return 0;
                        if (w1Count < w2Count) return 1;
                        return -1;
                    }
                });

                wordsAdapter.notifyDataSetChanged();
                hideLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loader.dismiss();

            }
        };

        databaseReference.addValueEventListener(postListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new MaterialDialog.Builder(this)
                    .title("Voeg een woord toe")
                    .positiveText("Toevoegen")
                    .negativeText("Annuleer")
                    .content("Heeft Jeroen weer een mooi woord gebruikt? Zet 'm op de lijst!")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("Word", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            addWord(input);
                        }
                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addWord(CharSequence input) {
        final String inputString = input.toString();

        if (inputString.length() > 0) {
            databaseReference.child(inputString).setValue(1);

            Word foundWord = null;

            for (Word w : words) {
                if (w.getName().equals(inputString)) {
                    foundWord = w;
                    break;
                }
            }

            if (foundWord != null) {
                databaseReference.child(inputString).setValue(foundWord.getCount() + 1);
            } else {
                databaseReference.child(inputString).setValue(1);
            }
        }
    }

    @Override
    public void increaseWord(Word word) {
        showLoader();
        databaseReference.child(word.getName()).setValue(word.getCount() + 1);
    }

    private void hideLoader() {
        loader.dismiss();
    }

    private void showLoader() {
        loader.show();
    }
}
