package xyz.DKMobile.QuickJot2

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class NoteAdapter(val items: List<NoteEntity>, val context: Context) :
RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    var list: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {
        var size = items.size
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
            //TODO change toast to new activity with intent of the selected note
            Toast.makeText(context,"clicked $position",Toast.LENGTH_SHORT).show()
            var intent = Intent(context, EditActivity::class.java).apply {
                putExtra("uid",uid)
                putExtra("category",category)
                putExtra("text",text)
               // addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            startActivity(context,intent,null)
        }
    }

    /**
     * Populates
     */
    fun populateList(){
        for(i in items){
            list.add(i.noteText)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each note to
        val listboxview = view.list_box_view
    }
}