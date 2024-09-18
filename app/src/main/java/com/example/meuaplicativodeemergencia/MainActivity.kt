package com.example.meuaplicativodeemergencia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnSamu: Button
    private lateinit var btnPolicia: Button
    private lateinit var btnContatoEmergencia: Button
    private lateinit var btnEnviarLocalizacao: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnSamu = findViewById(R.id.btnSamu)
        btnPolicia = findViewById(R.id.btnPolicia)
        btnContatoEmergencia = findViewById(R.id.btnContatoEmergencia)
        btnEnviarLocalizacao = findViewById(R.id.btnEnviarLocalizacao)

        btnSamu.setOnClickListener { fazerChamada("192") }
        btnPolicia.setOnClickListener { fazerChamada("190") }
        btnContatoEmergencia.setOnClickListener {
            val numeroContato = obterContatoEmergencia()
            if (numeroContato.isNotEmpty()) {
                fazerChamada(numeroContato)
            } else {
                Toast.makeText(this, "Configure o contato de emergência nas configurações.", Toast.LENGTH_SHORT).show()
            }
        }
        btnEnviarLocalizacao.setOnClickListener { enviarLocalizacao() }
    }

    // Método para fazer chamadas
    private fun fazerChamada(numero: String) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            return
        }
        startActivity(intent)
    }

    // Método para obter o contato de emergência (vamos implementar no próximo passo)
    private fun obterContatoEmergencia(): String {
        // TODO: Implementar lógica para obter o contato de emergência das configurações
        return "5551999999999"
    }

    // Método para enviar a localização
    private fun enviarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val numeroContato = obterContatoEmergencia()
                    if (numeroContato.isNotEmpty()) {
                        enviarSMS(numeroContato, "Minha localização: https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                    } else {
                        Toast.makeText(this, "Configure o contato de emergência nas configurações.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Método para enviar SMS
    private fun enviarSMS(numero: String, mensagem: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 3)
            return
        }
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(numero, null, mensagem, null, null)
            Toast.makeText(this, "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao enviar mensagem.", Toast.LENGTH_SHORT).show()
        }
    }
}