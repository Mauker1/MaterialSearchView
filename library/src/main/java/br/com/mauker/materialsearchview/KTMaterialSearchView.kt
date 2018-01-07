@file:Suppress("unused")

package br.com.mauker.materialsearchview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.speech.RecognizerIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import br.com.mauker.materialsearchview.adapters.CursorSearchAdapter
import br.com.mauker.materialsearchview.db.HistoryContract
import br.com.mauker.materialsearchview.listeners.OnQueryTextListener
import br.com.mauker.materialsearchview.listeners.OnVoiceClickedListener
import br.com.mauker.materialsearchview.listeners.SearchViewListener
import br.com.mauker.materialsearchview.utils.AnimationUtils
import java.util.*

@Suppress("MemberVisibilityCanPrivate")
/**
 * Created by Mauker and Adam McNeilly on 30/03/2016. dd/MM/YY.
 * Maintained by Mauker, Adam McNeilly and our beautiful open source community <3
 * Based on stadiko on 6/8/15. https://github.com/krishnakapil/MaterialSeachView
 */
class KTMaterialSearchView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttributes: Int = 0) : FrameLayout(context, attributeSet, defStyleAttributes) {
    //region PROPERTIES
    /**
     * Whether or not the search view is open right now.
     */
    var open: Boolean = false
        private set

    /**
     * Whether or not the MaterialSearchView will animate into view or just appear.
     */
    var shouldAnimate: Boolean = false

    /**
     * Whether or not the MaterialSearchView will clonse under a click on the Tint View (Blank Area).
     */
    var shouldCloseOnTintClick: Boolean = false

    /**
     * Wheter to keep the search history or not.
     */
    var shouldKeepHistory: Boolean = false

    /**
     * Flag for whether or not we are clearing focus.
     */
    private var clearingFocus: Boolean = false

    /**
     * Voice hint prompt text.
     */
    private var hintPrompt: String = ""
    //endregion

    //region UI ELEMENTS
    /**
     * The tint that appears over the search view.
     */
    private var tintView: View? = null

    /**
     * The root of the search view.
     */
    private var root: FrameLayout? = null

    /**
     * The bar at the top of the SearchView containing the EditText and ImageButtons.
     */
    private var searchBar: LinearLayout? = null

    /**
     * The EditText for entering a search.
     */
    private var searchEditText: EditText? = null

    /**
     * The ImageButton for navigating back.
     */
    private var back: ImageButton? = null

    /**
     * The ImageButton for initiating a voice search.
     */
    private var voice: ImageButton? = null

    /**
     * The ImageButton for clearing the search text.
     */
    private var clear: ImageButton? = null

    /**
     * The ListView for displaying suggestions based on the search.
     */
    private var suggestionsListView: ListView? = null

    /**
     * Adapter for displaying suggestions.
     */
    var adapter: CursorAdapter? = null
        private set
    //endregion

    //region QUERY PROPERTIES
    /**
     * The previous query text.
     */
    private var oldQuery: CharSequence = ""

    /**
     * The current query text.
     */
    private var currentQuery: CharSequence = ""

    /**
     * Number of suggestions to show.
     */
    var maxHistory = BuildConfig.MAX_HISTORY
    //endregion

    //region LISTENERS
    /**
     * Listener for when the query text is submitted or changed.
     */
    var onQueryTextListener: OnQueryTextListener? = null

    /**
     * Listener for when the search view opens and closes.
     */
    var searchViewListener: SearchViewListener? = null

    /**
     * Listener for interaction with the voice button.
     */
    var onVoiceClickedListener: OnVoiceClickedListener? = null
    //endregion

    //region CONSTRUCTORS
    init {
        this.shouldAnimate = true
        this.shouldKeepHistory = true

        // Initialize view
        init()

        // Initialize style
        initStyle(attributeSet, defStyleAttributes)
    }

