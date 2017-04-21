package nl.simyo.goodbyejeroenandroid;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by giuseppederaco on 04/02/2017.
 */

class WordsAdapter extends ArrayAdapter<Word> {

    WordsAdapter(Context context, ArrayList<Word> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Word word = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.word, parent, false);
        }

        final TextView nameTextView = (TextView) convertView.findViewById(R.id.word_name);
        final TextView counterTextView = (TextView) convertView.findViewById(R.id.word_counter);
        final View button = convertView.findViewById(R.id.word_incremeneter);

        nameTextView.setText(word.getName());
        counterTextView.setText("" + word.getCount());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WordIncreaserListener) getContext()).increaseWord(word);
            }
        });

        return convertView;
    }
}
