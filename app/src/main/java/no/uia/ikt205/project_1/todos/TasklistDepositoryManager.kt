package no.uia.ikt205.project_1.todos

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.android.volley.RequestQueue
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.uia.ikt205.project_1.MainActivity
import kotlin.collections.List
import no.uia.ikt205.project_1.todos.data.Item
import no.uia.ikt205.project_1.todos.data.Tasklist
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class TasklistDepositoryManager {

    private val TAG:String = "Todo_App:IListDepositoryManager"

    private var tasklistColection: MutableList<Tasklist> = mutableListOf(Tasklist("New Task", mutableListOf(Item("New Item", false))))
    private lateinit var queue: RequestQueue

    var onTasklists: ((List<Tasklist>) -> Unit)? = null
    var onTasklistUpdate: ((tasklist: Tasklist) -> Unit)? = null

    fun saveTasks(tasks: MutableList<Tasklist>, path:File?, fileName:String) {

        val outputJson: String = Gson().toJson(tasks)

        if (path != null) {
           val file = File(path, fileName)
           file.delete()
           FileOutputStream(file, true).bufferedWriter().use { writer ->
               writer.write(outputJson)
           }
            Log.d(TAG, "File saved")
        }
        else {
            Log.e(TAG, "Error")
        }
    }

    fun load(path:File?, fileName:String) {

      lateinit var inputJson:String

      if (path != null) {
          val file = File(path, fileName)
          if (file.canRead()) {
              inputJson = file.inputStream().bufferedReader().use(BufferedReader::readText)
              val listType = object : TypeToken<MutableList<Tasklist>>() {}.type
              tasklistColection = Gson().fromJson<MutableList<Tasklist>>(inputJson, listType)
              Log.d(TAG, "File loaded")
          }
      }
      else {
          // Error
      }
        onTasklists?.invoke(tasklistColection)
    }

    fun upload(path: File?, fileName: String) {
        val file:Uri = File(path, fileName).toUri()

        Log.d(TAG, "${file} Uploading")

        val ref = FirebaseStorage.getInstance().reference.child("todos/${file.lastPathSegment}")
        var uploadTask = ref.putFile(file)

        uploadTask.addOnSuccessListener {
            Log.d(TAG, "${it.toString()} Uploaded")
        }
            .addOnFailureListener{
                Log.e(TAG, "Error when trying to save file to Firebase")
            }
    }

    fun download(path: File?, fileName: String) {
        val file:Uri = File(path, fileName).toUri()

        Log.d(TAG, "Downloading ${file}")

        val ref = FirebaseStorage.getInstance().reference.child("todos/${file.lastPathSegment}")

        ref.getFile(file)
            .addOnSuccessListener {
                Log.d(TAG, "${it.toString()} Downloaded")
                this.load(path, fileName)
            }

            .addOnFailureListener{
                Log.e(TAG, "Error accured trying to download file from Firebase")
            }

            .addOnProgressListener { taskSnapshot ->

            }
    }

    fun getTasks():MutableList<Tasklist>{
        return tasklistColection
    }

    fun updateTask(tasklist: Tasklist) {
        onTasklistUpdate?.invoke(tasklist)
    }

    fun addTask(tasklist: Tasklist) {
        tasklistColection.add(tasklist)
        onTasklists?.invoke(tasklistColection)
    }

    fun deleteTasklist(tasklist: Tasklist){
        tasklistColection.remove(tasklist)
        onTasklists?.invoke(tasklistColection)
    }

    companion object {
        val instance = TasklistDepositoryManager()
    }

}