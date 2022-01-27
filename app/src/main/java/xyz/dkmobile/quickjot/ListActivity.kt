package xyz.dkmobile.quickjot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.dkmobile.quickjot.databinding.ActivityListBinding


/**
 * An activity which shows all notes in a two column, selectable list.
 */
class ListActivity : AdapterView.OnItemSelectedListener, AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private var noteList: List<NoteEntity> = ArrayList()
    private lateinit var db: AppDatabase

    var list =
        arrayOf("All", "General", "TO-DO", "Shopping", "Idea", "Goals", "Read / Watch", "Remember")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        this.supportActionBar!!.hide()

        //Database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "notes.db"
        ).build()

        binding.recyclerViewNotes.layoutManager = GridLayoutManager(this, 2)
        val emptyList = arrayListOf<NoteEntity>()
        binding.recyclerViewNotes.adapter = NoteAdapter(emptyList, this)
        initListeners()
        if (savedInstanceState == null) {
            addNotes(this)
        } else {
            val category = savedInstanceState.getCharSequence("category")
            val index = list.indexOf(category)
            binding.categoryListSpinner.setSelection(index)
            sortByCategory(index)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val category = binding.categoryListSpinner.selectedItem.toString()
        outState.putCharSequence("category", category)
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
        if (position != 0) {
            sortByCategory(position)
        } else {
            addNotes(this)
        }
    }

    /**
     * This version sorts by making a new query.
     */
    private fun sortByCategory(position: Int) {
        val text: String = list[position]

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                noteList = if (position == 0) {
                    db.noteDao().getAll()
                } else {
                    db.noteDao().getByCategory(text)
                }
            }
            binding.recyclerViewNotes.adapter = NoteAdapter(noteList, this@ListActivity)
        }
    }

    /**
     * Initialize the listeners.
     */
    private fun initListeners() {
        initSpinner()
        initSort()
        initAddNew()
    }

    /**
     * User clicks the 'Add New' button and the edit activity is launched.
     */
    private fun initAddNew() {
        val addNew = findViewById<FloatingActionButton>(R.id.add_new_fab)
        addNew.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    /**
     * Initializes the sort button. Reverses the starting order.
     */
    private fun initSort() {
        val sort = findViewById<FloatingActionButton>(R.id.sort_list_fab)
        sort.setOnClickListener {
            noteList = noteList.asReversed()
            binding.recyclerViewNotes.adapter = NoteAdapter(noteList, this)
        }
    }

    /**
     * Initializes the category spinner.
     */
    private fun initSpinner() {
        binding.categoryListSpinner.onItemSelectedListener = this
        val arrayAdapter = ArrayAdapter(this, R.layout.list_spinner_selected_item, list)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.categoryListSpinner.adapter = arrayAdapter
    }

    /**
     * Queries the database for all notes to populate the activity.
     * Then sets the recycler view adapter and layout manager.
     */
    private fun addNotes(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                noteList = db.noteDao().getAll()
            }
            binding.recyclerViewNotes.adapter = NoteAdapter(noteList, context)
            binding.recyclerViewNotes.layoutManager = GridLayoutManager(context, 2)
        }
    }
}
