package come.example.myblog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeActivity : AppCompatActivity() {
    lateinit var createRecipe : Button
    lateinit var viewRecipe : Button
    lateinit var editButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        createRecipe = findViewById(R.id.createRecipe)
        viewRecipe = findViewById(R.id.viewRecipeButton)
        editButton = findViewById(R.id.editRecipe)

        createRecipe.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@HomeActivity, CreateRecipeActivity::class.java)
                startActivity(intent)
            }
        })
        viewRecipe.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@HomeActivity, ListRecipes::class.java)
                startActivity(intent)
            }
        })
        editButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@HomeActivity, ShowUsersRecipes::class.java)
                startActivity(intent)
            }
        })
    }


}