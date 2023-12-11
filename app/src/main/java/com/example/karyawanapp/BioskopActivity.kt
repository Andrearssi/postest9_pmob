package com.example.karyawanapp

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.karyawanapp.databinding.ActivityBioskopBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BioskopActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBioskopBinding
    private var url2: String=""
    private var name: String=""
    private lateinit var storage: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBioskopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvBioskop.layoutManager = LinearLayoutManager(this)
        binding.rvBioskop.setHasFixedSize(true)
        showData()
        tambahData()
    }

    private fun showData() {
        val dataRef = FirebaseDatabase.getInstance("https://postest9-14412-default-rtdb.firebaseio.com/").getReference("Tiket")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tiketList = mutableListOf<ModelBioskop>()
                val adapter = AdapterBioskop(tiketList)
                for (dataSnapshot in snapshot.children) {
                    val tiketKey = dataSnapshot.getValue(ModelBioskop::class.java)
                    tiketKey?.let {
                        tiketList.add(it)
                        Log.d("cek data firebase", it.toString())
                    }
                }

                if (tiketList.isNotEmpty()) {
                    binding.rvBioskop.adapter = adapter
                } else {
                    Toast.makeText(this@BioskopActivity, "Data not found", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(snapshot: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun tambahData(){
        val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://postest9-14412-default-rtdb.firebaseio.com/")
        val pekerjaanRef: DatabaseReference = database.getReference("Tiket")
        binding.fabAddTiket.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.upload_dialog, null)

            MaterialAlertDialogBuilder(this)
                .setTitle("Tambah Tiket")
                .setView(dialogView)
                .setPositiveButton("Tambah") { dialog, _ ->
                    val filmName = dialogView.findViewById<EditText>(R.id.editTextFilmName).text.toString()
                    val jadwal = dialogView.findViewById<EditText>(R.id.editTextJadwal).text.toString()
                    val studio = dialogView.findViewById<EditText>(R.id.editTextStudio).text.toString()
                    val filmData = HashMap<String, Any>()
                    filmData["nama"] = filmName
                    filmData["jadwal"] = jadwal
                    filmData["studio"] = studio
                    filmData["photoUrl"] = url2
                    name = filmName

                    val newPekerjaanRef = pekerjaanRef.push()
                    newPekerjaanRef.setValue(filmData)

                    dialog.dismiss()

                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }

                .show()

            dialogView.findViewById<Button>(R.id.btnUpload).setOnClickListener {
                choosePicture()
            }
        }
    }

    private val getMultipleContentsPicture =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null){
                uploadPic(uri)
            }
        }
    private fun choosePicture() {
        getMultipleContentsPicture.launch("image/*")
    }
    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }

        return System.currentTimeMillis().toString()
    }
    private fun uploadPic(uris: Uri) {
        val selectedFile = mutableListOf<String>()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Picture...")
        progressDialog.setCancelable(false)

        progressDialog.show()
        storage = FirebaseStorage.getInstance("gs://postest9-14412.appspot.com").getReference("Film")
        val fileName = getFileName(uris)
        selectedFile.add(fileName)
        val contentResolver = this.contentResolver
        val nama = name
        // Upload semua file ke Firebase Storage
        val picRef = storage.child("image/${nama}/${uris.lastPathSegment}")

        try {
            val inputStream = contentResolver.openInputStream(uris)
            picRef.putStream(inputStream!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Upload berhasil
                    progressDialog.dismiss()
                    picRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        url2 = downloadUrl

                    }.addOnFailureListener {
                    }
                    Log.d("TAG", "File uploaded to Firebase Storage")
                }
                .addOnFailureListener { exception ->
                    Log.e("TAG", "Upload failed", exception)
                }
        } catch (e: Exception) {
            Log.e("TAG", "Failed to open input stream", e)
        }

    }
}