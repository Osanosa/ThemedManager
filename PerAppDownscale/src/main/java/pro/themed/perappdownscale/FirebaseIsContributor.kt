package pro.themed.perappdownscale

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun FirebaseIsContributor(
    context: Context,
    privilegedHelper: PrivilegedCommandHelper,
    onContributorStatusChanged: (Boolean, String?) -> Unit
) {
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    CoroutineScope(Dispatchers.IO).launch {
Thread.sleep(5000)
    val themedId = privilegedHelper.executeCommand("""getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b""").output
    val database =
        FirebaseDatabase.getInstance(
            "https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app"
        )
    val reference = database.getReference("Contributors/${themedId}")

    var isSubkeyPresent: Boolean

    // Add a ValueEventListener to check for the subkey just once
    reference.addListenerForSingleValueEvent(
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the subkey exists
                isSubkeyPresent = dataSnapshot.exists()
                Log.d("DATABASE", "THEMED ID IS ${themedId}")

                // If the subkey doesn't exist, set isSubkeyPresent to false
                if (isSubkeyPresent) {
                    val contributorDate = dataSnapshot.getValue(String::class.java)
                    onContributorStatusChanged(true, contributorDate)
                    Toast.makeText(context, "THANK YOU FOR YOUR CONTRIBUTION", Toast.LENGTH_SHORT)
                        .show()

                    Log.d("DATABASE", "ENTRY FOUND - Firebase Contributor: true")
                } else {
                    onContributorStatusChanged(false, null)

                    Log.d("DATABASE", "ENTRY NOT FOUND - Firebase Contributor: false")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
                onContributorStatusChanged(false, null)
                Log.d("DATABASE", "ENTRY SEARCH FAILED - Firebase Contributor: false")
            }
        }
    )
}}
