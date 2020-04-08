package xyz.DKMobile.QuickJot

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * The activity where a note is being created/edited. Can categorize, save, delete,
 * and launch the list activity.
 *
 * TODO decide if launching list or hitting back with an edit should prompt an 'are you sure?' dialog
 */
class EditActivity : AdapterView.OnItemSelectedListener, AppCompatActivity() {
    lateinit var edittext: EditText
    lateinit var spinner: Spinner
    lateinit var selection: String
    lateinit var db: AppDatabase

    var list = arrayOf("General","TO-DO","Shopping","Idea","Goals","Read / Watch","Remember")
    var editState = false
    var uid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        this.supportActionBar!!.hide()

        edittext = findViewById(R.id.editText)
        spinner = findViewById(R.id.categorySpinner)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "notes.db"
        ).build()

        initListeners()

        //Get uid from intent and check if it's valid
        val intentUID = intent.getIntExtra("uid",-1)
        //If editing existing note from database
        if(intentUID > -1){
            setEditConditions(intentUID)
        }
    }

    /**
     * After the listeners have been initialized, set the member variables to the saved values.
     */
    fun setEditConditions(intentUID: Int){
        uid = intentUID
        editState = true
        var savedCategory = intent.getStringExtra("category")
        var spinnerIndex = list.indexOf(savedCategory)
        spinner.setSelection(spinnerIndex)
        var text = intent.getStringExtra("text")
        edittext.setText(text)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        val userText = edittext.text
        //Don't need category?
        //val category = spinner.selectedItem.toString()
        val saveEditState = editState
        val saveUID = uid

        outState?.putCharSequence("savedText",userText)
        outState?.putBoolean("editState",saveEditState)
        outState?.putInt("uid",saveUID)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        val userText = savedInstanceState?.getCharSequence("savedText")
        edittext.setText(userText)
        uid = savedInstanceState!!.getInt("uid")
        editState = savedInstanceState!!.getBoolean("editState")
    }

    /**
     * (Spinner)
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * (Spinner)
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = list[position]
    }

    /**
     * User clicks Yes to delete dialog message. Deletes from database, or simply clears the screen
     * if it hasn't been stored yet.
     */
    fun deletePositiveClick() {
        if(editState){
            GlobalScope.launch(Dispatchers.IO){
                var note = NoteEntity(uid,spinner.selectedItem.toString(),edittext.text.toString())
                db.noteDao().delete(note)
            }
        } else {}
        clearText()
    }

    /**
     * Reset the activity to a blank state with the first spinner item selected.
     */
    fun clearText() {
        edittext.setText("")
        uid = 0
        spinner.setSelection(0)
        editState = false
    }

    /**
     * Initialize the various listeners.
     */
    fun initListeners() {
        initSpinner()
        initList()
        initDelete()
        initSave()
    }

    /**
     * Initialize the category spinner.
     */
    fun initSpinner() {
        spinner.setOnItemSelectedListener(this)
        val array_adapter = ArrayAdapter(this,R.layout.spinner_selected_item,list)
        array_adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.setAdapter(array_adapter)
    }

    /**
     * Clicking the list view fab starts the List View Activity.
     */
    fun initList() {
        val listButton = findViewById<FloatingActionButton>(R.id.list_view_fab)
        listButton.setOnClickListener {
            val intent = Intent(this,ListActivity::class.java).apply {
                //addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
            startActivity(intent)
        }
    }

    /**
     * Clicking the delete fab starts a call the the delete dialog.
     */
    fun initDelete() {
        val deleteButton = findViewById<FloatingActionButton>(R.id.delete_fab)
        deleteButton.setOnClickListener {
            showDeleteDialog()
        }
    }

    /**
     * Clicking the save button causes the note to be saved.
     * Either as a new database entry or as an update to an existing one.
     */
    fun initSave() {
        val saveButton = findViewById<FloatingActionButton>(R.id.save_note_fab)
        saveButton.setOnClickListener {
            if(edittext.text.toString() != ""){
                val note: NoteEntity
                if(!editState){
                    note = NoteEntity(0,spinner.selectedItem.toString(),edittext.text.toString())
                    uid = note.uid
                    GlobalScope.launch(Dispatchers.IO){
                        var longUID = db.noteDao().insert(note)
                        uid = longUID.toInt()
                    }
                    toast("Note Saved!")
                    editState = true
                } else {
                    note = NoteEntity(uid,spinner.selectedItem.toString(),edittext.text.toString())
                    GlobalScope.launch(Dispatchers.IO){
                        db.noteDao().update(note)
                    }
                    toast("Note Updated!")
                }
                clearText()
            } else {
                toast("Nothing to save!")
            }
        }
    }


    /**
     * Extension function to show toast message.
     */
    fun Context.toast(message: String) {
        var toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,480)
        toast.show()
    }

    fun doNothing() {}

    /**
     * Creates and shows an alert dialog to verify if user wants to delete the note.
     */
    fun showDeleteDialog() {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this,R.style.DialogTheme)

        // Set a title for alert dialog
        builder.setTitle("Delete this note?")

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> deletePositiveClick()
                DialogInterface.BUTTON_NEGATIVE -> doNothing()
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO ",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
        //Set button colors
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#4d648d"))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#d0e1f9"))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1e1f26"))
    }
}
