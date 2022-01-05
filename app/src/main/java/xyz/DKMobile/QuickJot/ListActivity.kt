package xyz.DKMobile.QuickJot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



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
        rv.layoutManager = GridLayoutManager(this,2)
        val emptyList = arrayListOf<NoteEntity>()
        //TODO notes are getting remade by initializer functions? Possibly.
        rv.adapter = NoteAdapter(emptyList, this)
        initListeners()
        if(savedInstanceState == null){
            addNotes(this)
        } else {
            val category = savedInstanceState?.getCharSequence("category")
            val index = list.indexOf(category)
            spinner.setSelection(index)
            sortByCategory(index)
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        val category = spinner.selectedItem.toString()
        outState?.putCharSequence("category",category)
    }

    // Don't need to implement.
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Spinner onItemSelected queries the database for notes that fall into the selection category.
     * It then only displays those notes.
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position != 0){
            sortByCategory(position)
        } else {
            addNotes(this)
        }
    }

    /**
     * This version sorts by making a new query.
     */
    fun sortByCategory(position: Int){
        val text: String = list[position]
        //var categorizedList: List<NoteEntity> = ArrayList()

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                if(position == 0){
                    noteList = db.noteDao().getAll()
                } else {
                    noteList = db.noteDao().getByCategory(text)
                }
            }
            rv.adapter = NoteAdapter(noteList, this@ListActivity)
        }
    }


    /**
     * Initialize the listeners.
     */
    fun initListeners(){
        initSpinner()
        initSort()
        initAddNew()
    }

    /**
     * User clicks the 'Add New' button and the edit activity is launched.
     */
    fun initAddNew(){
        val addNew = findViewById<FloatingActionButton>(R.id.add_new_fab)
        addNew.setOnClickListener{
            val intent = Intent(this,EditActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    /**
     * Initializes the sort button. Reverses the starting order.
     */
    fun initSort(){
        val sort = findViewById<FloatingActionButton>(R.id.sort_list_fab)
        sort.setOnClickListener{
            noteList = noteList.asReversed()
            rv.adapter = NoteAdapter(noteList,this)
        }
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
