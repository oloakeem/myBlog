package come.example.myblog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
     lateinit var loginEmail: EditText
    lateinit var loginPassword: EditText
    lateinit var loginButton: Button
    lateinit var SignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginEmail = findViewById(R.id.loginEmailEditTxt) as EditText
        loginPassword = findViewById(R.id.loginPassWordEditTxt) as EditText
        loginButton = findViewById(R.id.loginButton) as Button
        SignUp = findViewById(R.id.signUpTextView) as TextView

        auth = FirebaseAuth.getInstance()


    loginButton.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View): Unit {
            var myEmail = loginEmail.text.toString()
            var myPassword = loginPassword.text.toString()

            if (myEmail.isEmpty()) {
                loginEmail.setError("email")
                loginEmail.requestFocus()
            } else if (myPassword.isEmpty()) {
                loginPassword.setError("password")
                loginPassword.requestFocus()

            } else if (!(myEmail.isEmpty() && myPassword.isEmpty())) {
                auth.signInWithEmailAndPassword(myEmail, myPassword)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.putExtra("userEmail", myEmail)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.

                        }

                        // ...
                    }
            }
        }
    })
       SignUp.setOnClickListener(object : View.OnClickListener{
           override fun onClick(view: View): Unit {
           val intent = Intent(this@LoginActivity,CreateAccActivity::class.java)
               startActivity(intent)
           }
       })
       }

    }
