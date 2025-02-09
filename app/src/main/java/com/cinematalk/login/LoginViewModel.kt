import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.cinematalk.login.UserCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {
    // LiveData to hold the login result
    private val _loginResult = MutableLiveData<Pair<HashMap<String, Any>, String>>()
    val loginResult: LiveData<Pair<HashMap<String, Any>, String>> get() = _loginResult

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    // Function to log in the user using Firebase Authentication
    fun loginUser(credentials: UserCredentials) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener {
                // Fetch user data from Firestore upon successful authentication
                db.collection("Users").document(credentials.email)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.w("APP", "${result.id} => ${result.data}")
                        _loginResult.value = Pair(result.data as HashMap<String, Any>, result.id)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("LoginViewModel", "Error fetching Firestore document", exception)
                        _loginResult.value = Pair(hashMapOf(), "")
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("APP", "Authentication failed", exception)
                _loginResult.value = Pair(hashMapOf<String, Any>(), "")
            }
    }

    // Function to clear the login result
    fun clearLoginResult() {
        _loginResult.value = Pair(hashMapOf<String, Any>(), "")
    }
}
