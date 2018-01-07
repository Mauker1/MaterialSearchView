package br.com.mauker.materialsearchview.listeners

/**
 * Interface that handles the submission and change of search queries.
 */
interface OnQueryTextListener {
    /**
     * Called when a search query is submitted.
     *
     * @param[query] The text that will be searched.
     * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
     */
    fun onQueryTextSubmit(query: String): Boolean

    /**
     * Called when a search query is changed.
     *
     * @param[newText] The new text of the search query.
     * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
     */
    fun onQueryTextChange(newText: String): Boolean
}