package no.uia.ikt205.project_1.todos

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import no.uia.ikt205.project_1.todos.data.Tasklist
import project_1.databinding.TasklistLayoutBinding
import kotlin.collections.List
import kotlin.math.roundToInt


class TasklistCollectionAdapter(private var tasklists:List<Tasklist>, private val onTasklistClicked:(Tasklist) -> Unit, private val onTasklistDelete:(Tasklist) -> Unit) : RecyclerView.Adapter<TasklistCollectionAdapter.ViewHolder>(){

    class ViewHolder(val binding: TasklistLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(tasklist: Tasklist, onTasklistClicked:(Tasklist) -> Unit, onTasklistDelete: (Tasklist) -> Unit) {
            binding.taskName.setText(tasklist.name)

            binding.taskProgress.progress = (tasklist.getPercent() * 100).roundToInt()

            binding.card.setOnClickListener {
                onTasklistClicked(tasklist)
            }

            binding.taskName.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tasklist.name = v.text.toString()

                    val ipm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    ipm.hideSoftInputFromWindow(v?.windowToken, 0)

                    true
                }
                else {
                    false
                }
            }

           binding.deleteTaskBtn.setOnClickListener{
               onTasklistDelete(tasklist)
           }
        }
    }

    override fun getItemCount(): Int = tasklists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tasklist = tasklists[position]
        holder.bind(tasklist,onTasklistClicked, onTasklistDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(TasklistLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    public fun updateCollection(newTasklists:List<Tasklist>){
        tasklists = newTasklists
        notifyDataSetChanged()
    }

    public fun refresh(){
        notifyDataSetChanged()
    }
}