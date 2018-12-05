package com.example.android.news24;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.news24.News;
import com.example.android.news24.NewsAdapter;
import com.example.android.news24.NewsLoader;
import com.example.android.news24.R;
import com.example.android.news24.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Requesting from Guardian Website
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

    // My API key
    private static final String MY_API_KEY = "997dac08-e268-4072-9928-d4b8febaa613";

    // The Adapter for the list
    private NewsAdapter mAdapter;

    // Constant value
    private static final int NEWS_LOADER_ID = 1;

    // Log TAG
    private static final String TAG = MainActivity.class.getName();

    // Default view for when there is no data
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find listview
        ListView newsListView = (ListView) findViewById(R.id.list);

        // Set the view for when there is no data
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Adapter for list
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter for the list view
        newsListView.setAdapter(mAdapter);

        // obtain a reference to the SharedPreference file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // and register to be notified of preference changes
        // so we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set the Click Listener
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Retrieve the news article clicked on
                News currentNewsArticle = mAdapter.getItem(i);

                // Convert URL to URI object
                Uri newsUri = Uri.parse(currentNewsArticle.getUrl());

                // Intent for URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Intent for new activity
                startActivity(websiteIntent);
            }
        });

        // Check network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get data network details
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager for it to interact with the loaders.
            android.app.LoaderManager loaderManager = getLoaderManager();
            Log.i(TAG, "onCreate: Hey this is from LoaderManager!!!!");

            // Initialize the loader and pass in the constant
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            Log.i(TAG, "onCreate: This one is initLoader!!");
        } else {
            // Else, display error
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Set error connection message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_editions_key)) ||
                key.equals(getString(R.string.settings_order_by_key))) {

            // Clear listview
            mAdapter.clear();

            // Hide text view when loading indicator operates
            mEmptyStateTextView.setVisibility(View.GONE);

            // Simultaneous tasks/threads show loading indicator while fetching data
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart loader
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get String value from the preferences.
        String editions = sharedPrefs.getString(
                getString(R.string.settings_editions_key),
                getString(R.string.settings_editions_default)
        );

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // Uri parse and build the URL
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Add parameters
        uriBuilder.appendQueryParameter("editions", editions);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", "10");
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("api-key", MY_API_KEY);
        // get full url in log 'https://content.guardianapis.com/search?q=editions?q=us&order-by=newest&page-size=10&show-fields=byline'
        Log.i(TAG, uriBuilder.toString());

        // create a new loader for the given URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // hide loading indicator because data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        Log.i(TAG, "onLoadFinished: The load has finished!!!!!!!!");

        // Set no news articles found when applicable
        mEmptyStateTextView.setText(R.string.no_news_articles);

        // Add objects to list view
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Reset loader to clear data
        mAdapter.clear();
        Log.i(TAG, "onLoaderReset: The loader has reset!!!!!!!!!!!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate options menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}