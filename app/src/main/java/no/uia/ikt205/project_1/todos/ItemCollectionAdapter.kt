package no.uia.ikt205.project_1.todos

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.item_layout.view.*
import android.view.inputmethod.InputMethodManager
import kotlin.collections.List
import no.uia.ikt205.project_1.todos.data.Item
import no.uia.ikt205.project_1.todos.data.Tasklist
import project_1.databinding.ItemLayoutBinding
import java.sql.DriverManager.println
import kotlin.math.roundToInt


class ItemCollectionAdapter(private var Items:List<Item>, private val onItemChecked:() -> Unit, private  val onItemDelete:(item:Item) -> Unit) : RecyclerView.Adapter<ItemCollectionAdapter.ViewHolder>(){

    class ViewHolder(val binding: ItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item, onItemChecked:() -> Unit, onItemDelete: (item: Item) -> Unit) {
            binding.itemName.setText(item.name)
            binding.itemCheckBox.isChecked = item.isDone

            binding.itemName.setOnEditorActionListener{ v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    item.name = v.text.toString()

                    val ipm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    ipm.hideSoftInputFromWindow(v?.windowToken, 0)

                    true
                }
                else {
                    false
                }
            }


            binding.itemCheckBox.setOnClickListener{
                item.isDone = it.itemCheckBox.isChecked
                onItemChecked()
            }

            binding.delItemBtn.setOnClickListener{
                onItemDelete(item)
            }
        }
    }

    override fun getItemCount(): Int = Items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Items[position]
        holder.bind(item, onItemChecked, onItemDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    public fun updateCollection(newItems:List<Item>){
        Items = newItems
        notifyDataSetChanged()
    }


}