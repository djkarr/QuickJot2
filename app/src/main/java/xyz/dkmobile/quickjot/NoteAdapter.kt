package xyz.dkmobile.quickjot

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import xyz.dkmobile.quickjot.databinding.ListItemBinding

class NoteAdapter(private val items: List<NoteEntity>, private val context: Context) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    var list: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        populateList()
        val binding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
//        return ViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.list_item,
//                parent, false
//            )
//        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemText = list[position]
        holder.bind(itemText)
        val uid = items[position].uid
        val category = items[position].category
        val text = items[position].noteText

        //Set listener on each individual list item
        holder.itemView.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java).apply {
                putExtra("uid", uid)
                putExtra("category", category)
                putExtra("text", text)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            //TODO Review this change: Added this clear task flag so that any new edit activity will
            //become the new root activity, so that ondestroy won't be called and edits won't be cleared out yet.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(context, intent, null)
        }
    }

    /**
     * Populates arraylist with note text.
     */
    private fun populateList() {
        for (i in items) {
            list.add(i.noteText)
        }
    }

    fun update(newList: ArrayList<String>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Holds the TextView that will add each note to
//        val listboxview: TextView = view.list_box_view
        fun bind(text: String) {
            binding.listBoxView.text = text
        }
    }
}