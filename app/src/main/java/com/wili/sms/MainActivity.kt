package com.wili.sms

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.random.Random.Default.nextInt

class MainActivity : AppCompatActivity() {
    var otpkirim = ""
    var nohpkirim = ""
    var list_isisms :ArrayList<String> = ArrayList()
    var list_nohp :ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        send.setOnClickListener {
            if (this.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.SEND_SMS)
                }
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    999)

            } else {
                sendSMS()
            }


        }

        cek.setOnClickListener {
            if (this.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.READ_SMS)
                }
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_SMS),
                    998)

            } else {
                ceksms()
            }
        }
    }

    fun ceksms(){
        val otpnya = otp.text.toString()
        val cursor: Cursor? = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            null,
            null,
            null
        )

        if (cursor?.moveToFirst()!!) { // must check the result to prevent exception
            do {
                var msgData = ""
                var isisms = ""
                list_isisms.clear()
                list_nohp.clear()
                    for (idx in 0 until cursor.getColumnCount()) {
//                    msgData += " " + cursor.getColumnName(idx).toString() + ":" + cursor.getString(
//                        idx
//                    )
//                    println("isi sms dari : "+cursor.getString(cursor
//                        .getColumnIndexOrThrow("address"))+" isi => "+ cursor.getString(cursor.getColumnIndexOrThrow("body")))

                    if(cursor.getString(cursor.getColumnIndexOrThrow("body")).contains("sms message otepe ",true)){
                        list_isisms.add(cursor.getString(cursor.getColumnIndexOrThrow("body")))
                        list_nohp.add(cursor.getString(cursor.getColumnIndexOrThrow("address")))
                        println("otep : "+cursor.getString(cursor.getColumnIndexOrThrow("address")))
                    }
                }

                if(otpkirim==otpnya){
                    var hasil = false
                    for (i in list_isisms){
                        if(i.contains(otpnya)){
                            if(list_nohp[list_isisms.indexOf(i)].contains(nohpkirim)){
                                Toast.makeText(this,"OTEPE berhasil",Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(this,"no hp salah",Toast.LENGTH_LONG).show()
                            }
                            hasil = true
                            println("otep : "+list_nohp[list_isisms.indexOf(i)])
                        }
                    }
                    if(!hasil){
                        Toast.makeText(this,"no hp salah",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,"OTEPE salah",Toast.LENGTH_LONG).show()
                }

                // use msgData
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
        }
    }

    fun sendSMS()
    {
        nohpkirim = nohp.text.toString()
        var otp = (1..999999).shuffled().first()
        println("otep "+otp)
        otpkirim = otp.toString()

        val smsManager = SmsManager.getDefault() as SmsManager
        smsManager.sendTextMessage(nohpkirim, null, "sms message otepe "+otp, null, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            999 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendSMS()
                } else {
                    Toast.makeText(this,"Permintaan ijin ditolak",Toast.LENGTH_LONG).show()
                }
                return
            }
            998 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    ceksms()
                } else {
                    Toast.makeText(this,"Permintaan ijin ditolak",Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

}