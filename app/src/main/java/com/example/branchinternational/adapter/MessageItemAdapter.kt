package com.example.branchinternational.adapter

import android.app.*
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.branchinternational.ADAPTER_MESSAGE
import com.example.branchinternational.R
import com.example.branchinternational.ThreadDetailsActivity
import com.example.branchinternational.model.Message
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class MessageItemAdapter(context: Context,val dataset:MutableList<Message>,val itemType:Int): RecyclerView.Adapter<MessageItemAdapter.ItemViewHolder>()
{
    lateinit var context: Context
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val userId:TextView= view.findViewById(R.id.userId)
        val body:TextView= view.findViewById(R.id.body)
        val messageId:TextView= view.findViewById(R.id.messageId)
        val date:TextView= view.findViewById(R.id.date)
        val time:TextView=view.findViewById(R.id.time)
        val messageItem:MaterialCardView?=view.findViewById(R.id.messageCard)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        context=parent.context

        val adapterLayout =
            if(itemType== ADAPTER_MESSAGE)
                LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
            else
                LayoutInflater.from(parent.context).inflate(R.layout.thread_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.userId.text=
            if(item.user_id!=null && item.agent_id==null)
                context.getString(R.string.userId, item.user_id ?: -1)
            else
                context.getString(R.string.agentId, item.agent_id ?: -1)
        holder.body.text=item.body.toString()
        holder.messageId.text=context.getString(R.string.messageId, item.id ?: -1)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.date.text= item.timestamp?.let { dateFormat.format(it) }
        val timeFormat = SimpleDateFormat("hh:mm a")
        holder.time.text= item.timestamp?.let { timeFormat.format(it) }

        if(itemType == ADAPTER_MESSAGE) {
            holder.messageItem?.setOnClickListener {
                val threadId = item.thread_id
                val intent = Intent(holder.itemView.context, ThreadDetailsActivity::class.java)
                intent.putExtra("threadId", threadId)
                holder.itemView.context.startActivity(intent)
            }
        }

    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size

    fun addMessage(message: Message)
    {
        dataset.add(message)
        notifyItemInserted(dataset.size-1)
    }

}