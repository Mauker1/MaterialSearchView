package br.com.mauker.materialsearchview.listeners

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