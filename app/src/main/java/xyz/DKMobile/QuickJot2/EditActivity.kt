package xyz.DKMobile.QuickJot2

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        val userText = edittext.text
        val category = spinner.selectedItem.toString()
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = list[position]
    }

    //User clicks Yes to delete dialog message
    fun deletePositiveClick() {
        if(editState){
            GlobalScope.launch(Dispatchers.IO){
                var note = NoteEntity(uid,spinner.selectedItem.toString(),edittext.text.toString())
                db.noteDao().delete(note)
                clearText()
            }
        } else {
            clearText()
        }
    }

    fun clearText() {
        edittext.setText("")
        uid = 0
        spinner.setSelection(0)
        editState = false
    }

    fun initListeners() {
        initSpinner()
        initList()
        initDelete()
        initSave()
    }

    fun initSpinner() {
        spinner.setOnItemSelectedListener(this)
        val array_adapter = ArrayAdapter(this,R.layout.spinner_selected_item,list)
        array_adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.setAdapter(array_adapter)
    }

    fun initList() {
        val listButton = findViewById<FloatingActionButton>(R.id.list_view_fab)
        listButton.setOnClickListener {
            val intent = Intent(this,ListActivity::class.java)
            startActivity(intent)
        }
    }

    fun initDelete() {
        val deleteButton = findViewById<FloatingActionButton>(R.id.delete_fab)
        deleteButton.setOnClickListener {
            showDeleteDialog()
        }
    }

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


    // Extension function to show toast message
    fun Context.toast(message: String) {
        var toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,0,480)
        toast.show()
    }

    fun doNothing() {}

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
