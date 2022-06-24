package ukdw.ac.id.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ukdw.ac.id.myapplication.Model.Buku
import ukdw.ac.id.myapplication.R
import java.io.File

class adapter(private val data: ArrayList<Buku>):
    RecyclerView.Adapter<adapter.ViewHolder>() {

    private val mFirestore = FirebaseFirestore.getInstance()
    lateinit var ctx: Context
    lateinit var buku: Buku
    lateinit var adapter: adapter

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.daftar_buku, parent, false)
        ctx = v.context
        buku = Buku()
        this.adapter = this
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data[position])


        //val storageReference = Firebase.storage.reference.child(data[position].gambar+".jpg")
        val storageReference = FirebaseStorage.getInstance().reference.child(data[position].gambar)
        val localfile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            Glide.with(holder.itemView)
                .asBitmap()
                .load(bitmap)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        holder.img.setImageBitmap(resource)
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }.addOnFailureListener {

        }

        buku = data[position]

        holder.lnBuku.setOnClickListener {

        }

        holder.btnUbah.setOnClickListener {
            fg_ubah(
                position,
                data[position].id,
                data[position].gambar,
                data[position].judul,
                data[position].penulis,
                data[position].penerbit,
                data[position].tahun,
                data[position].halaman,
            )
        }

        holder.btnHapus.setOnClickListener {
            fg_hapus(position,data[position].id)
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvPengarang: TextView = itemView.findViewById(R.id.tvPengarang)
        val tvPenerbit: TextView = itemView.findViewById(R.id.tvPenerbit)
        val tvTahun: TextView = itemView.findViewById(R.id.tvTahun)
        val tvHalaman: TextView = itemView.findViewById(R.id.tvHalaman)
        val lnBuku: RelativeLayout = itemView.findViewById(R.id.lnBuku)

        val btnUbah: Button = itemView.findViewById(R.id.btnUbah)
        val btnHapus: Button = itemView.findViewById(R.id.btnHapus)

        val img: ImageView = itemView.findViewById(R.id.imgBuku)

        fun bindItems(f: Buku) {
            tvJudul.text = f.judul
            tvPengarang.text = f.penulis
            tvPenerbit.text = f.penerbit
            tvTahun.text = f.tahun
            tvHalaman.text = f.halaman
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fg_ubah(idx: Int,id: String, gambar: String, judul: String, pengarang: String, penerbit: String, tahun: String, halaman: String) {
        val dialog = AlertDialog.Builder(ctx)
        val v = LayoutInflater.from(ctx).inflate(R.layout.fg_ubah,null)
//        val dialogView = inflater.inflate(R.layout.fg_ubah, null)
        dialog.setView(v)

        val txtJudul = v.findViewById(R.id.txtJudul_ubah) as TextView
        txtJudul.text = judul

        val txtPengarang = v.findViewById(R.id.txtPenulis_ubah) as TextView
        txtPengarang.text = pengarang

        val txtPenerbit = v.findViewById(R.id.txtPenerbit_ubah) as TextView
        txtPenerbit.text = penerbit

        val txtTahun = v.findViewById(R.id.txtTahun_ubah) as TextView
        txtTahun.text = tahun

        val txtHalaman = v.findViewById(R.id.txtHalaman_ubah) as TextView
        txtHalaman.text = halaman

        val yesBtn = v.findViewById(R.id.btnUbah) as Button
        val noBtn = v.findViewById(R.id.btnBatal) as TextView

        val b = dialog.create()
        b.show()

        yesBtn.setOnClickListener {
            mFirestore.collection("buku").document(id).update(
                mapOf(
                    "gambar" to gambar,
                    "judul" to txtJudul.text.toString(),
                    "penulis" to txtPengarang.text.toString(),
                    "penerbit" to txtPenerbit.text.toString(),
                    "tahun" to txtTahun.text.toString(),
                    "halaman" to txtHalaman.text.toString()
                )
            ).addOnSuccessListener { documentReference ->
//                data.remove(buku); //Actually change your list of items here
//                data[idx] = buku
                data[idx] = Buku(
                    id,
                    gambar,
                    txtHalaman.text.toString(),
                    txtJudul.text.toString(),
                    txtPenerbit.text.toString(),
                    txtPengarang.text.toString(),
                    txtTahun.text.toString()
                )
                adapter.notifyDataSetChanged()
                Toast.makeText(ctx, "Sukses ubah data!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    ctx,
                    "Error : ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            b.dismiss()
        }
        noBtn.setOnClickListener {
            b.dismiss()
        }




    }

    @SuppressLint("NotifyDataSetChanged")
    fun fg_hapus(idx: Int, id: String){
        val builder = AlertDialog.Builder(ctx)
            .setTitle("Hapus Data")
            .setMessage("Yakin mau hapus?")
            .setPositiveButton("YA"){dialog, which ->
                mFirestore.collection("buku").document(id)
                    .delete()
                    .addOnCompleteListener {
                        data.removeAt(idx)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(ctx, "Sukses Hapus data", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { Toast.makeText(ctx, "Gagal Hapus data", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Tidak", null)
        builder.create().show()
    }

}