package ukdw.ac.id.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import ukdw.ac.id.myapplication.Adapter.adapter
import ukdw.ac.id.myapplication.Model.Buku
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG: String = javaClass.simpleName
    lateinit var fbAdd: FloatingActionButton
    lateinit var dataBuku: ArrayList<Buku>
    lateinit var tempBuku: ArrayList<Buku>
    lateinit var rvBuku: RecyclerView
    lateinit var txtCari: EditText
    lateinit var btnCari: Button
    lateinit var progressDialog: ProgressDialog

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        fbAdd = findViewById(R.id.fbAdd)
        rvBuku = findViewById(R.id.rvBuku)
        txtCari = findViewById(R.id.txtCari)
        btnCari = findViewById(R.id.btnCari)

        fbAdd.setOnClickListener { startActivity(Intent(this@MainActivity, Input::class.java)) }

        val db = FirebaseFirestore.getInstance()

        progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setTitle("Tunggu sebentar")
        progressDialog.setMessage("Sedang Mengambil data...")
        progressDialog.show()

        val handler = Handler()
        handler.postDelayed({
            readData(db)
        }, 2000)

        btnCari.setOnClickListener {
//            db.collection("buku")
//                .orderBy("judul")
//                .startAt(txtCari.text.toString())
//                .endAt(txtCari.text.toString()+"\uf8ff")
//                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    querySnapshot?.documents?.toString()?.let { it1 -> Log.e("CEK ", it1) }
//                }
            tempBuku.clear()
            val keyword = txtCari.text.toString().toLowerCase(Locale.getDefault())
            if (keyword.isNotEmpty()){
                dataBuku.forEach {
                    if(it.judul.toLowerCase(Locale.getDefault()).contains(keyword)){
                        tempBuku.add(it)
                    }
                }
                rvBuku.adapter?.notifyDataSetChanged()
            }else{
                tempBuku.clear()
                tempBuku.addAll(dataBuku)
                rvBuku.adapter?.notifyDataSetChanged()
            }

        }

    }



    fun readData(db: FirebaseFirestore) {

        dataBuku = ArrayList()
        tempBuku = ArrayList()
        dataBuku.clear()
        tempBuku.clear()
        rvBuku.adapter = null
        db.collection("buku").get()
            .addOnSuccessListener { result ->
                for (document in result){
//                    Log.d(TAG, "Datanya : ${document.id} => ${document.data.get("gambar")}")
                    dataBuku.add(
                        Buku(
                            document.id,
                            document.data["gambar"] as String,
                            document.data["halaman"] as String,
                            document.data["judul"] as String,
                            document.data["penerbit"] as String,
                            document.data["penulis"] as String,
                            document.data["tahun"] as String
                        )
                    )
                }
                tempBuku.addAll(dataBuku)
                val adapter = adapter(tempBuku)
                rvBuku.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
                rvBuku.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents : $exception")
            }
        progressDialog.hide()
    }


}