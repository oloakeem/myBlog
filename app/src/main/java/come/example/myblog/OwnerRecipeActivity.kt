package come.example.myblog

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class OwnerRecipeActivity : AppCompatActivity() {
    lateinit var viewTitle: TextView
    lateinit var viewImageView: ImageView
    lateinit var viewIngredients: TextView
    lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    lateinit var urLString : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_recipe)
        viewImageView = findViewById(R.id.viewImage)
        viewIngredients = findViewById(R.id.viewIngredients)

        storage = Firebase.storage("gs://myblog-e6e37.appspot.com")
        storageRef = storage.reference
        // Write a message to the database
        // Write a message to the database
        var bundle :Bundle ?=intent.extras
        var message = bundle!!.getString("name") // 1
        var strUser: String = intent.getStringExtra("name") // 2

        showApple(strUser)

    }
    fun readFile(filename: File) : String{
        val reader = BufferedReader(FileReader(filename))
        val stringBuilder = StringBuilder()
        var line: String?
        val ls = System.getProperty("line.separator")
        do {
            line = reader.readLine()
            if (line == null)
                break
            stringBuilder.append(line);
            stringBuilder.append(ls);
        } while (true)
        stringBuilder.deleteCharAt(stringBuilder.length - 1);
        reader.close()

        return stringBuilder.toString()
    }

    fun showApple(path : String) {
        var ingredientsRef = storageRef.child(path).child("Ingredients.txt")
        var stepsRef = storageRef.child(path).child("Description.txt")
        var nutritional = storageRef.child(path).child("NutritionalInfo.txt")
        var islandRef = storageRef.child(path).child("Images")
        val localFile = File.createTempFile("images", "jpeg")
        val textFile = File.createTempFile("sample", "txt")
        val textFile2 = File.createTempFile("sample", "txt")
        val textFile3 = File.createTempFile("sample", "txt")

        ingredientsRef.getFile(textFile)
        stepsRef.getFile(textFile)
        nutritional.getFile(textFile)
        islandRef.getFile(localFile).addOnSuccessListener {
            val myBitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            viewImageView.setImageBitmap(myBitmap);
            viewIngredients.text = readFile(textFile)
        }.addOnFailureListener {
            // Handle any errors
        }
    }
}