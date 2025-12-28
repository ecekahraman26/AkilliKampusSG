package com.example.akillikampussg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit

/**
 *NotificationAdapter
 *akış ekranında bildirimleri listelemek için kullanılan adapter
 *her satırda
 *tür ikonu
 *başlık
 *açıklama
 *oluşturulma zamanı
 *durum açık inceleniyor ve çözüldü gösterilir
 *liste elemanına tıklanınca detay ekranı açılır
 */
class NotificationAdapter(
    private var items: List<NotificationItem>,
    private val onClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.VH>() {

    /**
     * ViewHolder
     *item_notification.xml içindeki viewleri burada tutuyoruz
     *RecyclerView performansı için gereklidir
     */
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivType: ImageView = v.findViewById(R.id.ivType)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvDesc: TextView = v.findViewById(R.id.tvDesc)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
    }


     //her yeni satır için layout item_notification.xml

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return VH(v)
    }


     //toplam eleman sayısı

    override fun getItemCount(): Int = items.size


     //veriyi UI'a bağlama işlemi

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        //metin alanlarını doldur
        holder.tvTitle.text = item.title
        holder.tvDesc.text = item.desc
        holder.tvStatus.text = item.status
        holder.tvTime.text = timeAgo(item.createdAt)

        /**
         *tür ikonları
         *her bildirim türü farklı ikon ile gösterilir
         */
        holder.ivType.setImageResource(
            when (item.type.lowercase()) {
                "güvenlik" -> android.R.drawable.ic_lock_lock
                "arıza" -> android.R.drawable.ic_dialog_alert
                "temizlik" -> android.R.drawable.ic_menu_delete
                else -> android.R.drawable.ic_dialog_info
            }
        )


         //satıra tıklanınca detay ekranı açılması için click çağrılır

        holder.itemView.setOnClickListener { onClick(item) }
    }

    /**
     *filtre sıralama sonrası liste değiştiğinde adapter güncellenir
     *FeedFragment içinde güncelleme çağrılıyor
     */
    fun update(newItems: List<NotificationItem>) {
        items = newItems
        notifyDataSetChanged()
    }


     //zamanı kaç dk-saat-gün önce şeklinde kullanıcı dostu gösterir

    private fun timeAgo(time: Long): String {
        val diff = System.currentTimeMillis() - time
        val min = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hour = TimeUnit.MILLISECONDS.toHours(diff)
        val day = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            min < 1 -> "Az önce"
            min < 60 -> "$min dk önce"
            hour < 24 -> "$hour saat önce"
            else -> "$day gün önce"
        }
    }
}
