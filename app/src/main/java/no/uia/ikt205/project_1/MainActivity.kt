package no.uia.ikt205.project_1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import no.uia.ikt205.project_1.todos.data.Tasklist
import no.uia.ikt205.project_1.todos.TasklistCollectionAdapter
import no.uia.ikt205.project_1.todos.TasklistDepositoryManager
import no.uia.ikt205.project_1.todos.TasklistDetailsActivity
import no.uia.ikt205.project_1.todos.data.Item
import project_1.R
import project_1.databinding.ActivityMainBinding
import java.util.Collections.emptyList
import kotlinx.android.synthetic.main.activity_main.view.*


const val EXTRA_TASK_INFO: String = "no.uia.ikt205.project_1.book.info"
const val REQUEST_TASK_DETAILS:Int = 1

class TaskHolder{

    companion object{
        var pickedTasklist: Tasklist? = null
    }
}



class MainActivity : AppCompatActivity() {

    private val TAG:String = "Todo_App:MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        signInAnonymously()

        binding.taskListing.layoutManager = LinearLayoutManager(this)
        binding.taskListing.adapter = TasklistCollectionAdapter(emptyList<Tasklist>(), this::onTasklistClicked, this::onTasklistDelete)

        TasklistDepositoryManager.instance.onTasklists = {
            (binding.taskListing.adapter as TasklistCollectionAdapter).updateCollection(it)
        }

        //TasklistDepositoryManager.instance.load(getString(R.string.book_listing_url),this)


        binding.newTaskBtn.setOnClickListener {

            addTasklist("test")

            val ipm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            ipm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        }

        binding.syncMenu.setOnClickListener {

            val popup = PopupMenu(this@MainActivity, binding.syncMenu)
            popup.menuInflater.inflate(R.menu.sync_menu, popup.menu)

            popup.setOnMenuItemClickListener{
                when(it.itemId){
                    R.id.save ->{
                        TasklistDepositoryManager.instance.saveTasks(TasklistDepositoryManager.instance.getTasks(), getExternalFilesDir(null), "${userId}.json")
                    }

                    R.id.Upload -> {
                        TasklistDepositoryManager.instance.saveTasks(TasklistDepositoryManager.instance.getTasks(), getExternalFilesDir(null), "${userId}.json")
                        TasklistDepositoryManager.instance.upload(getExternalFilesDir(null), "${userId}.json")
                    }

                    R.id.Download -> {
                        TasklistDepositoryManager.instance.download(getExternalFilesDir(null), "${userId}.json")
                    }
                }
                true
            }
            popup.show()
        }
    }

    private fun addTasklist(name: String) {

        val tasklist = Tasklist(name, mutableListOf(Item("Item 1", false)))
        TasklistDepositoryManager.instance.addTask(tasklist)

    }

    private fun deleteTasklist(tasklist:Tasklist) {
        TasklistDepositoryManager.instance.deleteTasklist(tasklist)
    }

    private fun onTasklistDelete(tasklist: Tasklist) {
        deleteTasklist(tasklist)
    }

    private fun onTasklistClicked(tasklist: Tasklist): Unit {

        /*val intent =Intent(this, BookDetailsActivity::class.java).apply {
            putExtra(EXTRA_BOOK_INFO, book)
        }*/

        TaskHolder.pickedTasklist = tasklist

        val intent =Intent(this, TasklistDetailsActivity::class.java)

        startActivity(intent)
        startActivityForResult(intent, REQUEST_TASK_DETAILS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_TASK_DETAILS){
            (binding.root.taskListing.adapter as TasklistCollectionAdapter).refresh()
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener {
            Log.d(TAG, "Login success ${it.user.toString()}")
            userId = it.user.uid.toString()
        }
            .addOnFailureListener{
                Log.e(TAG, "Login failed", it)
            }
    }

}


