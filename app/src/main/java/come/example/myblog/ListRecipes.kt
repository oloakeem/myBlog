package come.example.myblog

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*
import java.util.logging.Filter


class ListRecipes : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataRef: DatabaseReference
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var storageRef: StorageReference
    lateinit var searchIcon: SearchView
    lateinit var storage: FirebaseStorage
    var user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewlist_layout)
        recyclerView = findViewById(R.id.viewRecipeRecyclerView)
        searchIcon = findViewById(R.id.country_search)
        dataRef = FirebaseDatabase.getInstance().reference.child("Recipes")
        storage = Firebase.storage("gs://myblog-e6e37.appspot.com")
        storageRef = storage.reference
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.hasFixedSize()

    loadData("")

}


        private fun loadData(string: String) {
            var query: Query = dataRef.orderByChild("title").startAt(string).endAt(string + "\uf8ff")
            val options = FirebaseRecyclerOptions.Builder<Model>()
                    .setQuery(query, Model::class.java)
                    .setLifecycleOwner(this)
                    .build()

            val adapter = object : FirebaseRecyclerAdapter<Model, MyViewHolder.ViewHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder.ViewHolder {
                    return MyViewHolder.ViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.recyclerview_item, parent, false))

                }

                override fun onBindViewHolder(holder: MyViewHolder.ViewHolder, position: Int, model: Model) {
                    var islandRef = storageRef.child(model.image.toString()).child("Images")
                    val localFile = File.createTempFile("images", "jpeg")
                    islandRef.getFile(localFile).addOnSuccessListener {
                        val myBitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        holder.textView.text = model.title
                        holder.author.text = model.author
                        holder.images.setImageBitmap(myBitmap)

                        holder.itemView.setOnClickListener {
                            val intent = Intent(applicationContext, OwnerRecipeActivity::class.java)
                            intent.putExtra("name", model.image)
                            startActivity(intent)
                        }
                    }
                }

                override fun onDataChanged() {
                    // If there are no chat messages, show a view that invites the user to add a message.
                    //    mEmptyListMessage.setVisibility(if (itemCount == 0) View.VISIBLE else View.GONE)
                }


            }
            adapter.startListening()
            recyclerView.adapter = adapter
            searchIcon.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    loadData(query)

                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    loadData(newText)
                    return false
                }

            })
        }

    }








