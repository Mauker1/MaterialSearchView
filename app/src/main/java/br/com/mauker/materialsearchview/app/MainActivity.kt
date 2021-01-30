package br.com.mauker.materialsearchview.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mauker.materialsearchview.MaterialSearchView
import br.com.mauker.materialsearchview.MaterialSearchView.SearchViewListener

class MainActivity : AppCompatActivity() {
    private lateinit var searchView: MaterialSearchView
    private lateinit var btClearHistory: Button
    private lateinit var btClearSuggestions: Button
    private lateinit var btClearAll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchView = findViewById(R.id.search_view)
        btClearHistory = findViewById(R.id.bt_clearHistory)
        btClearSuggestions = findViewById(R.id.bt_clearSuggestions)
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
        searchView.setOnItemClickListener { _, _, position, _ -> // Do something when the suggestion list is clicked.
            val suggestion = searchView.getSuggestionAtPosition(position)
            searchView.setQuery(suggestion, false)
        }
        searchView.setOnClearClickListener {
            Toast.makeText(this, "Clear clicked!", Toast.LENGTH_LONG).show()
        }
        btClearHistory.setOnClickListener { clearHistory() }
        btClearSuggestions.setOnClickListener { clearSuggestions() }
        btClearAll.setOnClickListener { clearAll() }

        searchView.adjustTintAlpha(0.8f)
        val context: Context = this
        searchView.setOnItemLongClickListener { _, _, i, _ ->
            Toast.makeText(context, "Long clicked position: $i", Toast.LENGTH_SHORT).show()
            true
        }
        // This will override the default audio action.
        searchView.setOnVoiceClickedListener { Toast.makeText(context, "Voice clicked!", Toast.LENGTH_SHORT).show() }
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
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        searchView.clearSuggestions()
    }

    override fun onResume() {
        super.onResume()
        searchView.activityResumed()
        val arr = resources.getStringArray(R.array.suggestions)
        searchView.addSuggestions(arr)
    }

    private fun clearHistory() {
        searchView.clearHistory()
    }

    private fun clearSuggestions() {
        searchView.clearSuggestions()
    }

    private fun clearAll() {
        searchView.clearAll()
    }
}