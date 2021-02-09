package br.com.mauker.materialsearchview.sealedClasses

sealed class Message {
    data class SaveQuery(val query: String): Message()
    data class AddSuggestion(val suggestion: String): Message()
    data class AddPin(val pin: String): Message()
    data class AddSuggestions(val suggestions: List<String>): Message()
    data class AddPinnedItems(val pinnedItems: List<String>): Message()

    data class RemoveItem(val item: String): Message()
    object ClearSuggestions: Message()
    object ClearHistory: Message()
    object ClearPinned: Message()
    object ClearAll: Message()
}
