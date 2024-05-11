package com.aldosolis.systemsales

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.aldosolis.systemsales.Entidad.Cliente
import java.util.Locale

class CustomDialogComprobante(context: Context, private val listener: OnItemClickedListener) : Dialog(context) {
    private lateinit var listaClientes: ListView
    private lateinit var mAdapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comprobantes_layout)
        listaClientes= findViewById(R.id.listClientes)
        mAdapter = ArrayAdapter(context, R.layout.item_spinner_comprobante)
        listaClientes.adapter = mAdapter
        val nombreCliente = findViewById<EditText>(R.id.etNombreCliente)
        nombreCliente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No necesitas implementar nada aquí
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filtrar la lista según el texto ingresado
                val searchText = s.toString().toLowerCase(Locale.getDefault())
                val filteredList = ClientesProvider.clientesList .filter { item ->
                    item.nombreCli!!.toLowerCase(Locale.getDefault()).contains(searchText)
                }
                val nombresClientesFiltrados = filteredList.map { cliente ->
                    cliente.nombreCli!!
                }
                // Actualizar el adaptador del ListView con la lista filtrada
                val adapter = listaClientes.adapter as ArrayAdapter<String>
                adapter.clear()
                adapter.addAll(nombresClientesFiltrados)
                adapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {
                // No necesitas implementar nada aquí
            }
        })


        listaClientes.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = ClientesProvider.clientesList[position].nombreCli
            listener.onItemClicked(selectedItem.toString())
            dismiss()
        }

    }
    fun setClientes(clientes: MutableList<Cliente>) {
        mAdapter.clear()
        for (cliente in clientes) {
            mAdapter.add(cliente.nombreCli)
        }
        mAdapter.notifyDataSetChanged()
    }
    // Agrega esta función para notificar al adaptador que los datos han cambiado
    interface OnItemClickedListener {
        fun onItemClicked(item: String)
    }
}