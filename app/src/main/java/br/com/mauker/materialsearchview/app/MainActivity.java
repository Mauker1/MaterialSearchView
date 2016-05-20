package br.com.mauker.materialsearchview.app;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.mauker.EmailBinder;
import br.com.mauker.ItemEmail;
import br.com.mauker.ItemName;
import br.com.mauker.NameBinder;
import br.com.mauker.materialsearchview.MaterialSearchView;
import io.c0nnector.github.least.LeastAdapter;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView searchView;

    private Button bt_clearHistory;
    private Button bt_clearSuggestions;
    private Button bt_clearAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        bt_clearHistory = (Button) findViewById(R.id.bt_clearHistory);
        bt_clearSuggestions = (Button) findViewById(R.id.bt_clearSuggestions);
        bt_clearAll = (Button) findViewById(R.id.bt_clearAll);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.suggestion_list);
        setupLeastView(recyclerView);

        searchView.setShouldKeepHistory(false);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {
                // Do something once the view is open.
            }

            @Override
            public void onSearchViewClosed() {
                // Do something once the view is closed.
            }
        });

        bt_clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

        bt_clearSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSuggestions();
            }
        });

        bt_clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

//        searchView.setTintAlpha(200);
        searchView.adjustTintAlpha(0.8f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here. It'll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                // Open the search view on the menu item click.

                searchView.openSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isOpen()) {
            // Close the search on the back button press.
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        searchView.clearSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //searchView.activityResumed();
        String[] arr = getResources().getStringArray(R.array.suggestions);

        searchView.addSuggestions(arr);
    }

    private void clearHistory() {
        searchView.clearHistory();
    }

    private void clearSuggestions() {
        searchView.clearSuggestions();
    }

    private void clearAll() {
        searchView.clearAll();
    }


    private void setupLeastView(RecyclerView recyclerView){

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        LeastAdapter adapter = new LeastAdapter.Builder()
                .binder(NameBinder.instance(this))
                .binder(EmailBinder.instance(this))
                .items(getItems())
                .build(this);

        recyclerView.setAdapter(adapter);
    }

    private List<Object> getItems(){
        List<Object> myList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            if (i%2 == 0) {
                myList.add(new ItemName("Name - " + i));
            }
            else myList.add(new ItemEmail("Email@Email.com - " + i));
        }

        return myList;
    }
}
