package come.example.myblog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.*

class EditUserRecipe : AppCompatActivity() {
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user!!.uid
    lateinit var enteredTitle: EditText
    lateinit var enteredImages: ImageView
    lateinit var submitButton: Button
    lateinit var enteredIngredients: EditText
    lateinit var sampleText: TextView
    lateinit var storage: FirebaseStorage
    lateinit var imageButton: Button
    lateinit var storageRef: StorageReference
    lateinit var itemKey: String
    lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_it)
         itemKey = intent.getStringExtra("key") // 2
        imagePath = intent.getStringExtra("path") // 2
        enteredTitle = findViewById(R.id.EDITOR_recipeTitleEditTxt)
        enteredIngredients = findViewById(R.id.EDITOR_recipeIngredients)
        enteredImages = findViewById(R.id.EDITOR_recipeImages)
        submitButton = findViewById(R.id.EDITOR_submitRecipe)
        sampleText = findViewById(R.id.EDITOR_sampleText)
        imageButton = findViewById(R.id.EDITOR_addrecipeImageButton)
        storage = Firebase.storage("gs://myblog-e6e37.appspot.com")
        storageRef = storage.reference

        showApple(imagePath)

        imageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, EditUserRecipe.PERMISSION_CODE);
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
            enteredImages.setImageBitmap(myBitmap);
            sampleText.text = readFile(textFile)
        }.addOnFailureListener {
            // Handle any errors
        }
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
    private fun getRecipeInfo(textInfo : File): String {

        val bufferedReader: BufferedReader = textInfo.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        sampleText.text = inputString
        return inputString
    }
    private fun writeToFile(recipeTitle: EditText, recipeText : EditText): File{
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
    private fun uploadFile() {
        val folderRef = storageRef.child("/user/$uid/Recipes/$itemKey")
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
        val folderRef = storageRef.child("/user/$uid/Recipes/$itemKey")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            enteredImages.setImageURI(data?.data)
            // Get the data from an ImageView as bytes
            val bitmap = (enteredImages.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            folderRef.child("Images").putBytes(data).addOnSuccessListener {
                var myPath = ("/user/$uid/Recipes/$itemKey")
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Recipes/$itemKey")
                myRef.setValue(Recipes("$recipeTitle","$myPath","$myPath","$myPath","$myPath","w/e","$itemKey"))
            }
                    .addOnFailureListener {
                        // Handle unsuccessful uploads

                    }
        }
    }
}