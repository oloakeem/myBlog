package come.example.myblog

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class MyViewHolder(private var dataSet: ArrayList<String>, var mContext: Context) :

        RecyclerView.Adapter<MyViewHolder.ViewHolder>()   {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val author: TextView
        val images: ImageView

        init {
            images = view.findViewById(R.id.image_item)
            author = view.findViewById(R.id.author_item)
            textView = view.findViewById(R.id.title_item)


        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recyclerview_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val localFile = File.createTempFile("images", "jpeg")

        val myBitmap = BitmapFactory.decodeFile(localFile.absolutePath)

        viewHolder.textView.text = dataSet[position]
        viewHolder.author.text = dataSet[position]
        viewHolder.images.setImageBitmap(myBitmap)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    }


