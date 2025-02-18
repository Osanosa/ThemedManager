package pro.themed.manager.utils

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun FirebaseIsContributor(sharedPreferences: SharedPreferences) {
    val database =
        FirebaseDatabase.getInstance(
            "https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app"
        )
    val reference = database.getReference("Contributors/${GlobalVariables.themedId}")

    var isSubkeyPresent: Boolean

    // Add a ValueEventListener to check for the subkey just once
    reference.addListenerForSingleValueEvent(
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the subkey exists
                isSubkeyPresent = dataSnapshot.exists()
                Log.d("DATABASE", "THEMED ID IS ${GlobalVariables.themedId}")

                // If the subkey doesn't exist, set isSubkeyPresent to false
                if (isSubkeyPresent) {
                    sharedPreferences.edit().putBoolean("isContributor", true).apply()
                    sharedPreferences
                        .edit()
                        .putString(
                            "isContributorDate",
                            "${dataSnapshot.getValue(String::class.java)}",
                        )
                        .apply()

                    Log.d("DATABASE", "ENTRY FOUND")
                } else {
                    sharedPreferences.edit().putBoolean("isContributor", false).apply()
                    sharedPreferences.edit().putString("isContributorDate", "null").apply()

                    Log.d("DATABASE", "ENTRY NOT FOUND")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors here
                sharedPreferences.edit().putBoolean("isContributor", false).apply()
                Log.d("DATABASE", "ENTRY SEARCH FAILED")
            }
        }
    )
}