    /**
     * Preforms any required initializations for the search view.
     */
    private fun init() {
        // Inflate view
        LayoutInflater.from(context).inflate(R.layout.search_view, this, true)

        // Get items
        root = findViewById<View>(R.id.search_layout) as FrameLayout
        tintView = root?.findViewById(R.id.transparent_view)
        searchBar = root?.findViewById(R.id.search_bar) as LinearLayout
        back = root?.findViewById(R.id.action_back) as ImageButton
        searchEditText = root?.findViewById(R.id.et_search) as EditText
        voice = root?.findViewById(R.id.action_voice) as ImageButton
        clear = root?.findViewById(R.id.action_clear) as ImageButton
        suggestionsListView = root?.findViewById(R.id.suggestion_list) as ListView

        // Set click listeners
        back?.setOnClickListener { closeSearch() }
        voice?.setOnClickListener { onVoiceClicked() }
        clear?.setOnClickListener { searchEditText?.setText("") }

        tintView?.setOnClickListener {
            if (shouldCloseOnTintClick) {
                closeSearch()
            }
        }

        // Show voice button
        displayVoiceButton(true)

        // Initialize the search view.
        initSearchView()

        adapter = CursorSearchAdapter(context, getHistoryCursor(), 0)
        adapter?.setFilterQueryProvider { constraint ->
            val filter = constraint.toString()

            if (filter.isEmpty()) {
                getHistoryCursor()
            } else {
                context?.contentResolver?.query(
                        HistoryContract.HistoryEntry.CONTENT_URI,
                        null,
                        HistoryContract.HistoryEntry.COLUMN_QUERY + " LIKE ?",
                        arrayOf("%$filter%"),
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " DESC, " +
                                HistoryContract.HistoryEntry.COLUMN_QUERY
                )
            }
        }

        suggestionsListView?.adapter = adapter
        suggestionsListView?.isTextFilterEnabled = true
    }

