package com.aldosolis.systemsales

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aldosolis.systemsales.Datos.ConexionFirebase
import com.aldosolis.systemsales.Datos.Procedimientos
import com.aldosolis.systemsales.Entidad.Cliente
import com.aldosolis.systemsales.Entidad.Comprobante
import com.aldosolis.systemsales.Entidad.DetalleVenta
import com.aldosolis.systemsales.Entidad.Inventario
import com.aldosolis.systemsales.Entidad.Ventas
import com.aldosolis.systemsales.adapter.ProductAdapter
//import com.aldosolis.systemsales.adapter.ProductAdapter
import com.aldosolis.systemsales.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var conexionFirebase:ConexionFirebase


    var listaComprobantesFirebase = ArrayList<Comprobante>()
    var listaVentasFirebase = ArrayList<Ventas>()
    var listaClientesFirebase = ArrayList<Cliente>()

    lateinit var mAdapter:ArrayAdapter<String>

    lateinit var inventario: Inventario
    lateinit var comprobante: Comprobante
    lateinit var ventas: Ventas
    lateinit var cliente: Cliente
    lateinit var productAdapter:ProductAdapter

    lateinit var procedimientos: Procedimientos

    var totalRegistrosVentas:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        conexionFirebase = ConexionFirebase(this)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        productAdapter = ProductAdapter(DetalleVentasTemporalProvider.detalleVentasList)
        procedimientos = Procedimientos()


        conexionFirebase.cargarDatosInventario()
        conexionFirebase.cargarDatosProductos()
        conexionFirebase.cargarDatosClientes()
        conexionFirebase.cargarDatosComprobantes()
        conexionFirebase.cargarDatosVentas()

        initUI()
        initRecyclerView()



        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            0, // No se manejan movimientos de arrastre
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Se manejan deslizamientos hacia la izquierda y hacia la derecha
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // No se manejan movimientos de arrastre, por lo que se devuelve falso
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    DetalleVentasTemporalProvider.detalleVentasList.removeAt(position)
                    binding.recyclerProducts.adapter?.notifyItemRemoved(position)
                }

            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerProducts)


    }
    private fun initUI(){
        binding.customToolbar.btnScan.setOnClickListener {
            openScan()
        }
        binding.faVoucher.setOnClickListener {
            val customDialog = CustomDialog(this@MainActivity,conexionFirebase,binding.recyclerProducts)
            customDialog.show()
            var Total  = customDialog.findViewById<TextView>(R.id.tvMontoTotal)
            var cardView = customDialog.findViewById<CardView>(R.id.cardView)
            var cardView1 = customDialog.findViewById<CardView>(R.id.cardView1)
            var cardViewImg = customDialog.findViewById<CardView>(R.id.cardViewImg)

            Total.setText(sumarMontoTotal())
            customDialog.window!!.setBackgroundDrawable(getDrawable(R.drawable.gris_background))
            cardView.setBackgroundResource(R.drawable.white_background)
            cardView1.setBackgroundResource(R.drawable.white_background)
            cardViewImg.setBackgroundResource(R.drawable.white_background)


        }
    }

    private fun openScan(){
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Lector - CDP")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?):Unit {
        val result:IntentResult=IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "LectorCancelada", Toast.LENGTH_LONG).show()
            } else {
                var codigoExtraido=result.contents.substring(0, result.contents.indexOf("/"))
                binding.etResultado.setText(codigoExtraido)

                // Buscar el producto con el código extraído en la lista de inventario
                for (inventario in InventarioProvider.inventarioList) {
                    var detalleVenta = DetalleVenta()
                    // Verificar si ya existe un detalle de venta para este producto
                    val detalleExistente = DetalleVentasTemporalProvider.detalleVentasList.find { it.codigoPro == inventario.codigoPro }

                    if (detalleExistente == null && inventario.codigoPro == codigoExtraido) {

                        val detalleVenta = DetalleVenta().apply {
                            codigoPro = inventario.codigoPro
                            fotoPro = inventario.fotoPro
                            nombrePro = inventario.nombrePro
                            presentacionPro = procedimientos.encontrarPresentacionPorCodigo(inventario.codigoPro!!)
                            cantidad = inventario.cantidad
                            precioVentaPro = inventario.precioVentaPro
                            subTotalPro = (inventario.cantidad!!.toDouble() * inventario.precioVentaPro!!.toDouble()).toString()
                        }

                        DetalleVentasTemporalProvider.detalleVentasList.add(detalleVenta)
                        binding.recyclerProducts.adapter?.notifyDataSetChanged()


                    }

                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun sumarMontoTotal():String{
        // Variable para almacenar la suma total de montos
        var Total = 0.0

// Iterar sobre cada elemento en el RecyclerView
        for (i in 0 until binding.recyclerProducts.childCount) {
            // Obtener la vista de cada elemento en el RecyclerView
            val view = binding.recyclerProducts.getChildAt(i)

            // Encontrar el TextView que muestra el monto total en cada elemento
            val montoTotalTextView = view.findViewById<TextView>(R.id.tvPrecioXcantidad)

            // Obtener el valor del monto total de este elemento y sumarlo al totalAmount
            val montoTotal = montoTotalTextView.text.toString().toDoubleOrNull() ?: 0.0
            Total += montoTotal
        }
        val decimalFormat = DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))
        val montoTotalFormateado = decimalFormat.format(Total)
        return montoTotalFormateado.toString()
    }
    private fun initRecyclerView(){
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = productAdapter


    }


}