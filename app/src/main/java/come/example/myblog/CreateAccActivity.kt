package come.example.myblog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class CreateAccActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    lateinit var emailEditText: EditText
    lateinit var userNameEditText: EditText

    lateinit var passwordEditText: EditText
        lateinit var createAccBtn: Button
    var user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acc)

        userNameEditText = findViewById(R.id.signUpUsernameEditText) as EditText
        emailEditText = findViewById(R.id.signUpEmailEditText) as EditText
        passwordEditText = findViewById(R.id.signUpPasswordEditText) as EditText
        createAccBtn = findViewById(R.id.createAccButton) as Button
        auth = Firebase.auth



            createAccBtn.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View): Unit {
                    var myEmail = emailEditText.text.toString()
                    var myPassword = passwordEditText.text.toString()
                    var myUserName = userNameEditText.text.toString()

                    if (myEmail.isEmpty()) {
                        emailEditText.setError("email")
                        emailEditText.requestFocus()
                    } else if (myPassword.isEmpty()) {
                        passwordEditText.setError("password")
                        passwordEditText.requestFocus()
                    } else if (myUserName.isEmpty()) {
                        userNameEditText.setError("username?")
                        userNameEditText.requestFocus()

                    } else if (!(myEmail.isEmpty() && myPassword.isEmpty() && myUserName.isEmpty())) {
                        auth.createUserWithEmailAndPassword(myEmail, myPassword)
                            .addOnCompleteListener(this@CreateAccActivity) { task ->
                                if (task.isSuccessful) {
                                    val database = FirebaseDatabase.getInstance()
                                    val myRef = database.getReference("User")
                                    myRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child("username").setValue(myUserName)
                                    val intent = Intent(this@CreateAccActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }

                    }

                }
            })
    }


}