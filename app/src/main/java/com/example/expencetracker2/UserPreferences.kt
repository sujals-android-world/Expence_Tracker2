package com.example.expencetracker2

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val CATEGORIES_IDS_KEY = stringSetPreferencesKey("selected_categories_ids")
    }

    suspend fun saveCategoryIds(ids: List<Long>) {
        val stringSet = ids.map { it.toString() }.toSet()
        Log.d("DataStore","Saving pure set : $stringSet")
        context.dataStore.edit { prefs->
            prefs[CATEGORIES_IDS_KEY] = stringSet
        }
    }

    val selectedCategoryIds: Flow<List<Long>> = context.dataStore.data.map { prefs ->
        val stringSet = prefs[CATEGORIES_IDS_KEY] ?: emptySet()
        val list = stringSet.mapNotNull { it.toLongOrNull() }
        Log.d("DataStore","Loaded Data $list")
        list
    }
}