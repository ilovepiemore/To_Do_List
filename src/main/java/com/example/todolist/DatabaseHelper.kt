import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ToDoDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IS_COMPLETED = "is_completed"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(description: String) {
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPTION, description)
        }
        writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun deleteTask(taskId: Int) {
        writableDatabase.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(taskId.toString()))
    }

    fun markTaskAsCompleted(taskId: Int) {
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, 1)
        }
        writableDatabase.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(taskId.toString()))
    }

    fun getAllTasks(): List<String> {
        val tasks = mutableListOf<String>()
        val cursor: Cursor = readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val descriptionColumnIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION)
            val isCompletedColumnIndex = cursor.getColumnIndex(COLUMN_IS_COMPLETED)

            if (descriptionColumnIndex != -1 && isCompletedColumnIndex != -1) {
                val description = cursor.getString(descriptionColumnIndex)
                val isCompleted = cursor.getInt(isCompletedColumnIndex) == 1
                val status = if (isCompleted) "[Completed]" else "[Pending]"
                tasks.add("$status $description")
            }
        }

        cursor.close()
        return tasks
    }

    fun getTaskId(position: Int): Int {
        val cursor: Cursor = readableDatabase.rawQuery("SELECT $COLUMN_ID FROM $TABLE_NAME", null)

        // Ensure that the cursor has the desired column
        val columnIndex = cursor.getColumnIndex(COLUMN_ID)
        if (columnIndex != -1) {
            cursor.moveToPosition(position)
            val taskId = cursor.getInt(columnIndex)
            cursor.close()
            return taskId
        }

        // Handle the case where the desired column is not found
        cursor.close()
        return -1
    }
}
