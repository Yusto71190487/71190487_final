package ukdw.ac.id.myapplication.Model

class Buku {
    var id: String = ""
    var gambar: String = ""
    var halaman: String = ""
    var judul: String = ""
    var penerbit: String = ""
    var penulis: String = ""
    var tahun: String = ""

    constructor()

    constructor(
        gambar: String,
        halaman: String,
        judul: String,
        penerbit: String,
        penulis: String,
        tahun: String
    ) {
        this.gambar = gambar
        this.halaman = halaman
        this.judul = judul
        this.penerbit = penerbit
        this.penulis = penulis
        this.tahun = tahun
    }

    constructor(
        id: String,
        gambar: String,
        halaman: String,
        judul: String,
        penerbit: String,
        penulis: String,
        tahun: String
    ) {
        this.id = id
        this.gambar = gambar
        this.halaman = halaman
        this.judul = judul
        this.penerbit = penerbit
        this.penulis = penulis
        this.tahun = tahun
    }


}