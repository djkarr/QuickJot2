package xyz.DKMobile.QuickJot

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class NoteAdapter(val items: List<NoteEntity>, val context: Context) :
RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    var list: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        populateList()
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,
            parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.listboxview?.text = list.get(position)
        var uid = items.get(position).uid
        var category = items.get(position).category
        var text = items.get(position).noteText

        //Set listener on each individual list item
        holder.listboxview.setOnClickListener {
            var intent = Intent(context, EditActivity::class.java).apply {
                putExtra("uid",uid)
                putExtra("category",category)
                putExtra("text",text)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            //TODO Review this change: Added this clear task flag so that any new edit activity will
            //become the new root activity, so that ondestroy won't be called and edits won't be cleared out yet.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(context,intent,null)
        }
    }

    /**
     * Populates arraylist with note text.
     */
    fun populateList(){
        for(i in items){
            list.add(i.noteText)
        }
    }

    fun update(newList: ArrayList<String>){
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each note to
        val listboxview = view.list_box_view
    }
}