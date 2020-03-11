package xyz.DKMobile.QuickJot2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An activity which shows all notes in a two column, selectable list.
 */
class ListActivity : AdapterView.OnItemSelectedListener, AppCompatActivity() {
    var noteList: List<NoteEntity> = ArrayList()
    lateinit var db: AppDatabase
    lateinit var rv: RecyclerView

    var list = arrayOf("All","General","TO-DO","Shopping","Idea","Goals","Read / Watch","Remember")
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        this.supportActionBar!!.hide()

        //Database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "notes.db"
        ).build()

        spinner = findViewById(R.id.categoryListSpinner)
        rv = findViewById(R.id.recyclerViewNotes)

        initListeners()
        addNotes(this)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = list[position]
    }

    //TODO if there are no more listeners, remove this function
    fun initListeners(){
        initSpinner()
    }

    /**
     * Initializes the category spinner.
     */
    fun initSpinner() {
        spinner.onItemSelectedListener = this
        val array_adapter = ArrayAdapter(this,R.layout.list_spinner_selected_item,list)
        array_adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = array_adapter
    }

    /**
     * Queries the database for all notes to populate the activity.
     * Then sets the recycler view adapter and layout manager.
     */
    fun addNotes(context: Context){
        GlobalScope.launch(Dispatchers.Main){
            withContext(Dispatchers.IO){
                noteList = db.noteDao().getAll()
            }
            rv.adapter = NoteAdapter(noteList,context)
            rv.layoutManager = GridLayoutManager(context,2)
        }
    }
}

/*
----------------------------------------------Answer from StackOverflow------------------------------------
In coroutines you can make a queue of non-identical coroutine codes. For example one block from coroutine1
and another one from coroutine2 and make them run sequentially. It is possible using
withContext(CoroutineContext)

Assume this code:

fun uiCode() {
  // doing things specially on mainThread
}

fun uiCode2() {
  // More work on mainThread
}

fun ioCode() {
  // Doing something not related to mainThread.
}

fun main() {

  launch(Dispatchers.Main) { // 1- run a coroutine
     uiCode() // will run on MainThread
     withContext(Dispachers.IO) { // 2- Coroutine will wait for ioCode
         ioCode() // Will run on ioThread
     }
     uiCode2() // 3- And then it will run this part
  }
}

If you wanted to do it asyncronously, use launch(), instead of withContext().
 */