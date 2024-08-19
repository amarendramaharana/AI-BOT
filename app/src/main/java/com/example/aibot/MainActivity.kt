package com.example.aibot

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions

import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aibot.Adapter.Dao.AiDao
import com.example.aibot.Adapter.Database.AIDatabase
import com.example.aibot.Adapter.Modelclass.AIResponse
import com.example.aibot.Adapter.Repo.AIRepo
import com.example.aibot.Adapter.ResultAdapter
import com.example.aibot.Adapter.ViewModel.AIModel
import com.example.aibot.Adapter.ViewModel.DrawerAdapter
import com.example.aibot.Adapter.utill.OnItemClickListner
import com.example.aibot.ViewModelFactory.AIModelFactory
import com.example.aibot.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class MainActivity : AppCompatActivity(), OnItemClickListner {
    private lateinit var resultAdapter: ResultAdapter
    private lateinit var drawerAdapter: DrawerAdapter
    private lateinit var mainBinding: ActivityMainBinding
    private val list: MutableList<Message> = ArrayList()
    private val drawerList: MutableList<AIResponse> = ArrayList()
    private var imageBitmap: Bitmap? = null
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var generativeModel: GenerativeModel
    private lateinit var aiRepo: AIRepo
    private lateinit var aiDao: AiDao
    private lateinit var aiDatabase: AIDatabase
    private lateinit var aiViewModel: AIModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        aiDatabase = AIDatabase.getInstance(this)
        aiDao = aiDatabase.getAiDao()
        aiRepo = AIRepo(aiDao)
        initializeGenerativeModel()
        initializeViewmodel()
        initializeRecyclerview()
        initializeDrawerRecyclerview()
        registerLauncher()
        onClickListener()


    }

    private fun initializeDrawerRecyclerview() {
        drawerAdapter = DrawerAdapter(drawerList, this)
        mainBinding.drawerRcv.layoutManager = LinearLayoutManager(this)
        mainBinding.drawerRcv.adapter = drawerAdapter
    }

    private fun initializeViewmodel() {
        val factory = AIModelFactory(application, aiRepo)
        aiViewModel = ViewModelProvider(this, factory)[AIModel::class.java]

    }

    private fun initializeRecyclerview() {
        resultAdapter = ResultAdapter(list)
        mainBinding.rcv.layoutManager = LinearLayoutManager(this)
        mainBinding.rcv.adapter = resultAdapter
    }

    private fun registerLauncher() {
        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data?.data
                    imageBitmap = getBitmap(uri)
                    setImage(imageBitmap, true)
                }
            }
    }

    private fun getBitmap(uri: Uri?): Bitmap {
        var imageBitmap: Bitmap? = null
        if (uri != null) {
            contentResolver.openInputStream(uri).use {
                imageBitmap = BitmapFactory.decodeStream(it)

            }
        }
        return imageBitmap as Bitmap

    }

    private fun initializeGenerativeModel() {
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )
    }

    private fun onClickListener() {
        mainBinding.sendPrompt.setOnClickListener {
            val prompt: String = mainBinding.edPrompt.text.toString()
            if (imageBitmap != null) {
                mainBinding.uploadLayout.visibility = View.GONE
                startLoading()
                sendImagePrompt(imageBitmap!!)
            } else if (prompt.isEmpty()) {
                Toast.makeText(this, "Please write something...", Toast.LENGTH_SHORT).show()
            } else {
                val newIndex = list.size

                list.add(Message(prompt, true))
                resultAdapter.notifyItemInserted(newIndex) // Only notify that a new item was inserted
                mainBinding.rcv.scrollToPosition(newIndex) // Scroll to the newly added message
                startLoading()
                sendPrompt(prompt)
                mainBinding.edPrompt.text?.clear()
            }

        }
        mainBinding.fileUpload.setOnClickListener {
            sendIntentForImagePic()
        }
        mainBinding.btnDraw.setOnClickListener {
            mainBinding.drawerLayout.openDrawer(GravityCompat.START)
            aiViewModel.getAllList().observe(this) {

                val newIndex = it.size
                drawerList.clear()
                drawerList.addAll(it)
                drawerAdapter.notifyItemInserted(newIndex)
            }
        }
        mainBinding.cancelImage.setOnClickListener {
            setImage(null, false)
        }
    }

    private fun setImage(bitmap: Bitmap?, value: Boolean) {
        if (value) {
            mainBinding.uploadLayout.visibility = View.VISIBLE
        } else {
            imageBitmap = null
            mainBinding.uploadLayout.visibility = View.GONE
        }
        mainBinding.uploadImg.setImageBitmap(bitmap)
    }

    private fun sendIntentForImagePic() {
        val intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(
                    Build.VERSION_CODES.R
                ) >= 2
            ) {
                Intent(MediaStore.ACTION_PICK_IMAGES)
            } else {
                TODO("SdkExtensions.getExtensionVersion(R) < 2")
            }
        intent.type = "image/*"
        imageLauncher.launch(intent)
    }

    private fun sendPrompt(prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getResponse(prompt)
                if (result.isNotEmpty()) {
                    withContext(Dispatchers.Main) {

                        val newIndex = list.size
                        list.add(Message(result, false))
                        uploadingDB(prompt, result)
                        stopLoading()
                        resultAdapter.notifyItemInserted(newIndex) // Notify that a new item was inserted
                        mainBinding.rcv.scrollToPosition(newIndex) // Scroll to the newly added item
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun sendImagePrompt(prompt: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = getImageResponse(prompt)
                if (result.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        list.add(Message(result, false))
                        stopLoading()
                        mainBinding.rcv.adapter = resultAdapter
                        setImage(null, false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadingDB(request: String, response: String) {
        aiViewModel.insert(AIResponse(null, request, response))

    }


    private suspend fun getResponse(prompt: String): String {
        val resp = generativeModel.generateContent(prompt)
        return resp.text.toString()
    }

    private suspend fun getImageResponse(prompt: Bitmap): String {
        val resp = generativeModel.generateContent(prompt)
        return resp.text.toString()
    }

    override fun onItemClick(response: AIResponse) {
        mainBinding.drawerLayout.closeDrawers()
        val newIndex = list.size
        list.clear()
        list.add(Message(response.request, true))
        list.add(Message(response.response, false))
        resultAdapter.notifyDataSetChanged()
    }

    private fun startLoading() {
        mainBinding.loading.visibility = View.VISIBLE
        mainBinding.sendPrompt.isEnabled = false
    }

    private fun stopLoading() {
        mainBinding.loading.visibility = View.GONE
        mainBinding.sendPrompt.isEnabled = true
    }

}