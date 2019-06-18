package com.ternovyi.musicplayer

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {

    private var player: MediaPlayerService? = null
    var serviceBound = false
    private var audioList: ArrayList<Audio>? = null

    private lateinit var collapsingImageView: ImageView

    private var imageIndex = 0

    //Binding this Client to the AudioPlayer Service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaPlayerService.LocalBinder
            player = binder.service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        collapsingImageView = findViewById(R.id.collapsingImageView)

        loadCollapsingImage(imageIndex)

        if (checkAndRequestPermissions()) {
            loadAudioList()
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener(View.OnClickListener {
            //                playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
            //play the first audio in the ArrayList
            //                playAudio(2);
            if (imageIndex == 4) {
                imageIndex = 0
                loadCollapsingImage(imageIndex)
            } else {
                loadCollapsingImage(++imageIndex)
            }
        })
    }

    private fun loadAudioList() {
        loadAudio()
        initRecyclerView()
    }

    private fun checkAndRequestPermissions(): Boolean {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            val permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            val permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val listPermissionsNeeded = ArrayList<String>()

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
            }

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            return if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toTypedArray(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                false
            } else {
                true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        val mTag = "LOG_PERMISSION"
        Log.d(mTag, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions

                    if (perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(mTag, "Phone state and storage permissions granted")
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        loadAudioList()
                    } else {
                        Log.d(mTag, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_PHONE_STATE
                            )
                        ) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE -> {
                                        }
                                    }// proceed with logic by disabling the related features or quit the app.
                                })
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show()
                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }


    private fun initRecyclerView() {
        if (audioList != null && audioList!!.size > 0) {
            val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
            val adapter = RecyclerViewAdapter(audioList!!, application)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.addOnItemTouchListener(CustomTouchListener(this, object : OnItemClickListener {
                override fun onClick(view: View, index: Int) {
                    playAudio(index)
                }
            }))
        }
    }

    private fun loadCollapsingImage(i: Int) {
        val array = resources.obtainTypedArray(R.array.images)
        collapsingImageView.setImageDrawable(array.getDrawable(i))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("serviceStatus", serviceBound)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("serviceStatus")
    }


    private fun playAudio(audioIndex: Int) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            val storage = StorageUtil(applicationContext)
            storage.storeAudio(audioList!!)
            storage.storeAudioIndex(audioIndex)

            val playerIntent = Intent(this, MediaPlayerService::class.java)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            //Store the new audioIndex to SharedPreferences
            val storage = StorageUtil(applicationContext)
            storage.storeAudioIndex(audioIndex)

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            val broadcastIntent = Intent(BROADCAST_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    /**
     * Load audio files using [ContentResolver]
     *
     * If this don't works for you, load the audio files to audioList Array your oun way
     */
    private fun loadAudio() {
        val contentResolver = contentResolver

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        if (cursor != null && cursor.count > 0) {
            audioList = ArrayList()
            while (cursor.moveToNext()) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                // Save to audioList
                audioList!!.add(Audio(data, title, album, artist))
            }
        }
        cursor?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            player!!.stopSelf()
        }
    }

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        const val BROADCAST_PLAY_NEW_AUDIO = "com.ternovyi.musicplayer.PlayNewAudio"
    }
}

