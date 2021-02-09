package br.com.mauker.materialsearchview.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mauker.materialsearchview.MaterialSearchView
import br.com.mauker.materialsearchview.MaterialSearchView.SearchViewListener
import br.com.mauker.materialsearchview.db.model.History

class MainActivity : AppCompatActivity() {
    private lateinit var searchView: MaterialSearchView
    private lateinit var btClearHistory: Button
    private lateinit var btClearSuggestions: Button
    private lateinit var btClearPins: Button
    private lateinit var btClearAll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchView = findViewById(R.id.search_view)
        btClearHistory = findViewById(R.id.bt_clearHistory)
        btClearSuggestions = findViewById(R.id.bt_clearSuggestions)
        btClearPins = findViewById(R.id.bt_clearPins)
        btClearAll = findViewById(R.id.bt_clearAll)

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchView.setSearchViewListener(object : SearchViewListener {
            override fun onSearchViewOpened() {
                // Do something once the view is open.
            }

            override fun onSearchViewClosed() {
                // Do something once the view is closed.
            }
        })

        val context: Context = this

        val clickListener = object: MaterialSearchView.OnHistoryItemClickListener {
            override fun onClick(history: History) {
                searchView.setQuery(history.query, false)
            }

            override fun onLongClick(history: History) {
                Toast.makeText(context, "Long clicked! Item: $history", Toast.LENGTH_SHORT).show()
            }
        }

        searchView.setOnItemClickListener(clickListener)

        searchView.setOnClearClickListener {
            Toast.makeText(this, "Clear clicked!", Toast.LENGTH_LONG).show()
        }
        btClearHistory.setOnClickListener { searchView.clearHistory() }
        btClearSuggestions.setOnClickListener { searchView.clearSuggestions() }
        btClearPins.setOnClickListener { searchView.clearPinned() }
        btClearAll.setOnClickListener { searchView.clearAll() }

        searchView.adjustTintAlpha(0.8f)
        // This will override the default audio action.
        // searchView.setOnVoiceClickedListener { Toast.makeText(context, "Voice clicked!", Toast.LENGTH_SHORT).show() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle toolbar item clicks here. It'll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
                // Open the search view on the menu item click.
                searchView.openSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (searchView.isOpen) {
            // Close the search on the back button press.
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (searchWrd.isNotBlank()) {
                    searchView.setQuery(searchWrd, false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        searchView.clearAll()
    }

    override fun onResume() {
        super.onResume()
        val arr = resources.getStringArray(R.array.suggestions)
        searchView.addSuggestions(arr)
        searchView.saveQueryToDb("Query")
        searchView.addPin("Pinned item test")
    }
}