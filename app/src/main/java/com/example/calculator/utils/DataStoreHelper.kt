package com.example.calculator.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.calculator.model.HistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "calculator_history")

object DataStoreHelper {
    private val HISTORY_KEY = stringPreferencesKey("history")
    private val gson = Gson()

    suspend fun saveHistory(context: Context, historyList: List<HistoryItem>) {
        val json = gson.toJson(historyList)
        context.dataStore.edit { prefs ->
            prefs[HISTORY_KEY] = json
        }
    }

    suspend fun loadHistory(context: Context): List<HistoryItem> {
        return context.dataStore.data
            .map { prefs ->
                val json = prefs[HISTORY_KEY]
                if (json.isNullOrBlank()) emptyList()
                else {
                    val type = object : TypeToken<List<HistoryItem>>() {}.type
                    gson.fromJson<List<HistoryItem>>(json, type)
                }
            }.first()
    }
}
