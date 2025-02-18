package pro.themed.audhdlauncher.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class LauncherDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_CATEGORY)
        db.execSQL(SQL_CREATE_APP_LAUNCHES)
        db.prepopulateCategories()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For each table, perform migration if needed
        migrateTable(db, "category", SQL_CREATE_CATEGORY, SQL_DELETE_CATEGORY)
        migrateTable(db, "app_launches", SQL_CREATE_APP_LAUNCHES, SQL_DELETE_APP_LAUNCHES)
        // If you have other upgrade logic (like updateDefaultFilters), run it here
        db.updateDefaultFilters() // if applicable
    }

    // Generic migration function for one table
    fun migrateTable(db: SQLiteDatabase, tableName: String, createSQL: String, deleteSQL: String) {
        // Backup existing data
        val backupTableName = "${tableName}_backup"
        db.execSQL("CREATE TEMP TABLE $backupTableName AS SELECT * FROM $tableName")

        // Drop the original table
        db.execSQL(deleteSQL)
        // Create the new table with updated schema
        db.execSQL(createSQL)

        // Get the common columns between backup and new table
        val backupColumns = db.getColumnNames(backupTableName)
        val newColumns = db.getColumnNames(tableName)
        val commonColumns = backupColumns.intersect(newColumns).joinToString(",")

        if (commonColumns.isNotEmpty()) {
            // Restore data for common columns only
            db.execSQL(
                "INSERT INTO $tableName ($commonColumns) SELECT $commonColumns FROM $backupTableName"
            )
        }

        // Drop the temporary backup table
        db.execSQL("DROP TABLE IF EXISTS $backupTableName")
    }

    // Extension function to get all column names for a given table
    fun SQLiteDatabase.getColumnNames(tableName: String): Set<String> {
        val cursor = rawQuery("PRAGMA table_info($tableName)", null)
        val columns = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            val colName = cursor.getString(cursor.getColumnIndex("name"))
            columns.add(colName)
        }
        cursor.close()
        return columns
    }

    // Get all categories from DB
    fun getAllCategories(): List<CategoryData> {
        val db = readableDatabase
        val cursor = db.query("category", null, null, null, null, null, null)
        val categories = mutableListOf<CategoryData>()

        while (cursor.moveToNext()) {
            categories.add(
                CategoryData(
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("default_include")),
                    cursor.getString(cursor.getColumnIndexOrThrow("default_exclude")),
                    cursor.getString(cursor.getColumnIndexOrThrow("custom_include")),
                    cursor.getString(cursor.getColumnIndexOrThrow("custom_exclude")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("custom_bool")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("custom_int")),
                )
            )
        }

        cursor.close()
        return categories
    }

    fun incrementAppLaunchCount(dbHelper: LauncherDbHelper, categoryId: Int, packageName: String) {
        val db = dbHelper.writableDatabase

        // Query to get the current launch count
        val cursor =
            db.rawQuery(
                "SELECT launch_count FROM app_launches WHERE category_id = ? AND package_name = ?",
                arrayOf(categoryId.toString(), packageName),
            )

        if (cursor.moveToFirst()) {
            // Get the current launch count
            val currentLaunchCount = cursor.getInt(cursor.getColumnIndex("launch_count"))

            // Increment the launch count
            val newLaunchCount = currentLaunchCount + 1

            // Prepare values to update
            val values = ContentValues().apply { put("launch_count", newLaunchCount) }

            // Update the app launch count in the database
            val rowsAffected =
                db.update(
                    "app_launches",
                    values,
                    "category_id = ? AND package_name = ?",
                    arrayOf(categoryId.toString(), packageName),
                )

            if (rowsAffected == 0) {
                // If no rows were updated, the entry doesn't exist, so insert it
                insertAppLaunch(dbHelper, categoryId, packageName, newLaunchCount)
            }

            Log.d("LauncherDbHelper", "Launch count updated to $newLaunchCount for $packageName")
        } else {
            // No entry found, so insert the app with launch count = 1
            insertAppLaunch(dbHelper, categoryId, packageName, 1)
        }

        cursor.close()
    }

    fun insertAppLaunch(
        dbHelper: LauncherDbHelper,
        categoryId: Int,
        packageName: String,
        launchCount: Int,
    ) {
        val db = dbHelper.writableDatabase

        val values =
            ContentValues().apply {
                put("category_id", categoryId)
                put("package_name", packageName)
                put("launch_count", launchCount)
            }

        // Insert the new app launch record
        val rowsInserted = db.insert("app_launches", null, values)

        if (rowsInserted == -1L) {
            // Handle error if insertion failed
            Log.e("LauncherDbHelper", "Failed to insert app launch for $packageName")
        }
    }

    // Get app launch counts per category
    fun getLaunchCountsForCategory(categoryId: Int): Map<String, Int> {
        val db = readableDatabase
        val cursor =
            db.query(
                "app_launches",
                arrayOf("package_name", "launch_count"),
                "category_id = ?",
                arrayOf(categoryId.toString()),
                null,
                null,
                null,
            )

        val launchCounts = mutableMapOf<String, Int>()
        while (cursor.moveToNext()) {
            launchCounts[cursor.getString(0)] = cursor.getInt(1)
        }

        cursor.close()
        return launchCounts
    }

    // Get category ID by name
    fun getCategoryId(categoryName: String): Int {
        val db = readableDatabase
        val cursor =
            db.query("category", arrayOf("id"), "name = ?", arrayOf(categoryName), null, null, null)

        return if (cursor.moveToFirst()) cursor.getInt(0) else -1
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Launcher.db"

        private const val SQL_CREATE_CATEGORY =
            "CREATE TABLE category (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "default_include TEXT," +
                "default_exclude TEXT," +
                "custom_include TEXT," +
                "custom_exclude TEXT," +
                "custom_bool INTEGER," +
                "custom_int INTEGER" +
                ")"

        private const val SQL_DELETE_CATEGORY = "DROP TABLE IF EXISTS category"

        private const val SQL_CREATE_APP_LAUNCHES =
            "CREATE TABLE app_launches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category_id INTEGER NOT NULL," +
                "package_name TEXT NOT NULL," +
                "launch_count INTEGER DEFAULT 0," +
                "FOREIGN KEY(category_id) REFERENCES category(id) ON DELETE CASCADE," +
                "UNIQUE(category_id, package_name) ON CONFLICT REPLACE" +
                ")"

        private const val SQL_DELETE_APP_LAUNCHES = "DROP TABLE IF EXISTS app_launches"
    }
}

