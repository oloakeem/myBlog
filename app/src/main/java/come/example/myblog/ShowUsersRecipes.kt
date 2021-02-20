package come.example.myblog

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import java.io.File

class ShowUsersRecipes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataRef: DatabaseReference
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var storageRef: StorageReference
    lateinit var newKeyList: ArrayList<String>
    lateinit var nameList: ArrayList<String>
    lateinit var imageList: ArrayList<String>
    lateinit var simpleListView: ListView
    lateinit var storage: FirebaseStorage
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users_recipes)
        storage = Firebase.storage("gs://myblog-e6e37.appspot.com")
        storageRef = storage.reference
        dataRef = FirebaseDatabase.getInstance().reference.child("Recipes")
        simpleListView = findViewById(R.id.viewUserList)

        val listRef = storageRef.child("user/$uid/Recipes/")
        listRef.listAll()
                .addOnSuccessListener { (items, prefixes) ->
                    val listItems: ArrayList<String> = ArrayList(items.size)

                    prefixes.forEach { prefix ->
                        listItems.add(prefix.name)

                    }
                    loadIN(listItems)

                    items.forEach { item ->

                    }


                }
                .addOnFailureListener {
                    // Uh-oh, an error occurred!
                }
    }

    fun loadIN(keyList: ArrayList<String>) {
        nameList = ArrayList(keyList.size)
        imageList = ArrayList(keyList.size)
        newKeyList = ArrayList(keyList.size)

        for (i in 0 until keyList.size) {
            dataRef.child(keyList[i]).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // TODO: handle the post
                    var name = snapshot.child("title").value.toString()
                    var images = snapshot.child("image").value.toString()
                    var key = snapshot.child("key").value.toString()

                    nameList.add(name)
                    imageList.add(images)
                    newKeyList.add(key)
                    val adapter = MyListAdapter(this@ShowUsersRecipes, nameList, imageList,newKeyList)
                    simpleListView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }
    }
}




