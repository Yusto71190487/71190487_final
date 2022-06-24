package ukdw.ac.id.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ukdw.ac.id.myapplication.Model.Buku
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class Input : AppCompatActivity() {
    private val TAG: String = javaClass.simpleName

    lateinit var btnGallery: Button
    lateinit var btnInput: Button
    lateinit var txtJudul: EditText
    lateinit var txtPenerbit: EditText
    lateinit var txtPenulis: EditText
    lateinit var txtTahun: EditText
    lateinit var txtHalaman: EditText
    lateinit var img: ImageView
    lateinit var tvLoading: TextView

    // Konfigurasi FireStore & Storage
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        supportActionBar?.hide()

        btnGallery = findViewById(R.id.btnGallery)
        btnInput = findViewById(R.id.btnInput)
        txtJudul = findViewById(R.id.txtJudul)
        txtPenerbit = findViewById(R.id.txtPenerbit)
        txtPenulis = findViewById(R.id.txtPenulis)
        txtTahun = findViewById(R.id.txtTahun)
        txtHalaman = findViewById(R.id.txtHalaman)
        img = findViewById(R.id.img)
        tvLoading = findViewById(R.id.tvLoading)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btnGallery.setOnClickListener { launchGallery() }

        btnInput.setOnClickListener {
            inputBuku()
        }

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                img.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun inputBuku(){
//        Thread {
        tvLoading.visibility = View.VISIBLE
        if (filePath != null) {

            val getname = UUID.randomUUID().toString()
            val ref = storageReference?.child("" + getname)
            ref?.putFile(filePath!!)

            val db = FirebaseFirestore.getInstance()

            val user = hashMapOf(
                "judul" to txtJudul.text.toString(),
                "penerbit" to txtPenerbit.text.toString(),
                "penulis" to txtPenulis.text.toString(),
                "tahun" to txtTahun.text.toString(),
                "halaman" to txtHalaman.text.toString(),
                "gambar" to getname
            )
            db.collection("buku").add(user)
                .addOnSuccessListener { documentReference ->
                    tvLoading.visibility = View.GONE
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this, "Sukses tambah Buku!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    tvLoading.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Error adding document : ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            tvLoading.visibility = View.GONE
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
        startActivity(Intent(this@Input, MainActivity::class.java))
//        }.start()
    }
}