package com.example.android.news24;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.news24.News;
import com.example.android.news24.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * This is the custom constructor. The content is used to inflate the layout file,
     * and the list is the date we want to populate into the lists.
     *
     * @param context is the current context used to inflate the layout file.
     * @param news is a list of news article objects to display in a list.
     */

    public NewsAdapter(Context context, List<News> news) {
        // Initialize Array Adapter storage
        super(context, 0, news);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get news object
        News currentNewsArticle = getItem(position);

        // Find ID title text view
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        // Set text
        titleView.setText(currentNewsArticle.getTitle());

        // Find ID section textview
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        // Set text
        sectionView.setText(currentNewsArticle.getSection());

        // Find date textview
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        if (currentNewsArticle.hasDate()) {
            // If presence detected, then display
            dateView.setText(currentNewsArticle.getDate());

            // Set to visible
            dateView.setVisibility(View.VISIBLE);
        } else {
            // Set to invisible
            dateView.setVisibility(View.GONE);
        }

        // Find author text view
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        // Check author presence w/boolean
        if (currentNewsArticle.hasAuthor()) {
            // Display
            authorView.setText(currentNewsArticle.getAuthor());
            // Make visible
            authorView.setVisibility(View.VISIBLE);
        } else {
            // Else, set to invisible
            authorView.setVisibility(View.GONE);
        }

        return listItemView;
    }

}