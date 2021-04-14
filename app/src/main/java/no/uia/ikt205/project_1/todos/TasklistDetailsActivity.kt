package no.uia.ikt205.project_1.todos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.inputmethod.InputMethodManager
import no.uia.ikt205.project_1.TaskHolder
import no.uia.ikt205.project_1.EXTRA_TASK_INFO
import no.uia.ikt205.project_1.todos.data.Item
import no.uia.ikt205.project_1.todos.data.Tasklist
import project_1.databinding.ActivityTaskDetailsBinding
import java.util.Collections.emptyList
import kotlin.math.roundToInt

class ItemHolder{
    companion object{
        var PickedItem: Item? = null
    }
}

class TasklistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding
    private lateinit var tasklist: Tasklist


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemListing.layoutManager = LinearLayoutManager(this)
        binding.itemListing.adapter = ItemCollectionAdapter(emptyList<Item>(), this::onItemChecked, this::onItemDelete)

        ItemDepositoryManager.instance.onItems = {
            (binding.itemListing.adapter as ItemCollectionAdapter).updateCollection(it)
        }

        val receivedTasklist = TaskHolder.pickedTasklist

        if(receivedTasklist != null){
            tasklist = receivedTasklist
            Log.i("Details view", receivedTasklist.toString())
        } else{

            setResult(RESULT_CANCELED, Intent(EXTRA_TASK_INFO).apply {

            })

            finish()
        }

        ItemDepositoryManager.instance.load(tasklist, this)

        binding.headerTitle.text = tasklist.name
        binding.taskProgress.progress = (tasklist.getPercent() * 100).roundToInt()

        binding.newItemBtn.setOnClickListener {
            addItem("New Item")

            val ipm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            ipm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.taskProgress.progress = (tasklist.getPercent() * 100).roundToInt()
        }
    }

    private fun addItem(name:String) {

        val item = Item(name, false)
        ItemDepositoryManager.instance.addItem(item)
    }

    private fun deleteItem(item: Item) {
        ItemDepositoryManager.instance.deleteItem(item)
    }

    private fun onItemDelete(item: Item) {
        deleteItem(item)
        binding.taskProgress.progress = (tasklist.getPercent() * 100).roundToInt()
    }

    private fun onItemChecked(): Unit {

        binding.taskProgress.progress = (tasklist.getPercent() * 100).roundToInt()

        /*val intent =Intent(this, BookDetailsActivity::class.java).apply {
            putExtra(EXTRA_BOOK_INFO, book)
        }*/

        //ItemHolder.PickedItem = item

        //val intent = Intent(this, IListDetailsActivity::class.java)

        //startActivity(intent)
        //startActivityForResult(intent, REQUEST_BOOK_DETAILS)
    }
}