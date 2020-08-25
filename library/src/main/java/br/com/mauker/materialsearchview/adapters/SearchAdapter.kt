package br.com.mauker.materialsearchview.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.mauker.materialsearchview.MaterialSearchView
import br.com.mauker.materialsearchview.R
import br.com.mauker.materialsearchview.db.model.History
import br.com.mauker.materialsearchview.db.model.QueryType

class SearchAdapter(
        val history: MutableList<History>,
        var listener: MaterialSearchView.OnHistoryItemClickListener? = null
): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val icon: ImageView = itemView.findViewById(R.id.iv_icon)
        private val content: TextView = itemView.findViewById(R.id.tv_str)

        fun bind(historyItem: History) {
            icon.setImageResource(when(historyItem.queryType) {
                QueryType.PINNED -> pinIcon
                QueryType.HISTORY-> historyIcon
                else -> suggestionIcon
            })
            content.text = historyItem.query
            content.setTextColor(textColor)

            rootView.setOnClickListener {
                listener?.onClick(historyItem)
            }

            rootView.setOnLongClickListener {
                listener?.onLongClick(historyItem)
                true
            }
        }
    }

    init {
        setHasStableIds(true)
    }

    var textColor = Color.WHITE
    var historyIcon = R.drawable.ic_history_white
    var suggestionIcon = R.drawable.ic_action_search_white
    var pinIcon = R.drawable.ic_pin_outline

    fun updateAdapter(history: MutableList<History>) {
        this.history.clear()
        this.history.addAll(history)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(history[position])
    }

    override fun getItemCount(): Int = history.size

    override fun getItemId(position: Int): Long = history[position].historyId
}