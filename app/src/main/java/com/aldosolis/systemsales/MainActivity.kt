package com.aldosolis.systemsales

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aldosolis.systemsales.adapter.ProductAdapter
import com.aldosolis.systemsales.databinding.ActivityMainBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.util.UUID

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseDatabase:FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var productAdapter:ProductAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        productAdapter = ProductAdapter(ProductProvider.productList)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initRecyclerView()
        initFirebase()
        initUI()
        listData()


    }
    private fun initUI(){
        binding.customToolbar.btnScan.setOnClickListener {
            openScan()
        }
        binding.faVoucher.setOnClickListener {
            val totalPrecioXCantidad = binding.recyclerProducts.children.sumByDouble { itemView ->
                val priceXamount = itemView.findViewById<TextView>(R.id.tvProductPricexAmount)
                priceXamount.text.toString().toDoubleOrNull() ?: 0.0
            }
            Toast.makeText(this, "TOTAL A PAGAR: $totalPrecioXCantidad", Toast.LENGTH_SHORT).show()
        }
        binding.btnAddProduct.setOnClickListener {
            if(binding.etName.text.isEmpty()){
                Toast.makeText(this,"El campo nombre esta vacio",Toast.LENGTH_SHORT).show()
            }else if(binding.etPrice.text.isEmpty()){
                Toast.makeText(this,"El campo precio esta vacio",Toast.LENGTH_SHORT).show()
            }else if(binding.etAmount.text.isEmpty()){
                Toast.makeText(this,"El campo cantidad esta vacio",Toast.LENGTH_SHORT).show()
            }
            val product = Product()
            product.setId(UUID.randomUUID().toString())
            product.setName(binding.etName.text.toString())
            product.setPrice(binding.etPrice.text.toString())
            product.setAmount(binding.etAmount.text.toString())

            val myRef = databaseReference.child(product.getId())
            myRef.setValue(product)
            Toast.makeText(this,"Agregado exitosamente",Toast.LENGTH_SHORT).show()


        }

    }
    private fun listData(){
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    ProductProvider.productList.add(product!!)
                    binding.recyclerProducts.adapter?.notifyDataSetChanged()
                }
                //
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun initFirebase(){
        FirebaseApp.initializeApp(this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.setPersistenceEnabled(true)
        databaseReference = firebaseDatabase.getReference().child("products")

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
        if(result!=null){
            if(result.contents==null) {
                Toast.makeText(this, "LectorCancelada", Toast.LENGTH_LONG).show();
            }else{
                val product = Product(UUID.randomUUID().toString(),result.contents,"2.50","1")

                val myRef = databaseReference.child(product.getId())
                myRef.setValue(product)
                Toast.makeText(this,"Agregado exitosamente",Toast.LENGTH_SHORT).show()


            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun initRecyclerView(){
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = ProductAdapter(ProductProvider.productList)


    }
}