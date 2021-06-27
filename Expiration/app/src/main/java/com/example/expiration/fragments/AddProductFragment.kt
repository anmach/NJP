package com.example.expiration.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.expiration.*
import java.util.*

class AddProductFragment : Fragment() {
    private var productName: String? = ""
    private var date: String? = ""
    private var dbc: DBCommunicator? = null
    private var diaFragment: DialogFragment? = null
    private var dateManager: DateManager = DateManager()
    private var nameEditText: EditText? = null
    private var dateEditText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        dbc = DBCommunicator(
                requireActivity().openOrCreateDatabase(
                        getString(R.string.database_name),
                        Context.MODE_PRIVATE,
                        null
                )
        )
        prepareEditTexts(view)

        view.findViewById<View>(R.id.button_add)
            .setOnClickListener {
                val name = nameEditText!!.text.toString()
                val date = dateEditText!!.text.toString()
                val dbDate: String =
                    dateManager.fromDottedDMYToDBDateWithFutureDateCheck(date)
                val toast: Toast
                toast = if (dbDate !== "") {
                    addProduct(name, dbDate)
                    Toast.makeText(
                        requireContext().applicationContext,
                        "Dodano nowy produkt",
                        Toast.LENGTH_SHORT
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Błąd: niepoprawna data",
                        Toast.LENGTH_SHORT
                    )
                }
                toast.show()
            }
        view.findViewById<View>(R.id.fabCalendar)
            .setOnClickListener { view -> showDatePickerDialog(view) }
        view.findViewById<View>(R.id.fabFile)
            .setOnClickListener { chooseFile() }
    }

    private fun addProduct(name: String, dbDate: String) {
        dbc!!.insertProduct(name, dbDate)
    }

    private fun addProducts(nameDatePairs: ArrayList<Array<String>>) {
        var noOfProducts = 0
        for (pair in nameDatePairs) {
            addProduct(pair[0], pair[1])
            noOfProducts++
        }
        val toast = Toast.makeText(
            context,
            "Zaimportowano produkty w liczbie: $noOfProducts",
            Toast.LENGTH_SHORT
        )
        toast.show()
    }

    private fun showDatePickerDialog(view: View) {
        diaFragment!!.show(requireActivity().supportFragmentManager, "datePicker")
    }

    private fun chooseFile() {
        val intent: Intent
        val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "text/plain"
        intent = Intent.createChooser(chooseFile, "Wybierz plik")
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            val uri = data!!.data
            val filePath = uri!!.path
            val csvFile = CSVFile(filePath!!)
            addProducts(csvFile.readProductDataPairs())
        }
    }

    private fun prepareEditTexts(view: View) {
        nameEditText = view.findViewById<View>(R.id.editTextName) as EditText
        nameEditText!!.setText(productName)
        dateEditText = view.findViewById<View>(R.id.editTextDate) as EditText
        diaFragment = DatePicker(dateEditText)
    }

    companion object {
        private const val ACTIVITY_CHOOSE_FILE = 1
    }
}