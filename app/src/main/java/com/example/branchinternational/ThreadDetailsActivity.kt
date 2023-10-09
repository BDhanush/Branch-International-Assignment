package com.example.branchinternational

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.branchinternational.adapter.MessageItemAdapter
import com.example.branchinternational.databinding.ActivityThreadDetailsBinding
import com.example.branchinternational.model.Message
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates


class ThreadDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThreadDetailsBinding
    lateinit var adapter:MessageItemAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    var dataset=mutableListOf<Message>()
    private var threadId by Delegates.notNull<Int>();

    override fun onCreate(savedInstanceState: Bundle?) {
        threadId = intent.getIntExtra("threadId",-1)
        super.onCreate(savedInstanceState)
        binding = ActivityThreadDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title=getString(R.string.threadTitle,threadId)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        linearLayoutManager= LinearLayoutManager(this)
        binding.threadRecyclerView.layoutManager=linearLayoutManager
        adapter = MessageItemAdapter(applicationContext,threads[threadId]!!, ADAPTER_THREAD)
        adapter.notifyDataSetChanged()
        binding.threadRecyclerView.adapter=adapter

        binding.swipeRefreshLayout.setOnRefreshListener{
            refresh()
        }

        binding.reply.setOnClickListener {
            val body:String = binding.compose.text.toString().trim()

            if(body.isNotEmpty())
            {
                lockButton()
                sendMessage(threadId,body)
            }
            binding.compose.text.clear()
        }

        val actionBar1: View =findViewById(androidx.appcompat.R.id.action_bar)
        actionBar1.setOnClickListener {
            binding.threadRecyclerView.layoutManager?.scrollToPosition(0)
            binding.threadRecyclerView.setLayoutFrozen(true)
            binding.threadRecyclerView.setLayoutFrozen(false)
        }

    }

    private fun sendMessage(threadId:Int,body:String)
    {
        var message=Message(threadId,body)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
        val retrofitBuffer= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL).build().create(ApiInterface::class.java)

        val retrofitData = AUTH_TOKEN?.let { retrofitBuffer.sendMessage(it,message) }

        retrofitData?.enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
//                val code=response.code()
//                Toast.makeText(applicationContext,"$code",Toast.LENGTH_SHORT).show()
                message=response.body()!!
                Toast.makeText(applicationContext,"Message sent",Toast.LENGTH_SHORT).show()
                addMessage(message)
                unlockButton()

            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Toast.makeText(applicationContext,t.message?:"Error",Toast.LENGTH_SHORT).show()
                unlockButton()

            }
        })
    }

    private fun refresh()
    {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
        val retrofitBuffer= Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL).build().create(ApiInterface::class.java)

        val retrofitData = AUTH_TOKEN?.let { retrofitBuffer.getMessages(it) }
        retrofitData?.enqueue(object : Callback<List<Message>?> {
            override fun onResponse(call: Call<List<Message>?>, response: Response<List<Message>?>) {
//                val code=response.code()
//                Toast.makeText(applicationContext,"$code",Toast.LENGTH_SHORT).show()
                dataset=response.body()!!.toMutableList()
                sortByDate(dataset)
                createThread(dataset)
                adapter = MessageItemAdapter(applicationContext,threads[threadId]!!, ADAPTER_THREAD)
                adapter.notifyDataSetChanged()
                binding.threadRecyclerView.adapter=adapter
                binding.swipeRefreshLayout.isRefreshing = false

            }

            override fun onFailure(call: Call<List<Message>?>, t: Throwable) {
                Toast.makeText(applicationContext,t.message?:"Error",Toast.LENGTH_SHORT).show()


            }
        })
    }

    private fun sortByDate(dataset:MutableList<Message>)
    {
        dataset.sortBy{ it.timestamp }
    }

    private fun createThread(dataset:MutableList<Message>)
    {
        threads.clear()
        for(message in dataset)
        {
            if(threads[message.thread_id]==null)
            {
                threads[message.thread_id!!] = mutableListOf()
            }
            threads[message.thread_id]?.add(message)
        }

    }

    private fun addMessage(message: Message)
    {
        adapter.addMessage(message)
        binding.threadRecyclerView.layoutManager?.scrollToPosition(adapter.dataset.size-1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun lockButton()
    {
        binding.reply.isEnabled=false
    }

    private fun unlockButton()
    {
        binding.reply.isEnabled=true
    }

}