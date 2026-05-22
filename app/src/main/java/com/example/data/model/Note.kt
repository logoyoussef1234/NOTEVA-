package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false,
    val category: String = "All",
    val tags: String = "", // Comma separated tags (e.g. "Work,Idea")
    val imagePath: String? = null,
    val voicePath: String? = null,
    val voiceDuration: Long = 0L,
    val checklistJson: String? = null, // JSON representation of to-do checklist items [{text: "Item", checked: false}]
    val isLocked: Boolean = false,
    val lockPasscode: String? = null,
    val isSynced: Boolean = false
) : Serializable

data class ChecklistItem(
    val id: String,
    val text: String,
    val isChecked: Boolean
) {
    companion object {
        fun toJsonArray(items: List<ChecklistItem>): String {
            val array = org.json.JSONArray()
            for (item in items) {
                val obj = org.json.JSONObject()
                obj.put("id", item.id)
                obj.put("text", item.text)
                obj.put("isChecked", item.isChecked)
                array.put(obj)
            }
            return array.toString()
        }

        fun fromJsonArray(json: String?): List<ChecklistItem> {
            if (json.isNullOrBlank()) return emptyList()
            val list = mutableListOf<ChecklistItem>()
            try {
                val array = org.json.JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ChecklistItem(
                            id = obj.optString("id", System.currentTimeMillis().toString() + "_" + i),
                            text = obj.optString("text", ""),
                            isChecked = obj.optBoolean("isChecked", false)
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }
    }
}
