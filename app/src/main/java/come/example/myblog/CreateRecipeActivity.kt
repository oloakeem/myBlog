package come.example.myblog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.*
import javax.security.auth.callback.Callback


class CreateRecipeActivity : AppCompatActivity() {
    lateinit var enteredTitle: EditText
    lateinit var enteredImages: ImageView
    lateinit var submitButton: Button
    lateinit var enteredIngredients: EditText
    lateinit var sampleText: TextView
    lateinit var finally : String
    lateinit var author: TextView
    lateinit var storage: FirebaseStorage
    lateinit var imageButton: Button
    lateinit var storageRef: StorageReference
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("Recipes").push()
    val UsermyRef = database.getReference("User")
    var myKey = myRef.key
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        storage = Firebase.storage("gs://myblog-e6e37.appspot.com")
        enteredTitle = findViewById(R.id.recipeTitleEditTxt) as EditText
        enteredIngredients = findViewById(R.id.recipeIngredients) as EditText
        enteredImages = findViewById(R.id.recipeImages) as ImageView
        submitButton = findViewById(R.id.submitRecipe) as Button
        sampleText = findViewById(R.id.sampleText) as TextView
        author = findViewById(R.id.authorTextView) as TextView
        imageButton = findViewById(R.id.addrecipeImageButton) as Button
        storageRef = storage.reference

        imageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //check runtime permission
                if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    //system OS is < Marshmallow
                    pickImageFromGallery();
                }
            }
        })

        submitButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                    makeDirectory()

            }
        })
    }

    fun makeDirectory() {
        var recipeTitle = enteredTitle.text.toString()
        val file = File(applicationContext.getExternalFilesDir(null), "$recipeTitle")

        if (file.isDirectory()) {
            getRecipeInfo(writeToFile(enteredTitle,enteredIngredients))
            uploadFile()
        } else {
            file.mkdirs()
            makeDirectory()
        }

    }

    private fun writeToFile(recipeTitle:EditText,recipeText : EditText): File{
        var title = recipeTitle.text.toString()
        val file = File(applicationContext.getExternalFilesDir(null), "$title")
        var recipeInfo = recipeText.text.toString()
        var  myFile = File(file, "RecipeINFO.txt")

        try {
            val writer = FileWriter(myFile)
            writer.append(recipeInfo)
            writer.flush()
            writer.close()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return myFile
    }
    private fun getRecipeInfo(textInfo : File): String {

        val bufferedReader: BufferedReader = textInfo.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        sampleText.text = inputString
        return inputString
    }


    private fun uploadFile() {
        val folderRef = storageRef.child("/user/$uid/Recipes/$myKey")
        folderRef.child("Ingredients.txt").putBytes(getRecipeInfo(writeToFile(enteredTitle,enteredIngredients)).toByteArray())
        folderRef.child("Description.txt").putBytes(getRecipeInfo(writeToFile(enteredTitle,enteredIngredients)).toByteArray())
        folderRef.child("NutritionalInfo.txt").putBytes(getRecipeInfo(writeToFile(enteredTitle,enteredIngredients)).toByteArray()).addOnSuccessListener {
            Toast.makeText(applicationContext, "Did it", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed it", Toast.LENGTH_SHORT).show()

        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var recipeTitle = enteredTitle.text.toString()
        val folderRef = storageRef.child("/user/$uid/Recipes/$myKey")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            enteredImages.setImageURI(data?.data)
            // Get the data from an ImageView as bytes
            val bitmap = (enteredImages.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            folderRef.child("Images").putBytes(data).addOnSuccessListener {
                var myPath = ("/user/$uid/Recipes/$myKey")
               // var author = UsermyRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child("username").database
                myRef.setValue(Recipes("$recipeTitle","$myPath","$myPath","$myPath","$myPath","Admin","$myKey"))
                UsermyRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child("savedKeys").push().setValue(myKey)
            }
                    .addOnFailureListener {
                        // Handle unsuccessful uploads

                    }
        }
    }
}



