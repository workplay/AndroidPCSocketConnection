package com.example.androidpcsocketconnection


import android.net.LocalServerSocket
import android.net.LocalSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.DataOutputStream


class MainActivity : AppCompatActivity() {
    var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        serverThread = ServerThread()
        serverThread!!.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThread!!.isLoop = false
    }


    inner class ToastMessageHandler: Handler() {
        override fun handleMessage(msg: Message) {
            Toast.makeText(applicationContext, msg.getData().getString("MSG", "Toast"), Toast.LENGTH_SHORT).show()
        }
    }

    inner class ServerThread : Thread() {
        var isLoop = true

        val TAG = "ServerThread"

        private val handler: ToastMessageHandler = ToastMessageHandler()

        override fun run() {
            Log.d(TAG, "running")
            var serverSocket: LocalServerSocket? = null
            try {
                serverSocket = LocalServerSocket("localServer")
                while (isLoop) {
                    val socket: LocalSocket = serverSocket.accept()
                    Log.d(TAG, "accept")
                    val inputStream = DataInputStream(socket.inputStream)
                    val outputStream = DataOutputStream(socket.outputStream)

                    val msg: String = inputStream.readUTF()
                    val bundle = Bundle().apply { putString("MSG", msg) }
                    val message = Message.obtain().apply { data = bundle }

                    handler.sendMessage(message)
                    socket.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                Log.d(TAG, "destory")
                if (serverSocket != null) {
                    try {
                        serverSocket.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


}