package com.example.akillikampussg

import android.view.View
import android.widget.AdapterView

/**
 *SimpleItemSelectedListener
 *spinner seçimlerini daha temiz ve okunabilir şekilde yönetmek için yazılmış yardımcı listener sınıfıdır
 *özellikle Feed ekranındaki filtreleme kodunu sadeleştirir
 */
class SimpleItemSelectedListener(
    //seçim yapıldığında çalışacak fonksiyon
    private val onSelected: () -> Unit
) : AdapterView.OnItemSelectedListener {


     //spinner'da bir öğe seçildiğinde otomatik olarak çağrılır

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        onSelected()
    }


     //hiçbir şey seçilmediğinde çağrılır

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
