package xyz.DKMobile.QuickJot2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class NoteAdapter(val items: List<NoteEntity>, val context: Context) :
RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    var list: ArrayList<String> = ArrayList()
    override fun getItemCount(): Int {
        Log.i("SIZE","ITEM COUNTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
        var size = items.size
        Log.i("SIZE",size.toString())
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("REACHED ONCREATEVIEWHOLDER","&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
        populateList()
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,
            parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.listboxview?.text = list.get(position)
    }

    fun populateList(){
        Log.i("PUPULATING LIST++++++++++++++++++++++++++","+++++++++++++++++++++++")
        for(i in items){
            Log.i("output",i.noteText)
            list.add(i.noteText)
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val listboxview = view.list_box_view
    }
}