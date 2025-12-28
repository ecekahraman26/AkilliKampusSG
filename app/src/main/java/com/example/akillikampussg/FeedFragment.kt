package com.example.akillikampussg

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *FeedFragment
 *ana sayfadaki bildirim akışı ekranıdır
 *kullanıcı burada
 *bildirimleri liste halinde görür
 *arama yapabilir (başlık açıklama)
 *tür filtresi uygulayabilir
 *sadece açık olanları görebilir
 *sadece takip ettiklerini görebilir
 *admin ayrıca kendi birimine ait bildirimleri filtreleyebilir yetkim bölümünde sıralama (yeni→eski / eski→yeni) yapabilir
 */
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var adapter: NotificationAdapter

    //sıralama: true = yeni→eski, false = eski→yeni
    private var sortNewestFirst = true

    //filtreleme
    private var etSearch: EditText? = null
    private var spType: Spinner? = null
    private var cbOnlyOpen: CheckBox? = null
    private var cbFollowed: CheckBox? = null
    private var cbAdminArea: CheckBox? = null
    private var btnSort: Button? = null

    //admin kontrolü
    private var isAdmin: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvFeed)

        //UI componentlerini bağla
        etSearch = view.findViewById(R.id.etSearch)
        spType = view.findViewById(R.id.spType)
        cbOnlyOpen = view.findViewById(R.id.cbOnlyOpen)
        cbFollowed = view.findViewById(R.id.cbFollowed)
        cbAdminArea = view.findViewById(R.id.cbAdminArea)
        btnSort = view.findViewById(R.id.btnSort)


         //admin ise yetkim filtresi görünür kullanıcı ise gizli kalır

        isAdmin = DataStore.currentUser?.role == "ADMIN"
        cbAdminArea?.visibility = if (isAdmin) View.VISIBLE else View.GONE

        /**
         *spinner için tür listesini üretir
         *base sabit türler
         */
        fun getTypes(): List<String> {
            val dynamic = DataStore.items.map { it.type }.distinct()
            val base = listOf("Tümü", "Güvenlik", "Temizlik", "Arıza", "Genel", "Kayıp-Buluntu")

            //tekilleştirme?
            return (listOf("Tümü") + (base.drop(1) + dynamic).distinct().filter { it != "Tümü" })
        }

        fun refreshSpinner() {
            val types = getTypes()
            spType?.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                types
            )
        }

        refreshSpinner()

        /**
         *recyclerView Adapter
         *liste elemanına tıklanınca bottomSheet ile detay ekranı açılır
         */
        adapter = NotificationAdapter(DataStore.items) { item ->
            NotificationDetailBottomSheet.show(parentFragmentManager, item.id)
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter


         //sıralama butonu Yeni→Eski / Eski→Yeni arasında geçiş sağlar

        btnSort?.text = "Sırala: Yeni → Eski"
        btnSort?.setOnClickListener {
            sortNewestFirst = !sortNewestFirst
            btnSort?.text = if (sortNewestFirst) "Sırala: Yeni → Eski" else "Sırala: Eski → Yeni"
            applyFilters()
        }

        //filtre değiştikçe listeyi güncelle
        etSearch?.addTextChangedListener { applyFilters() }
        cbOnlyOpen?.setOnCheckedChangeListener { _, _ -> applyFilters() }
        cbFollowed?.setOnCheckedChangeListener { _, _ -> applyFilters() }
        cbAdminArea?.setOnCheckedChangeListener { _, _ -> applyFilters() }
        spType?.onItemSelectedListener = SimpleItemSelectedListener { applyFilters() }

        //ilk açılışta filtreleri uygula
        applyFilters()
    }


     //detay ekranından geri dönünce takip durum değişmiş olabilir ve listeyi yeniler

    override fun onResume() {
        super.onResume()
        applyFilters()
    }


     //arama, filtre, yetki filtresi, sıralama hepsini birlikte uygulayan ana fonksiyon

    private fun applyFilters() {
        val q = etSearch?.text?.toString()?.trim()?.lowercase() ?: ""
        val selectedType = spType?.selectedItem?.toString() ?: "Tümü"
        val onlyOpen = cbOnlyOpen?.isChecked ?: false
        val onlyFollowed = cbFollowed?.isChecked ?: false
        val onlyAdminArea = (cbAdminArea?.isChecked ?: false) && isAdmin

        val meEmail = DataStore.currentUser?.email ?: ""

        //filtreleme
        val filtered = DataStore.items.filter { n ->

            //başlık veya açıklama içinde arama
            val matchSearch =
                q.isEmpty() || n.title.lowercase().contains(q) || n.desc.lowercase().contains(q)

            //tür filtresi
            val matchType =
                (selectedType == "Tümü") || n.type.equals(selectedType, ignoreCase = true)

            //sadece açık olanlar
            val matchOpen =
                !onlyOpen || n.status == "Açık"

            //sadece takip edilenler
            val matchFollowed =
                !onlyFollowed || (meEmail.isNotEmpty() && n.followers.contains(meEmail))

            //admin için yetkim filtresi birimleri göz önüne alarak
            val myUnit = DataStore.currentUser?.unit?.trim()?.lowercase() ?: ""
            val matchAdmin =
                !onlyAdminArea || (myUnit.isNotEmpty() && n.unit.trim().lowercase() == myUnit)

            matchSearch && matchType && matchOpen && matchFollowed && matchAdmin
        }

        //kronolojik sıralama (yeni→eski veya eski→yeni)
        val sorted = if (sortNewestFirst) {
            filtered.sortedByDescending { it.createdAt }
        } else {
            filtered.sortedBy { it.createdAt }
        }

        //adapter güncelle
        if (::adapter.isInitialized) {
            adapter.update(sorted)
        }
    }
}