// Data class for pre-population
data class CategoryData(
    val name: String,
    val defaultInclude: String?,
    val defaultExclude: String?,
    val customInclude: String?,
    val customExclude: String?,
    val customBool: Int,
    val customInt: Int,
)

data class AppLaunch(
    val id: Int,
    val categoryId: Int,
    val packageName: String,
    val launchCount: Int,
)

fun SQLiteDatabase.insertCategory(
    name: String,
    defaultInclude: String?,
    defaultExclude: String?,
    customInclude: String?,
    customExclude: String?,
    customBool: Int,
    customInt: Int,
): Long {
    val values =
        ContentValues().apply {
            put("name", name)
            put("default_include", defaultInclude)
            put("default_exclude", defaultExclude)
            put("custom_include", customInclude)
            put("custom_exclude", customExclude)
            put("custom_bool", customBool)
            put("custom_int", customInt)
        }
    return this.insert("category", null, values)
}

// Extension function on SQLiteDatabase to prepopulate categories.
fun SQLiteDatabase.prepopulateCategories() {
    DefaultCategoryData.categories.forEach { category ->
        val values =
            ContentValues().apply {
                put("name", category.name)
                put("default_include", category.defaultInclude)
                put("default_exclude", category.defaultExclude)
                put("custom_include", category.customInclude)
                put("custom_exclude", category.customExclude)
                put("custom_bool", category.customBool)
                put("custom_int", category.customInt)
            }
        insert("category", null, values)
    }
}

// Extension function on SQLiteDatabase to update default filters.
fun SQLiteDatabase.updateDefaultFilters() {
    DefaultCategoryData.categories.forEach { defaultCat ->
        val values =
            ContentValues().apply {
                put("default_include", defaultCat.defaultInclude)
                put("default_exclude", defaultCat.defaultExclude)
                put("custom_bool", defaultCat.customBool)
                put("custom_int", defaultCat.customInt)
            }
        val rowsAffected = update("category", values, "name = ?", arrayOf(defaultCat.name))
        if (rowsAffected == 0) {
            // If no row was updated, this category doesn't exist yet—insert it.
            val insertValues =
                ContentValues().apply {
                    put("name", defaultCat.name)
                    put("default_include", defaultCat.defaultInclude)
                    put("default_exclude", defaultCat.defaultExclude)
                    put("custom_include", defaultCat.customInclude)
                    put("custom_exclude", defaultCat.customExclude)
                    put("custom_bool", defaultCat.customBool)
                    put("custom_int", defaultCat.customInt)
                }
            insert("category", null, insertValues)
        }
    }
}
