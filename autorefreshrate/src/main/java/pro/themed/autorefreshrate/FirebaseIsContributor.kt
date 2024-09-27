package pro.themed.autorefreshrate

import android.content.*
import android.util.*
import android.widget.*
import com.google.firebase.database.*
import com.jaredrummler.ktsh.*

val themedId by lazy {
    Shell.SH.run("""su -c getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b""")
        .stdout()
}
fun FirebaseIsContributor(sharedPreferences: SharedPreferences, context: Context) {
    val database =
        FirebaseDatabase.getInstance("https://themed-manager-default-rtdb.europe-west1.firebasedatabase.app")
    val reference = database.getReference("Contributors/${themedId}")

    var isSubkeyPresent: Boolean


    // Add a ValueEventListener to check for the subkey just once
    reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Check if the subkey exists
            isSubkeyPresent = dataSnapshot.exists()
            Log.d("DATABASE", "THEMED ID IS ${themedId}")

            // If the subkey doesn't exist, set isSubkeyPresent to false
            if (isSubkeyPresent) {
                sharedPreferences.edit().putBoolean("isContributor", true)
                    .apply()
                sharedPreferences.edit().putString(
                    "isContributorDate",
                    "${dataSnapshot.getValue(String::class.java)}"
                ).apply()
                Toast.makeText(context, "THANK YOU FOR YOUR CONTRIBUTION", Toast.LENGTH_SHORT).show()

                Log.d("DATABASE", "ENTRY FOUND")
            } else {
                sharedPreferences.edit().putBoolean("isContributor", false)
                    .apply()
                sharedPreferences.edit().putString("isContributorDate", "null")
                    .apply()

                Log.d("DATABASE", "ENTRY NOT FOUND")

            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle any errors here
            sharedPreferences.edit().putBoolean("isContributor", false).apply()
            Log.d("DATABASE", "ENTRY SEARCH FAILED")
        }
    })
}