    /**
     * Initializes the style of this view.
     * @param attributeSet The attributes to apply to the view.
     * @param defStyleAttribute An attribute to the style theme applied to this view.
     */
    private fun initStyle(attributeSet: AttributeSet?, defStyleAttribute: Int) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialSearchView, defStyleAttribute, 0)

        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
                background = typedArray.getDrawable(R.styleable.MaterialSearchView_searchBackground)
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColor,
                        ContextCompat.getColor(context, R.color.black)))
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColorHint,
                        ContextCompat.getColor(context, R.color.gray_50)))
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_hint)) {
                setHint(typedArray.getString(R.styleable.MaterialSearchView_android_hint))
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchVoiceIcon)) {
                setVoiceIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchVoiceIcon,
                        R.drawable.ic_action_voice_search)
                )
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
                setClearIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchCloseIcon,
                        R.drawable.ic_action_navigation_close)
                )
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchBackIcon,
                        R.drawable.ic_action_navigation_arrow_back)
                )
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchSuggestionBackground,
                        R.color.search_layover_bg)
                )
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_inputType)) {
                setInputType(typedArray.getInteger(
                        R.styleable.MaterialSearchView_android_inputType,
                        InputType.TYPE_CLASS_TEXT)
                )
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBarHeight)) {
                setSearchBarHeight(typedArray.getDimensionPixelSize(R.styleable.MaterialSearchView_searchBarHeight, getAppCompatActionBarHeight()))
            } else {
                setSearchBarHeight(getAppCompatActionBarHeight())
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_voiceHintPrompt)) {
                setVoiceHintPrompt(typedArray.getString(R.styleable.MaterialSearchView_voiceHintPrompt))
            } else {
                setVoiceHintPrompt(context.getString(R.string.hint_prompt))
            }

            fitsSystemWindows = typedArray.getBoolean(R.styleable.MaterialSearchView_android_fitsSystemWindows, false)

            typedArray.recycle()
        }
    }

    /**
     * Preforms necessary initializations on the SearchView.
     */
    private fun initSearchView() {
        searchEditText?.setOnEditorActionListener { _, _, _ ->
            // When an edit occurs, submit the query.
            onSubmitQuery()
            true
        }

        searchEditText?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // When the text changes, filter
                adapter?.filter?.filter(s.toString())
                adapter?.notifyDataSetChanged()
                (this@KTMaterialSearchView::onTextChanged)(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            // If we gain focus, show keyboard and show suggestions.
            if (hasFocus) {
                showKeyboard(searchEditText)
                showSuggestions()
            }
        }
    }
    //endregion

    //region SHOW METHODS
    /**
     * Displays the keyboard with a focus on the Search EditText.
     * @param view The view to attach the keyboard to.
     */
    private fun showKeyboard(view: View?) {
        if (view?.hasFocus() == true) {
            view.clearFocus()
        }

        view?.requestFocus()

        if (!isHardKeyboardAvailable()) {
            val inputMethodManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 0)
        }
    }

    /**
     * Method that checks if there's a physical keyboard on the phone.
     *
     * @return true if there's a physical keyboard connected, false otherwise.
     */
    private fun isHardKeyboardAvailable(): Boolean {
        return context.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
    }

    /**
     * Changes the visibility of the voice button to VISIBLE or GONE.
     * @param display True to display the voice button, false to hide it.
     */
    private fun displayVoiceButton(display: Boolean) {
        // Only display voice if we pass in true, and it's available
        voice?.visibility = if (display && isVoiceAvailable()) View.VISIBLE else View.GONE
    }

    /**
     * Changes the visibility of the clear button to VISIBLE or GONE.
     * @param display True to display the clear button, false to hide it.
     */
    private fun displayClearButton(display: Boolean) {
        clear?.visibility = if (display) View.VISIBLE else View.GONE
    }

    /**
     * Displays the available suggestions, if any.
     */
    private fun showSuggestions() {
        suggestionsListView?.visibility = View.VISIBLE
    }

    /**
     * Displays the SearchView.
     */
    fun openSearch() {
        // If search is already open, just return.
        if (open) return

        // Get focus
        searchEditText?.setText("")
        searchEditText?.requestFocus()

        if (shouldAnimate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                root?.visibility = View.VISIBLE
                AnimationUtils.circleRevealView(searchBar)
            } else {
                AnimationUtils.fadeInView(root)
            }
        } else {
            root?.visibility = View.VISIBLE
        }

        searchViewListener?.onSearchViewOpened()

        open = true
    }
    //endregion

    //region HIDE METHODS
    /**
     * Hides the suggestion list.
     */
    private fun dismissSuggestions() {
        suggestionsListView?.visibility = View.GONE
    }

    /**
     * Hides the keyboard displayed for the SearchEditText.
     * @param view The view to detach the keyboard from.
     */
    private fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Closes the search view if necessary.
     */
    fun closeSearch() {
        // If we're already closed, just return.
        if (!open) return

        // Clear text, values, and focus.
        searchEditText?.setText("")
        dismissSuggestions()
        clearFocus()

        if (shouldAnimate) {
            val v = root

            val listenerAdapter = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // After the animation is done. Hide the root view.
                    v?.visibility = View.GONE
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AnimationUtils.circleHideView(searchBar, listenerAdapter)
            } else {
                AnimationUtils.fadeOutView(root)
            }
        } else {
            // Just hide the view.
            root?.visibility = View.GONE
        }

        // Call listener if we have one
        searchViewListener?.onSearchViewClosed()

        open = false
    }
    //endregion

    //region INTERFACE METHODS
    /**
     * Filters and updates the buttons when text is changed.
     * @param newText The new text.
     */
    private fun onTextChanged(newText: CharSequence) {
        // Get current query
        currentQuery = searchEditText?.text.toString()

        // If the text is not empty, show the empty button and hide the voice button
        if (!TextUtils.isEmpty(currentQuery)) {
            displayVoiceButton(false)
            displayClearButton(true)
        } else {
            displayClearButton(false)
            displayVoiceButton(true)
        }

        // If we have a query listener and the text has changed, call it.
        onQueryTextListener?.onQueryTextChange(newText.toString())

        oldQuery = currentQuery
    }

    /**
     * Called when a query is submitted. This will close the search view.
     */
    private fun onSubmitQuery() {
        // Get the query.
        val query = searchEditText?.text

        // If the query is not null and it has some text, submit it.
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {

            // If we don't have a listener, or if the search view handled the query, close it.
            // TODO - Improve.
            if (onQueryTextListener == null || onQueryTextListener?.onQueryTextSubmit(query.toString()) != true) {

                if (shouldKeepHistory) {
                    saveQueryToDb(query.toString(), System.currentTimeMillis())
                }

                // Refresh the cursor on the adapter,
                // so the new entry will be shown on the next time the user opens the search view.
                refreshAdapterCursor()

                closeSearch()
                searchEditText?.setText("")
            }
        }
    }

    /**
     * Handles when the voice button is clicked and starts listening, then calls activity with voice search.
     */
    private fun onVoiceClicked() {
        // If the user has their own OnVoiceClickedListener defined, call that. Otherwise, use
        // the library default.
        val voiceListener = onVoiceClickedListener
        if (voiceListener != null) {
            voiceListener.onVoiceClicked()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, hintPrompt)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS) // Quantity of results we want to receive

            (context as? Activity)?.startActivityForResult(intent, REQUEST_VOICE)
        }
    }
    //endregion

    //region MUTATORS
    /**
     * Sets an OnItemClickListener to the suggestion list.
     *
     * @param listener - The ItemClickListener.
     */
    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        suggestionsListView?.onItemClickListener = listener
    }

    /**
     * Sets an OnItemLongClickListener to the suggestion list.
     *
     * @param listener - The ItemLongClickListener.
     */
    fun setOnItemLongClickListener(listener: AdapterView.OnItemLongClickListener) {
        suggestionsListView?.onItemLongClickListener = listener
    }

    /**
     * Set the query to search view. If submit is set to true, it'll submit the query.
     *
     * @param query - The Query value.
     * @param submit - Whether to submit or not the query or not.
     */
    fun setQuery(query: CharSequence?, submit: Boolean) {
        searchEditText?.setText(query)

        if (query != null) {
            searchEditText?.setSelection(searchEditText?.length() ?: 0)
            currentQuery = query
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    /**
     * Sets the background of the SearchView.
     * @param background The drawable to use as a background.
     */
    @Suppress("DEPRECATION")
    override fun setBackground(background: Drawable?) {
        // Method changed in jelly bean for setting background.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tintView?.background = background
        } else {
            tintView?.setBackgroundDrawable(background)
        }
    }

    /**
     * Sets the background color of the SearchView.
     *
     * @param color The color to use for the background.
     */
    override fun setBackgroundColor(color: Int) {
        setTintColor(color)
    }

    /**
     * Set background color of search bar.
     *
     * @param[color] The color to display as the search background.
     */
    fun setSearchBarColor(color: Int) {
        searchEditText?.setBackgroundColor(color)
    }

    /**
     * Change the color of the background tint.
     *
     * @param color The new color.
     */
    private fun setTintColor(color: Int) {
        tintView?.setBackgroundColor(color)
    }

    /**
     * Sets the alpha value of the background tint.
     * @param alpha The alpha value, from 0 to 255.
     */
    fun setTintAlpha(alpha: Int) {
        if (alpha < 0 || alpha > 255) return

        val d = tintView?.background

        if (d is ColorDrawable) {
            val color = d.color
            val newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))

            setTintColor(newColor)
        }
    }

    /**
     * Adjust the background tint alpha, based on a percentage.
     *
     * @param factor The factor of the alpha, from 0% to 100%.
     */
    fun adjustTintAlpha(factor: Float) {
        if (factor < 0 || factor > 1.0) return

        val d = tintView?.background

        if (d is ColorDrawable) {
            var color = d.color

            color = adjustAlpha(color, factor)

            tintView?.setBackgroundColor(color)
        }
    }

    /**
     * Adjust the alpha of a color based on a percent factor.
     *
     * @param color - The color you want to change the alpha value.
     * @param factor - The factor of the alpha, from 0% to 100%.
     * @return The color with the adjusted alpha value.
     */
    private fun adjustAlpha(color: Int, factor: Float): Int {
        if (factor < 0) return color

        val alpha = Math.round(Color.alpha(color) * factor)

        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Sets the text color of the EditText.
     * @param color The color to use for the EditText.
     */
    fun setTextColor(color: Int) {
        searchEditText?.setTextColor(color)
    }

    /**
     * Sets the text color of the search hint.
     * @param color The color to be used for the hint text.
     */
    fun setHintTextColor(color: Int) {
        searchEditText?.setHintTextColor(color)
    }

    /**
     * Sets the hint to be used for the search EditText.
     * @param hint The hint to be displayed in the search EditText.
     */
    fun setHint(hint: CharSequence?) {
        searchEditText?.hint = hint
    }

    /**
     * Sets the icon for the voice action.
     * @param resourceId The drawable to represent the voice action.
     */
    fun setVoiceIcon(resourceId: Int) {
        voice?.setImageResource(resourceId)
    }

    /**
     * Sets the icon for the clear action.
     * @param resourceId The resource ID of drawable that will represent the clear action.
     */
    fun setClearIcon(resourceId: Int) {
        clear?.setImageResource(resourceId)
    }

    /**
     * Sets the icon for the back action.
     * @param resourceId The resource Id of the drawable that will represent the back action.
     */
    fun setBackIcon(resourceId: Int) {
        back?.setImageResource(resourceId)
    }

    /**
     * Sets the background of the suggestions ListView.
     *
     * @param resource The resource to use as a background for the
     * suggestions listview.
     */
    fun setSuggestionBackground(resource: Int) {
        if (resource > 0) {
            suggestionsListView?.setBackgroundResource(resource)
        }
    }

    /**
     * Sets the input type of the SearchEditText.
     *
     * @param inputType The input type to set to the EditText.
     */
    fun setInputType(inputType: Int) {
        searchEditText?.inputType = inputType
    }

    /**
     * Sets the bar height if prefered to not use the existing actionbar height value
     *
     * @param height The value of the height in pixels
     */
    fun setSearchBarHeight(height: Int) {
        searchBar?.minimumHeight = height
        searchBar?.layoutParams?.height = height
    }

    /**
     * Sets the prompt that is displayed when the voice button is clicked.
     *
     * @param[hintPrompt] The string that we should display. If empty, it uses a library default.
     */
    fun setVoiceHintPrompt(hintPrompt: String?) {
        this.hintPrompt = if (hintPrompt?.isEmpty() != false) context.getString(R.string.hint_prompt) else hintPrompt
    }

    /**
     * Returns the actual AppCompat ActionBar height value. This will be used as the default
     *
     * @return The value of the actual actionbar height in pixels
     */
    private fun getAppCompatActionBarHeight(): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
        return resources.getDimensionPixelSize(tv.resourceId)
    }
    //endregion

    //region ACCESSORS
    /**
     * Gets the current text on the SearchView, if any. Returns an empty String if no text is available.
     * @return The current query, or an empty String if there's no query.
     */
    fun getCurrentQuery(): String {
        return currentQuery.toString()
    }

    /** Determines if the user's voice is available
     * @return True if we can collect the user's voice, false otherwise.
     */
    private fun isVoiceAvailable(): Boolean {
        // Get package manager
        val packageManager = context.packageManager

        // Gets a list of activities that can handle this intent.
        val activities = packageManager.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)

        // Returns true if we have at least one activity.
        return activities.size > 0
    }

    /**
     * Retrieves a suggestion at a given index in the adapter.
     *
     * @return The search suggestion for that index.
     */
    fun getSuggestionAtPosition(position: Int): String {
        // If position is out of range just return empty string.
        return if (position < 0 || position >= adapter?.count ?: 0) {
            ""
        } else {
            adapter?.getItem(position).toString()
        }
    }
    //endregion

    //region VIEW METHODS
    /**
     * Handles any cleanup when focus is cleared from the view.
     */
    override fun clearFocus() {
        this.clearingFocus = true
        hideKeyboard(this)
        super.clearFocus()
        searchEditText?.clearFocus()
        this.clearingFocus = false
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        // Don't accept if we are clearing focus, or if the view isn't focusable.
        return !(clearingFocus || !isFocusable) && (searchBar?.requestFocus(direction, previouslyFocusedRect) == true)
    }

    fun activityResumed() {
        refreshAdapterCursor()
    }
    //endregion

    //region DATABASE METHODS
    /**
     * Save a query to the local database.
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param ms - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     */
    @Synchronized
    fun saveQueryToDb(query: String, ms: Long) {
        if (!TextUtils.isEmpty(query) && ms > 0) {
            val values = ContentValues()

            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, query)
            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, ms)
            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 1) // Saving as history.

            context.contentResolver.insert(HistoryContract.HistoryEntry.CONTENT_URI, values)
        }
    }

    /**
     * Add a single suggestion item to the suggestion list.
     * @param suggestion - The suggestion to be inserted on the database.
     */
    @Synchronized
    fun addSuggestion(suggestion: String) {
        if (!TextUtils.isEmpty(suggestion)) {
            val value = ContentValues()
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, suggestion)
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis())
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 0) // Saving as suggestion.


            context.contentResolver.insert(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    value
            )
        }
    }

    /**
     * Removes a single suggestion from the list. <br></br>
     * Disclaimer, this doesn't remove a single search history item, only suggestions.
     * @param suggestion - The suggestion to be removed.
     */
    @Synchronized
    fun removeSuggestion(suggestion: String) {
        if (!TextUtils.isEmpty(suggestion)) {
            context.contentResolver.delete(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_QUERY +
                            " = ? AND " +
                            HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY +
                            " = ?",
                    arrayOf(suggestion, (0).toString())
            )
        }
    }

    @Synchronized
    fun addSuggestions(suggestions: List<String>) {
        val toSave = ArrayList<ContentValues>()
        for (str in suggestions) {
            val value = ContentValues()
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, str)
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis())
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 0) // Saving as suggestion.

            toSave.add(value)
        }

        val values = toSave.toTypedArray()

        context.contentResolver.bulkInsert(
                HistoryContract.HistoryEntry.CONTENT_URI,
                values
        )
    }

    fun addSuggestions(suggestions: Array<String>) {
        val list = ArrayList(Arrays.asList(*suggestions))
        addSuggestions(list)
    }

    private fun getHistoryCursor(): Cursor? {
        return context.contentResolver.query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                arrayOf("1"),
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " DESC LIMIT " + maxHistory
        )
    }

    private fun refreshAdapterCursor() {
        val historyCursor = getHistoryCursor()
        adapter?.changeCursor(historyCursor)
    }

    @Synchronized
    fun clearSuggestions() {
        context.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                arrayOf("0")
        )
    }

    @Synchronized
    fun clearHistory() {
        context.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                arrayOf("1")
        )
    }

    @Synchronized
    fun clearAll() {
        context.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null, null
        )
    }
    //endregion

    companion object {
        /**
         * The freaking log tag. Used for logs, duh.
         */
        @JvmStatic private val LOG_TAG = MaterialSearchView::class.java.simpleName

        /**
         * The maximum number of results we want to return from the voice recognition.
         */
        @JvmStatic private val MAX_RESULTS = 1

        /**
         * The identifier for the voice request intent. (Guess why it's 42).
         */
        @JvmStatic
        val REQUEST_VOICE = 42
    }
}