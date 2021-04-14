package no.uia.ikt205.project_1.todos.data

import kotlinx.android.parcel.Parcelize
import kotlin.collections.List
import com.google.gson.annotations.SerializedName

data class Item (@SerializedName("name") var name:String, @SerializedName("isDone") var isDone:Boolean)
data class Tasklist (@SerializedName("name") var name:String, @SerializedName("items") var items:MutableList<Item>){

    public fun getPercent():Float {
        var doneCount: Float = 0f
        if (this.items.size == 0){
            return 0f
        } else {
            this.items.forEach {
                if (it.isDone)
                    doneCount += 1f
            }
        }
        return doneCount / (this.items.size).toFloat()
    }
}