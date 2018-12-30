package br.com.mauker.materialsearchview.adapters

import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView

import br.com.mauker.materialsearchview.R
import br.com.mauker.materialsearchview.db.HistoryContract

/**
 * Created by mauker on 19/04/2016.
 *
 * Default adapter used for the suggestion/history ListView.
 */
open class CursorSearchAdapter @JvmOverloads constructor(
        context: Context,
        cursor: Cursor,
        flags: Int = 0
) : CursorAdapter(context, cursor, flags) {

    var textColor = Color.WHITE
    var historyIcon = R.drawable.ic_history_white
    var suggestionIcon = R.drawable.ic_action_search_white

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val vh = ListViewHolder(view)
        view.tag = vh

        val text = cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY))

        val isHistory = cursor.getInt(cursor.getColumnIndexOrThrow(
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY)) != 0

        val historyItem = SearchHistoryItem(text, isHistory)
        vh.bindItem(historyItem)
    }

    override fun getItem(position: Int): Any {
        var retString = ""

        // Move to position, get query
        val cursor = cursor
        if (cursor.moveToPosition(position)) {
            retString = cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY))
        }

        return retString
    }

    private inner class ListViewHolder internal constructor(convertView: View) {
        private val iv_icon: ImageView = convertView.findViewById(R.id.iv_icon)
        private val tv_content: TextView = convertView.findViewById(R.id.tv_str)

        fun bindItem(item: SearchHistoryItem) {
            tv_content.text = item.text
            tv_content.setTextColor(textColor)

            val iconRes = if (item.isHistory) historyIcon else suggestionIcon
            iv_icon.setImageResource(iconRes)
        }
    }
}

data class SearchHistoryItem(val text: String, val isHistory: Boolean)
