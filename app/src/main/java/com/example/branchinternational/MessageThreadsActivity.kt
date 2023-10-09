package com.example.branchinternational

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.branchinternational.adapter.MessageItemAdapter
import com.example.branchinternational.databinding.ActivityMessageThreadsBinding
import com.example.branchinternational.model.Message
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MessageThreadsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageThreadsBinding
    lateinit var adapter:MessageItemAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    var dataset=mutableListOf<Message>()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageThreadsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val actionBar: ActionBar?=supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#b4e6fe")))
        actionBar?.setDisplayShowTitleEnabled(false);

        actionBar?.setDisplayShowCustomEnabled(true)
        val layoutInflater:LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        actionBar?.customView = layoutInflater.inflate(R.layout.action_bar_logo,null) as View

        binding.progressBar.show()
        binding.messageThreadsRecyclerView.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(this)
        binding.messageThreadsRecyclerView.layoutManager=linearLayoutManager
        getMessages()
        binding.swipeRefreshLayout.setOnRefreshListener{
            binding.swipeRefreshLayout.isRefreshing = false
            getMessages()
        }
        val actionBar1: View =findViewById(androidx.appcompat.R.id.action_bar)
        actionBar1.setOnClickListener {
            binding.messageThreadsRecyclerView.layoutManager?.scrollToPosition(0)
            binding.messageThreadsRecyclerView.setLayoutFrozen(true)
            binding.messageThreadsRecyclerView.setLayoutFrozen(false)

        }

    }

    private fun getMessages()
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
                dataset=createThread(dataset)
                adapter = MessageItemAdapter(applicationContext,dataset,ADAPTER_MESSAGE)
                adapter.notifyDataSetChanged()
                binding.progressBar.hide()
                binding.messageThreadsRecyclerView.adapter=adapter

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

    private fun createThread(dataset:MutableList<Message>):MutableList<Message>
    {
        threads.clear()
        val threadHeader= mutableListOf<Message>()
        for(message in dataset)
        {
            if(threads[message.thread_id]==null)
            {
                threads[message.thread_id!!] = mutableListOf()
            }
            threads[message.thread_id]?.add(message)
        }

        for((threadId,messageList) in threads)
        {
            for(i in messageList.size-1 downTo 0)
            {
                if(messageList[i].agent_id==null)
                {
//                    Toast.makeText(this)
                    threadHeader.add(messageList[i])
                    break
                }
            }
        }

        threadHeader.sortByDescending { it.timestamp }
        return threadHeader

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.logOut -> {
                val pref = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.remove("auth_token")
                editor.apply()
                Toast.makeText(this, R.string.logOutMessage, Toast.LENGTH_LONG).show()
                finish()
                Intent(this, LoginActivity::class.java).also{
                    startActivity(it)
                }
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

}