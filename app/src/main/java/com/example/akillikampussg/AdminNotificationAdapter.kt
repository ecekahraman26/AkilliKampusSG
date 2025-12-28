package com.example.akillikampussg

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * AdminNotificationAdapter
 * admin bildirimin detaylarını görür durumunu değiştirir
 * ve yanlış veya uygunsuz bildirimi sistemden kaldırabilir
 */
class AdminNotificationAdapter(

    //sistemdeki tüm bildirimler DataStoreitems den gelir
    private val items: MutableList<NotificationItem>,

    //bildirim durumu değiştiğinde tetiklenen kısım
    //bildirim atmak takipçileri uyarmak ve benzeri şeyler
    private val onStatusChanged: (item: NotificationItem, oldStatus: String, newStatus: String) -> Unit

) : RecyclerView.Adapter<AdminNotificationAdapter.VH>() {

    //admin tarafından seçilebilecek durumlar
    private val statuses = listOf("Açık", "İnceleniyor", "Çözüldü")


     //item_admin_notification.xml içindeki viewleri tutan kısım

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvATitle)
        val tvType: TextView = v.findViewById(R.id.tvAType)
        val tvDesc: TextView = v.findViewById(R.id.tvADesc)
        val tvCreator: TextView = v.findViewById(R.id.tvACreator)
        val tvUnit: TextView = v.findViewById(R.id.tvAUnit)
        val tvLocation: TextView = v.findViewById(R.id.tvALocation)
        val tvTime: TextView = v.findViewById(R.id.tvATime)
        val spStatus: Spinner = v.findViewById(R.id.spStatus)
        val btnTerminate: Button = v.findViewById(R.id.btnTerminate)
    }


     //yeni satır oluşturulur

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_notification, parent, false)
        return VH(v)
    }


     //kaç adet bildirim gösterileceği
    override fun getItemCount(): Int = items.size


    //her bir bildirimin ekrana bağlandığı yer

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = items[position]

        //bildirim bilgileri ekrana yazılır
        holder.tvTitle.text = item.title
        holder.tvType.text = "Tür: ${item.type}"
        holder.tvDesc.text = item.desc
        holder.tvCreator.text = "Kullanıcı: ${item.creatorEmail}"
        holder.tvUnit.text = "Birim: ${item.unit}"
        holder.tvLocation.text = "Konum: %.5f, %.5f".format(item.lat, item.lng)
        holder.tvTime.text =
            "Zaman: ${android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", item.createdAt)}"

        //spinner ayarları
        val spinnerAdapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_dropdown_item,
            statuses
        )
        holder.spStatus.adapter = spinnerAdapter

        //mevcut durum spinnerda seçili gelsin
        val currentIndex = statuses.indexOf(item.status).coerceAtLeast(0)
        holder.spStatus.setSelection(currentIndex, false)


         //admin bildirimin durumunu değiştirdiğinde çalışır

        holder.spStatus.onItemSelectedListener = SimpleItemSelectedListener {
            val selected = holder.spStatus.selectedItem.toString()
            if (item.status != selected) {
                val old = item.status
                item.status = selected

                //admin panelinden fragmente haber ver
                onStatusChanged(item, old, selected)
            }
        }


         //yanlış veya uygunsuz bildirimi tamamen sonlandırma

        holder.btnTerminate.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Bildirimi sonlandır?")
                .setMessage("Bu bildirim yanlış veya uygunsuz ise sistemden kaldırılacaktır. Devam edilsin mi?")
                .setPositiveButton("Evet") { _, _ ->

                    val idx = holder.adapterPosition
                    if (idx != RecyclerView.NO_POSITION) {

                        //bildirimi listeden kaldır
                        items.removeAt(idx)

                        //recyclerView güncelle
                        notifyItemRemoved(idx)
                        notifyItemRangeChanged(idx, items.size)
                    }
                }
                .setNegativeButton("Vazgeç", null)
                .show()
        }
    }
}
