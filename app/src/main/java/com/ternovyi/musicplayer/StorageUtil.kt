package com.ternovyi.musicplayer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageUtil(private var context: Context) {

    private val mStorage = " com.ternovyi.musicplayer.mStorage"
    private var preferences: SharedPreferences? = null

    fun storeAudio(arrayList: ArrayList<Audio>) {
        preferences = context.getSharedPreferences(mStorage, Context.MODE_PRIVATE)

        val editor = preferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(arrayList)
        editor.putString("audioArrayList", json)
        editor.apply()
    }

    fun loadAudio(): ArrayList<Audio> {
        preferences = context.getSharedPreferences(mStorage, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences!!.getString("audioArrayList", null)
        val type = object : TypeToken<ArrayList<Audio>>() {}.type
        return gson.fromJson(json, type)
    }

    fun storeAudioIndex(index: Int) {
        preferences = context.getSharedPreferences(mStorage, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt("audioIndex", index)
        editor.apply()
    }

    fun loadAudioIndex(): Int {
        preferences = context.getSharedPreferences(mStorage, Context.MODE_PRIVATE)
        return preferences!!.getInt("audioIndex", -1)//return -1 if no data found
    }

    fun clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(mStorage, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.clear()
        editor.apply()
    }
}