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
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import br.com.mauker.materialsearchview.adapters.CursorSearchAdapter
import br.com.mauker.materialsearchview.db.HistoryContract
import br.com.mauker.materialsearchview.utils.AnimationUtils.circleHideView
import br.com.mauker.materialsearchview.utils.AnimationUtils.circleRevealView
import br.com.mauker.materialsearchview.utils.AnimationUtils.fadeInView
import br.com.mauker.materialsearchview.utils.AnimationUtils.fadeOutView
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Mauker and Adam McNeilly on 30/03/2016. dd/MM/YY.
 * Maintained by Mauker, Adam McNeilly and our beautiful open source community <3
 * Based on stadiko on 6/8/15. https://github.com/krishnakapil/MaterialSeachView
 */
class MaterialSearchView @JvmOverloads constructor(
        private val mContext: Context, attributeSet: AttributeSet? = null, defStyleAttributes: Int = 0
) : FrameLayout(mContext, attributeSet) {

    companion object {
        //region Properties
        /**
         * The freaking log tag. Used for logs, duh.
         */
        private val LOG_TAG = MaterialSearchView::class.java.simpleName

        /**
         * The maximum number of results we want to return from the voice recognition.
         */
        private const val MAX_RESULTS = 1

        /**
         * The identifier for the voice request intent. (Guess why it's 42).
         */
        const val REQUEST_VOICE = 42

        /**
         * Number of suggestions to show.
         */
        private var MAX_HISTORY = BuildConfig.MAX_HISTORY

        private val EMPTY_STRING = ""

        /**
         * Sets how many items you want to show from the history database.
         *
         * @param maxHistory - The number of items you want to display.
         */
        fun setMaxHistoryResults(maxHistory: Int) {
            MAX_HISTORY = maxHistory
        }
    }

    //endregion
    //region Constructors
    init {
        // Initialize view
        init()

        // Initialize style
        initStyle(attributeSet, defStyleAttributes)
    }

    /**
     * Determines if the search view is opened or closed.
     * @return True if the search view is open, false if it is closed.
     */
    /**
     * Whether or not the search view is open right now.
     */
    var isOpen = false
        private set

    /**
     * Whether or not the MaterialSearchView will animate into view or just appear.
     */
    private var mShouldAnimate = true

    /**
     * Whether or not the MaterialSearchView will clonse under a click on the Tint View (Blank Area).
     */
    private var mShouldCloseOnTintClick = false

    /**
     * Wheter to keep the search history or not.
     */
    private var mShouldKeepHistory = true

    /**
     * Flag for whether or not we are clearing focus.
     */
    private var mClearingFocus = false

    /**
     * Voice hint prompt text.
     */
    private lateinit var mHintPrompt: String

    /**
     * Allows user to decide whether to allow voice search.
     */
    var isVoiceIconEnabled = false
    //endregion

    //region UI Elements
    /**
     * The tint that appears over the search view.
     */
    private lateinit var mTintView: View

    /**
     * The root of the search view.
     */
    private lateinit var mRoot: FrameLayout

    /**
     * The bar at the top of the SearchView containing the EditText and ImageButtons.
     */
    private lateinit var mSearchBar: LinearLayout

    /**
     * The EditText for entering a search.
     */
    private lateinit var mSearchEditText: EditText

    /**
     * The ImageButton for navigating back.
     */
    private lateinit var mBack: ImageButton

    /**
     * The ImageButton for initiating a voice search.
     */
    private lateinit var mVoice: ImageButton

    /**
     * The ImageButton for clearing the search text.
     */
    private lateinit var mClear: ImageButton

    /**
     * The ListView for displaying suggestions based on the search.
     */
    private lateinit var mSuggestionsListView: ListView

    /**
     * Adapter for displaying suggestions.
     */
    lateinit var adapter: CursorAdapter
        private set
    //endregion

    //region Query Properties
    /**
     * The previous query text.
     */
    private lateinit var mOldQuery: CharSequence

    /**
     * The current query text.
     */
    private lateinit var mCurrentQuery: CharSequence
    //endregion

    //region Listeners
    /**
     * Listener for when the query text is submitted or changed.
     */
    private var mOnQueryTextListener: OnQueryTextListener? = null

    /**
     * Listener for when the search view opens and closes.
     */
    private var mSearchViewListener: SearchViewListener? = null

    /**
     * Listener for interaction with the voice button.
     */
    private var mOnVoiceClickedListener: OnVoiceClickedListener? = null
    //endregion

    //region Initializers
    /**
     * Preforms any required initializations for the search view.
     */
    private fun init() {
        // Inflate view
        LayoutInflater.from(mContext).inflate(R.layout.search_view, this, true)

        // Get items
        mRoot = findViewById(R.id.search_layout)
        mTintView = mRoot.findViewById(R.id.transparent_view)
        mSearchBar = mRoot.findViewById(R.id.search_bar)
        mBack = mRoot.findViewById(R.id.action_back)
        mSearchEditText = mRoot.findViewById(R.id.et_search)
        mVoice = mRoot.findViewById(R.id.action_voice)
        mClear = mRoot.findViewById(R.id.action_clear)
        mSuggestionsListView = mRoot.findViewById(R.id.suggestion_list)

        // Set click listeners
        mBack.setOnClickListener { closeSearch() }
        mVoice.setOnClickListener { onVoiceClicked() }
        mClear.setOnClickListener { mSearchEditText.setText(EMPTY_STRING) }
        mTintView.setOnClickListener {
            if (mShouldCloseOnTintClick) {
                closeSearch()
            }
        }

        // Initialize the search view.
        initSearchView()
        adapter = CursorSearchAdapter(mContext, historyCursor, 0)

        adapter.setFilterQueryProvider(FilterQueryProvider { constraint ->
            val filter = constraint.toString()
            if (filter.isEmpty()) {
                historyCursor
            } else {
                mContext.contentResolver.query(
                        HistoryContract.HistoryEntry.CONTENT_URI,
                        null,
                        HistoryContract.HistoryEntry.COLUMN_QUERY + " LIKE ?", arrayOf("%$filter%"),
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " DESC, " +
                                HistoryContract.HistoryEntry.COLUMN_QUERY
                )
            }
        })
        mSuggestionsListView.adapter = adapter
        mSuggestionsListView.isTextFilterEnabled = true
        mSuggestionsListView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val suggestion = getSuggestionAtPosition(position)
            setQuery(suggestion, true)
        }
    }

    /**
     * Initializes the style of this view.
     * @param attributeSet The attributes to apply to the view.
     * @param defStyleAttribute An attribute to the style theme applied to this view.
     */
    private fun initStyle(attributeSet: AttributeSet?, defStyleAttribute: Int) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.MaterialSearchView, defStyleAttribute, 0)

        if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
            background = typedArray.getDrawable(R.styleable.MaterialSearchView_searchBackground)!!
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
            setTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColor,
                    ContextCompat.getColor(mContext, R.color.black)))
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
            setHintTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColorHint,
                    ContextCompat.getColor(mContext, R.color.gray_50)))
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
        if (typedArray.hasValue(R.styleable.MaterialSearchView_historyIcon) && adapter is CursorSearchAdapter) {
            (adapter as CursorSearchAdapter).historyIcon = typedArray.getResourceId(
                    R.styleable.MaterialSearchView_historyIcon,
                    R.drawable.ic_history_white)
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_suggestionIcon) && adapter is CursorSearchAdapter) {
            (adapter as CursorSearchAdapter).suggestionIcon = typedArray.getResourceId(
                    R.styleable.MaterialSearchView_suggestionIcon,
                    R.drawable.ic_action_search_white)
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_listTextColor) && adapter is CursorSearchAdapter) {
            (adapter as CursorSearchAdapter).textColor = typedArray.getColor(R.styleable.MaterialSearchView_listTextColor,
                    ContextCompat.getColor(mContext, R.color.white))
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_android_inputType)) {
            setInputType(typedArray.getInteger(
                    R.styleable.MaterialSearchView_android_inputType,
                    InputType.TYPE_CLASS_TEXT)
            )
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBarHeight)) {
            setSearchBarHeight(typedArray.getDimensionPixelSize(R.styleable.MaterialSearchView_searchBarHeight, appCompatActionBarHeight))
        } else {
            setSearchBarHeight(appCompatActionBarHeight)
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_voiceHintPrompt)) {
            setVoiceHintPrompt(typedArray.getString(R.styleable.MaterialSearchView_voiceHintPrompt) ?: EMPTY_STRING)
        } else {
            setVoiceHintPrompt(mContext.getString(R.string.hint_prompt))
        }
        if (typedArray.hasValue(R.styleable.MaterialSearchView_voiceIconEnabled)) {
            isVoiceIconEnabled = typedArray.getBoolean(R.styleable.MaterialSearchView_voiceIconEnabled, true)
        }
        fitsSystemWindows = false
        typedArray.recycle()

        // Show voice button. We put this here because whether or not it's shown is defined by a style above.
        displayVoiceButton(true)
    }

    /**
     * Preforms necessary initializations on the SearchView.
     */
    private fun initSearchView() {
        mSearchEditText.setOnEditorActionListener { v, actionId, event -> // When an edit occurs, submit the query.
            onSubmitQuery()
            true
        }
        mSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // When the text changes, filter
                adapter.filter.filter(s.toString())
                adapter.notifyDataSetChanged()
                this@MaterialSearchView.onTextChanged(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mSearchEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus -> // If we gain focus, show keyboard and show suggestions.
            if (hasFocus) {
                showKeyboard(mSearchEditText)
                showSuggestions()
            }
        }
    }
    //endregion
    //region Show Methods
    /**
     * Displays the keyboard with a focus on the Search EditText.
     * @param view The view to attach the keyboard to.
     */
    private fun showKeyboard(view: View?) {
        view?.requestFocus()
        if (isHardKeyboardAvailable.not()) {
            val inputMethodManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(view, 0)
        }
    }

    /**
     * Method that checks if there's a physical keyboard on the phone.
     *
     * @return true if there's a physical keyboard connected, false otherwise.
     */
    private val isHardKeyboardAvailable: Boolean
        get() = mContext.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS

    /**
     * Changes the visibility of the voice button to VISIBLE or GONE.
     * @param display True to display the voice button, false to hide it.
     */
    private fun displayVoiceButton(display: Boolean) {
        // Only display voice if we pass in true, and it's available
        if (display && isVoiceAvailable && isVoiceIconEnabled) {
            mVoice.visibility = VISIBLE
        } else {
            mVoice.visibility = GONE
        }
    }

    /**
     * Changes the visibility of the clear button to VISIBLE or GONE.
     * @param display True to display the clear button, false to hide it.
     */
    private fun displayClearButton(display: Boolean) {
        mClear.visibility = if (display) VISIBLE else GONE
    }

    /**
     * Displays the available suggestions, if any.
     */
    private fun showSuggestions() {
        mSuggestionsListView.visibility = VISIBLE
    }

    /**
     * Displays the SearchView.
     */
    fun openSearch() {
        // If search is already open, just return.
        if (isOpen) {
            return
        }

        // Get focus
        mSearchEditText.setText(EMPTY_STRING)
        mSearchEditText.requestFocus()
        if (mShouldAnimate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRoot.visibility = VISIBLE
                circleRevealView(mSearchBar)
            } else {
                fadeInView(mRoot)
            }
        } else {
            mRoot.visibility = VISIBLE
        }

        mSearchViewListener?.onSearchViewOpened()

        isOpen = true
    }
    //endregion
    //region Hide Methods
    /**
     * Hides the suggestion list.
     */
    private fun dismissSuggestions() {
        mSuggestionsListView.visibility = GONE
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
        if (!isOpen) {
            return
        }

        // Clear text, values, and focus.
        mSearchEditText.setText(EMPTY_STRING)
        dismissSuggestions()
        clearFocus()
        if (mShouldAnimate) {
            val v: View = mRoot
            val listenerAdapter: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // After the animation is done. Hide the root view.
                    v.visibility = GONE
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleHideView(mSearchBar, listenerAdapter)
            } else {
                fadeOutView(mRoot)
            }
        } else {
            // Just hide the view.
            mRoot.visibility = GONE
        }


        // Call listener if we have one
        mSearchViewListener?.onSearchViewClosed()

        isOpen = false
    }
    //endregion
    //region Interface Methods
    /**
     * Filters and updates the buttons when text is changed.
     * @param newText The new text.
     */
    private fun onTextChanged(newText: CharSequence) {
        // Get current query
        mCurrentQuery = mSearchEditText.text

        // If the text is not empty, show the empty button and hide the voice button
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            displayVoiceButton(false)
            displayClearButton(true)
        } else {
            displayClearButton(false)
            displayVoiceButton(true)
        }

        // If we have a query listener and the text has changed, call it.
        mOnQueryTextListener?.onQueryTextChange(newText.toString())

        mOldQuery = mCurrentQuery
    }

    /**
     * Called when a query is submitted. This will close the search view.
     */
    private fun onSubmitQuery() {
        // Get the query.
        val query: CharSequence? = mSearchEditText.text

        // If the query is not null and it has some text, submit it.
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {

            // If we don't have a listener, or if the search view handled the query, close it.
            // TODO - Improve.
            if (mOnQueryTextListener?.onQueryTextSubmit(query.toString()) == false) {
                if (mShouldKeepHistory) {
                    saveQueryToDb(query.toString(), System.currentTimeMillis())
                }

                // Refresh the cursor on the adapter,
                // so the new entry will be shown on the next time the user opens the search view.
                refreshAdapterCursor()
                closeSearch()
                mSearchEditText.setText(EMPTY_STRING)
            }
        }
    }

    /**
     * Handles when the voice button is clicked and starts listening, then calls activity with voice search.
     */
    private fun onVoiceClicked() {
        // If the user has their own OnVoiceClickedListener defined, call that. Otherwise, use
        // the library default.
        if (mOnVoiceClickedListener != null) {
            mOnVoiceClickedListener?.onVoiceClicked()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mHintPrompt)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS) // Quantity of results we want to receive
            if (mContext is Activity) {
                mContext.startActivityForResult(intent, REQUEST_VOICE)
            }
        }
    }

    //endregion

    //region Mutators
    fun setOnQueryTextListener(mOnQueryTextListener: OnQueryTextListener?) {
        this.mOnQueryTextListener = mOnQueryTextListener
    }

    fun setSearchViewListener(mSearchViewListener: SearchViewListener?) {
        this.mSearchViewListener = mSearchViewListener
    }

    /**
     * Sets an OnItemClickListener to the suggestion list.
     *
     * @param listener - The ItemClickListener.
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mSuggestionsListView.onItemClickListener = listener
    }

    /**
     * Sets an OnItemLongClickListener to the suggestion list.
     *
     * @param listener - The ItemLongClickListener.
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        mSuggestionsListView.onItemLongClickListener = listener
    }

    /**
     * Toggles the Tint click action.
     *
     * @param shouldClose - Whether the tint click should close the search view or not.
     */
    fun setCloseOnTintClick(shouldClose: Boolean) {
        mShouldCloseOnTintClick = shouldClose
    }

    /**
     * Sets whether the MSV should be animated on open/close or not.
     *
     * @param mShouldAnimate - true if you want animations, false otherwise.
     */
    fun setShouldAnimate(mShouldAnimate: Boolean) {
        this.mShouldAnimate = mShouldAnimate
    }

    /**
     * Sets whether the MSV should be keeping track of the submited queries or not.
     *
     * @param keepHistory - true if you want to save the search history, false otherwise.
     */
    fun setShouldKeepHistory(keepHistory: Boolean) {
        mShouldKeepHistory = keepHistory
    }

    /**
     * Set the query to search view. If submit is set to true, it'll submit the query.
     *
     * @param query - The Query value.
     * @param submit - Whether to submit or not the query or not.
     */
    fun setQuery(query: CharSequence?, submit: Boolean) {
        mSearchEditText.setText(query)
        if (query != null) {
            mSearchEditText.setSelection(mSearchEditText.length())
            mCurrentQuery = query
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    /**
     * Sets the background of the SearchView.
     * @param background The drawable to use as a background.
     */
    override fun setBackground(background: Drawable) {
        // Method changed in jelly bean for setting background.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTintView.background = background
        } else {
            mTintView.setBackgroundDrawable(background)
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

    fun setSearchBarColor(color: Int) {
        // Set background color of search bar.
        mSearchEditText.setBackgroundColor(color)
    }

    /**
     * Change the color of the background tint.
     *
     * @param color The new color.
     */
    private fun setTintColor(color: Int) {
        mTintView.setBackgroundColor(color)
    }

    /**
     * Sets the alpha value of the background tint.
     * @param alpha The alpha value, from 0 to 255.
     */
    fun setTintAlpha(alpha: Int) {
        if (alpha < 0 || alpha > 255) return
        val d = mTintView.background
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
        val d = mTintView.background
        if (d is ColorDrawable) {
            var color = d.color
            color = adjustAlpha(color, factor)
            mTintView.setBackgroundColor(color)
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
        val alpha = (Color.alpha(color) * factor).roundToInt()
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Sets the text color of the EditText.
     * @param color The color to use for the EditText.
     */
    fun setTextColor(color: Int) {
        mSearchEditText.setTextColor(color)
    }

    /**
     * Sets the text color of the search hint.
     * @param color The color to be used for the hint text.
     */
    fun setHintTextColor(color: Int) {
        mSearchEditText.setHintTextColor(color)
    }

    /**
     * Sets the hint to be used for the search EditText.
     * @param hint The hint to be displayed in the search EditText.
     */
    fun setHint(hint: CharSequence?) {
        mSearchEditText.hint = hint
    }

    /**
     * Sets the icon for the voice action.
     * @param resourceId The drawable to represent the voice action.
     */
    fun setVoiceIcon(resourceId: Int) {
        mVoice.setImageResource(resourceId)
    }

    /**
     * Sets the icon for the clear action.
     * @param resourceId The resource ID of drawable that will represent the clear action.
     */
    fun setClearIcon(resourceId: Int) {
        mClear.setImageResource(resourceId)
    }

    /**
     * Sets the icon for the back action.
     * @param resourceId The resource Id of the drawable that will represent the back action.
     */
    fun setBackIcon(resourceId: Int) {
        mBack.setImageResource(resourceId)
    }

    /**
     * Sets the background of the suggestions ListView.
     *
     * @param resource The resource to use as a background for the
     * suggestions listview.
     */
    fun setSuggestionBackground(resource: Int) {
        if (resource > 0) {
            mSuggestionsListView.setBackgroundResource(resource)
        }
    }

    /**
     * Changes the default history list icon.
     *
     * @param resourceId The resource id of the new history icon.
     */
    fun setHistoryIcon(@DrawableRes resourceId: Int) {
        adapter.let {
            if (it is CursorSearchAdapter) {
                it.historyIcon = resourceId
            }
        }
    }

    /**
     * Changes the default suggestion list icon.
     *
     * @param resourceId The resource id of the new suggestion icon.
     */
    fun setSuggestionIcon(@DrawableRes resourceId: Int) {
        adapter.let {
            if (it is CursorSearchAdapter) {
                it.suggestionIcon = resourceId
            }
        }
    }

    /**
     * Changes the default suggestion list item text color.
     *
     * @param color The new color.
     */
    fun setListTextColor(color: Int) {
        adapter.let {
            if (it is CursorSearchAdapter) {
                it.textColor = color
            }
        }
    }

    /**
     * Sets the input type of the SearchEditText.
     *
     * @param inputType The input type to set to the EditText.
     */
    fun setInputType(inputType: Int) {
        mSearchEditText.inputType = inputType
    }

    /**
     * Sets a click listener for the voice button.
     */
    fun setOnVoiceClickedListener(listener: OnVoiceClickedListener?) {
        mOnVoiceClickedListener = listener
    }

    fun setOnVoiceClickedListener(listener: () -> Unit) {
        mOnVoiceClickedListener = object: OnVoiceClickedListener {
            override fun onVoiceClicked() {
                listener.invoke()
            }
        }
    }

    /**
     * Sets the bar height if prefered to not use the existing actionbar height value
     *
     * @param height The value of the height in pixels
     */
    fun setSearchBarHeight(height: Int) {
        mSearchBar.minimumHeight = height
        mSearchBar.layoutParams.height = height
    }

    fun setVoiceHintPrompt(hintPrompt: String) {
        mHintPrompt = if (!TextUtils.isEmpty(hintPrompt)) {
            hintPrompt
        } else {
            mContext.getString(R.string.hint_prompt)
        }
    }

    /**
     * Returns the actual AppCompat ActionBar height value. This will be used as the default
     *
     * @return The value of the actual actionbar height in pixels
     */
    private val appCompatActionBarHeight: Int
        get() {
            val tv = TypedValue()
            context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
            return resources.getDimensionPixelSize(tv.resourceId)
        }
    //endregion

    //region Accessors
    /**
     * Gets the current text on the SearchView, if any. Returns an empty String if no text is available.
     * @return The current query, or an empty String if there's no query.
     */
    val currentQuery: String
        get() = if (!TextUtils.isEmpty(mCurrentQuery)) {
            mCurrentQuery.toString()
        } else EMPTY_STRING// Get package manager

    // Gets a list of activities that can handle this intent.

    // Returns true if we have at least one activity.
    /** Determines if the user's voice is available
     * @return True if we can collect the user's voice, false otherwise.
     */
    private val isVoiceAvailable: Boolean
        get() {
            // Get package manager
            val packageManager = mContext.packageManager

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
        return if (position < 0 || position >= adapter.count) {
            EMPTY_STRING
        } else {
            adapter.getItem(position).toString()
        }
    }
    //endregion

    //region View Methods
    /**
     * Handles any cleanup when focus is cleared from the view.
     */
    override fun clearFocus() {
        mClearingFocus = true
        hideKeyboard(this)
        super.clearFocus()
        mSearchEditText.clearFocus()
        mClearingFocus = false
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        // Don't accept if we are clearing focus, or if the view isn't focusable.
        return !(mClearingFocus || !isFocusable) && mSearchEditText.requestFocus(direction, previouslyFocusedRect)
    }

    //----- Lifecycle methods -----//
    //    public void activityPaused() {
    //        Cursor cursor = ((CursorAdapter)mAdapter).getCursor();
    //        if (cursor != null && !cursor.isClosed()) {
    //            cursor.close();
    //        }
    //    }
    fun activityResumed() {
        refreshAdapterCursor()
    }
    //endregion

    //region Database Methods
    /**
     * Save a query to the local database.
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param ms - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     */
    @Synchronized
    fun saveQueryToDb(query: String?, ms: Long) {
        if (!TextUtils.isEmpty(query) && ms > 0) {
            val values = ContentValues()
            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, query)
            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, ms)
            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 1) // Saving as history.
            mContext.contentResolver.insert(HistoryContract.HistoryEntry.CONTENT_URI, values)
        }
    }

    /**
     * Add a single suggestion item to the suggestion list.
     * @param suggestion - The suggestion to be inserted on the database.
     */
    @Synchronized
    fun addSuggestion(suggestion: String?) {
        if (suggestion?.isNotEmpty() == true) {
            val value = ContentValues()
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, suggestion)
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis())
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 0) // Saving as suggestion.
            mContext.contentResolver.insert(
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
    fun removeSuggestion(suggestion: String?) {
        if (suggestion?.isNotEmpty() == true) {
            mContext.contentResolver.delete(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_QUERY +
                            " = ? AND " +
                            HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY +
                            " = ?", arrayOf(suggestion, 0.toString()))
        }
    }

    @Synchronized
    fun addSuggestions(suggestions: List<String?>) {
        val toSave = ArrayList<ContentValues>()
        for (str in suggestions) {
            val value = ContentValues()
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, str)
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis())
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 0) // Saving as suggestion.
            toSave.add(value)
        }
        val values = toSave.toTypedArray()
        mContext.contentResolver.bulkInsert(
                HistoryContract.HistoryEntry.CONTENT_URI,
                values
        )
    }

    fun addSuggestions(suggestions: Array<String?>) {
        val list = ArrayList(listOf(*suggestions))
        addSuggestions(list)
    }

    private val historyCursor: Cursor?
        get() = mContext.contentResolver.query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?", arrayOf("1"),
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " DESC LIMIT " + MAX_HISTORY
        )

    private fun refreshAdapterCursor() {
        val historyCursor = historyCursor
        adapter.changeCursor(historyCursor)
    }

    @Synchronized
    fun clearSuggestions() {
        mContext.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?", arrayOf("0"))
    }

    @Synchronized
    fun clearHistory() {
        mContext.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?", arrayOf("1"))
    }

    @Synchronized
    fun clearAll() {
        mContext.contentResolver.delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        )
    }
    //endregion
    //region Interfaces
    /**
     * Interface that handles the submission and change of search queries.
     */
    interface OnQueryTextListener {
        /**
         * Called when a search query is submitted.
         *
         * @param query The text that will be searched.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        fun onQueryTextSubmit(query: String): Boolean

        /**
         * Called when a search query is changed.
         *
         * @param newText The new text of the search query.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        fun onQueryTextChange(newText: String): Boolean
    }

    /**
     * Interface that handles the opening and closing of the SearchView.
     */
    interface SearchViewListener {
        /**
         * Called when the searchview is opened.
         */
        fun onSearchViewOpened()

        /**
         * Called when the search view closes.
         */
        fun onSearchViewClosed()
    }

    /**
     * Interface that handles interaction with the voice button.
     */
    interface OnVoiceClickedListener {
        /**
         * Called when the user clicks the voice button.
         */
        fun onVoiceClicked()
    } //endregion
}
