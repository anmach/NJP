package com.example.expiration.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.expiration.DBCommunicator
import com.example.expiration.DateManager
import com.example.expiration.R
import java.util.*

class ListFragment : Fragment() {
    var dbc: DBCommunicator? = null
    var listView: ListView? = null
    private var customAdapter: ListAdapter? = null
    val dateManager: DateManager? = DateManager()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.list_view)
        dbc = DBCommunicator(
                requireActivity().openOrCreateDatabase(
                        getString(R.string.database_name),
                        Context.MODE_PRIVATE,
                        null
                )
        )
        deleteOldProducts()
        createAdapter()

        view.findViewById<View>(R.id.fab_add)
            .setOnClickListener {
                NavHostFragment.findNavController(this@ListFragment)
                    .navigate(R.id.action_ListFragment_to_AddProductFragment)
            }
        view.findViewById<View>(R.id.fab_delete)
            .setOnClickListener { confirmDeleteAndDelete() }
    }

    override fun onResume() {
        super.onResume()
        createAdapter()
    }

    fun createAdapter() {
        customAdapter = ListAdapter()
        val expirationDates: ArrayList<String> = dbc!!.selectAllDatesWithOrder()
        var products: ArrayList<String>
        if (expirationDates.isEmpty()) {
            return
        }
        val dateManager = DateManager()
        for (date in expirationDates) {
            customAdapter!!.addSeparatorItem(dateManager.fromDBDateToDottedDMY(date))
            if(dbc != null) {
                products = dbc!!.selectProductNamesByDate(date)
                for (product in products) {
                    customAdapter!!.addItem(product)
                }
            }
        }
        listView!!.adapter = customAdapter
    }

    private fun deleteOldProducts() {
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        val date: String = dateManager!!.fromIntsToDBDate(day, month + 1, year)
        if(date != null){
            dbc?.deleteProductsBeforeDate(date)
        }
    }

    private fun deleteSelectedProducts() {
        val dateManager = DateManager()
        val toDelete =
            customAdapter!!.clickedNameWithDate
        for (product in toDelete) {
            val productName = product[0]
            val productDate: String = dateManager.fromDottedDMYToDBDate(product[1])
            dbc!!.deleteOneProductByNameAndDate(productName, productDate)
        }
        createAdapter()
    }

    private inner class ListAdapter : BaseAdapter() {
        private val clickedColour = Color.parseColor("#BF4545")
        private val unclickedColour = Color.parseColor("#FFFFFFFF")
        private val listItems = ArrayList<String>()
        private val isItemClicked = ArrayList<Boolean>()
        private val inflater: LayoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private val separatorsSet = TreeSet<Int>()

        private val TYPE_ITEM = 0
        private val TYPE_SEPARATOR = 1
        private val TYPE_MAX_COUNT = TYPE_SEPARATOR + 1

        fun addItem(item: String?) {
            listItems.add(item!!)
            isItemClicked.add(false)
            notifyDataSetChanged()
        }

        fun addSeparatorItem(item: String) {
            listItems.add(item)
            isItemClicked.add(false)
            separatorsSet.add(listItems.size - 1)
            notifyDataSetChanged()
        }

        private fun getSeparatorAboveItem(itemPosition: Int): String {
            val separator = ""
            var iter = itemPosition
            while (iter >= 0) {
                if (getItemViewType(iter) == TYPE_SEPARATOR) {
                    return getItem(iter)
                }
                iter--
            }
            return separator
        }

        override fun getItemViewType(position: Int): Int {
            return if (separatorsSet.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
        }

        override fun getViewTypeCount(): Int {
            return TYPE_MAX_COUNT
        }

        override fun getCount(): Int {
            return listItems.size
        }

        val clickedNameWithDate: ArrayList<Array<String>>
            get() {
                val duos =
                    ArrayList<Array<String>>()
                var iter = 0
                for (isClicked in isItemClicked) {
                    if (isClicked) {
                        val name = getItem(iter)
                        val date = getSeparatorAboveItem(iter)
                        duos.add(arrayOf(getItem(iter), getSeparatorAboveItem(iter)))
                    }
                    iter++
                }
                return duos
            }

        override fun getItem(position: Int): String {
            return listItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var convView = convertView
            var holder: ViewHolder? = null
            val type = getItemViewType(position)
            if (convView == null) {
                holder = ViewHolder()
                when (type) {
                    TYPE_ITEM -> {
                        convView = inflater.inflate(R.layout.list_item, null)
                        holder.textView =
                            convView.findViewById<View>(R.id.text) as TextView
                        holder.textView!!.setOnClickListener(
                            ListOnClickListener(
                                position
                            )
                        )
                    }
                    TYPE_SEPARATOR -> {
                        convView = inflater.inflate(R.layout.list_header, null)
                        holder.textView =
                            convView.findViewById<View>(R.id.textHeader) as TextView
                    }
                }
                if (convView != null) {
                    convView.tag = holder
                }
            } else {
                holder = convView.tag as ViewHolder
                if (type == TYPE_ITEM) {
                    holder.textView!!.setOnClickListener(null)
                    holder!!.textView!!.setOnClickListener(
                        ListOnClickListener(
                            position
                        )
                    )
                    if (isItemClicked[position]) {
                        holder.textView!!.setBackgroundColor(clickedColour)
                    } else {
                        holder.textView!!.setBackgroundColor(unclickedColour)
                    }
                }
            }
            holder!!.textView!!.text = listItems[position]
            return convView
        }

        private inner class ListOnClickListener internal constructor(private val pos: Int) :
            View.OnClickListener {
            override fun onClick(view: View) {
                val viewColor = view.background as ColorDrawable
                if (viewColor.color == clickedColour) {
                    view.setBackgroundColor(unclickedColour)
                    isItemClicked[pos] = false
                } else {
                    view.setBackgroundColor(clickedColour)
                    isItemClicked[pos] = true
                }
            }

        }

    }

    class ViewHolder {
        var textView: TextView? = null
    }

    private fun confirmDeleteAndDelete() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz wykonać akcję")
        builder.setPositiveButton(
            "Tak"
        ) { dialog, which -> deleteSelectedProducts() }
        builder.setNegativeButton(
            "Nie"
        ) { dialog, which -> }
        val dialog = builder.create()
        dialog.show()
    }
}