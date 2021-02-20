package come.example.myblog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage

class MyListAdapter(val context: Activity, private val title: ArrayList<String>, private val image: ArrayList<String>, private val key: ArrayList<String>)
    : ArrayAdapter<String>(context, R.layout.user_items, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater

        val rowView = inflater.inflate(R.layout.user_items, null, true)
        val titleText = rowView.findViewById(R.id.user_title_item) as TextView
        val editButton = rowView.findViewById(R.id.editButton) as Button
        val deleteButton = rowView.findViewById(R.id.deleteButton) as Button


        titleText.text = title[position]

        editButton.setOnClickListener {
                Toast.makeText(context, "Edit it it", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, EditUserRecipe::class.java)
                intent.putExtra("key", key[position])
                intent.putExtra("path",image[position])
                context.startActivity(intent)
        }
        deleteButton.setOnClickListener {
            key[position]?.let { it1 -> confirmRemoval(it1,context) }
        }
        return rowView
    }
    fun confirmRemoval(string: String,context : Activity){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you wish to delete this Recipe")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("Yes") { dialog, which ->
            deleteRecipe(string)
        }

        builder.setNegativeButton("Cancel") { dialog, which ->

        }
        builder.show()
    }
    fun deleteRecipe(string : String){
        var user = FirebaseAuth.getInstance().currentUser
        var uid = user!!.uid
        val storage = Firebase.storage("gs://myblog-e6e37.appspot.com")
        val storageRef = storage.reference
        val listRef = storageRef.child("user/$uid/Recipes/$string/")
        val dataRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Recipes")

        listRef.listAll()
                .addOnSuccessListener { (items, prefixes) ->

                    prefixes.forEach { prefix ->

                    }

                    items.forEach { item ->
                        item.delete().addOnSuccessListener {
                                 // File deleted successfully
                             }.addOnFailureListener {
                                 // Uh-oh, an error occurred!
                             }

                    }
                }
                .addOnFailureListener {
                    // Uh-oh, an error occurred!
                    Toast.makeText(context,
                            "Failure", Toast.LENGTH_SHORT).show()
                }
            dataRef.child(string).removeValue()


    }
    }
