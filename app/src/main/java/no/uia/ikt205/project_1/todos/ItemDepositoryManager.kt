package no.uia.ikt205.project_1.todos

import android.content.Context
import com.android.volley.RequestQueue
import no.uia.ikt205.project_1.todos.data.Item
import no.uia.ikt205.project_1.todos.data.Tasklist

class ItemDepositoryManager {

    private lateinit var ItemColection: MutableList<Item>
    private lateinit var queue: RequestQueue

    var onItems: ((List<Item>) -> Unit)? = null
    var onItemUpdate: ((item:Item) -> Unit)? = null


    fun load(tasklist: Tasklist, context: Context) {
        ItemColection = tasklist.items

        onItems?.invoke(ItemColection)
    }

    fun updateItem(item: Item) {
        onItemUpdate?.invoke(item)
    }

    fun addItem(item: Item) {
        ItemColection.add(item)
        onItems?.invoke(ItemColection)
    }

    fun deleteItem(item: Item) {
        ItemColection.remove(item)
        onItems?.invoke(ItemColection)
    }

    companion object {
        val instance = ItemDepositoryManager()
    }

}