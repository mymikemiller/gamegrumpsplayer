package com.mymikemiller.gamegrumpsplayer.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mymikemiller.gamegrumpsplayer.Detail
import android.content.ContentValues.TAG
import java.sql.SQLException

/**
 * This class uses the database to keep track of which games the user has specified they'd like to skip
 */
class SkippedGames {
    companion object {

        val DATABASE_VERSION: Int = 2
        val DATABASE_NAME: String = "SkippedGames"
        val SKIPPED_GAMES_TABLE_NAME: String = "SkippedGamesTable"

        // VideoList columns
        val KEY_GAME: String="Game"

        private val SKIPPED_GAMES_TABLE_CREATE =
                "CREATE TABLE " + SKIPPED_GAMES_TABLE_NAME + " (" +
                        KEY_GAME + " TEXT NOT NULL UNIQUE);"

        fun filterOutSkipped(context: Context, details: List<Detail>) : List<Detail> {
            val dbHelper = SkippedGamesOpenHelper(context.applicationContext)
            val gamesToSkip = dbHelper.getSkippedGamesFromDb()

            val filteredDetails = details.filter {
                !gamesToSkip.contains(it.game)
            }

            return filteredDetails
        }

        fun addSkippedGame(context: Context, game: String) {
            val dbHelper = SkippedGamesOpenHelper(context.applicationContext)
            dbHelper.addSkippedGame(game)
        }

        fun getAllSkippedGames(context: Context): List<String> {
            val dbHelper = SkippedGamesOpenHelper(context.applicationContext)
            val skippedGames = dbHelper.getSkippedGamesFromDb()
            return skippedGames
        }

        fun unSkipAllGames(context: Context) {
            val dbHelper = SkippedGamesOpenHelper(context.applicationContext)
            dbHelper.unskipAllGames()
        }
        fun unSkipGame(context: Context, game: String) {
            val dbHelper = SkippedGamesOpenHelper(context.applicationContext)
            dbHelper.unskipGame(game)
        }
    }

    class SkippedGamesOpenHelper internal constructor(context: Context) : SQLiteOpenHelper(context, SkippedGames.DATABASE_NAME, null, SkippedGames.DATABASE_VERSION) {

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion != newVersion) {
                // Simplest implementation is to drop all old tables and recreate them
                db.execSQL("DROP TABLE IF EXISTS " + SkippedGames.SKIPPED_GAMES_TABLE_NAME)
                onCreate(db)
            }
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SkippedGames.SKIPPED_GAMES_TABLE_CREATE)
        }

        // Insert a Game into the database
        fun addSkippedGame(game: String) {

            // Create and/or open the database for writing
            val db = writableDatabase

            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction()

            try {
                val values = ContentValues()
                values.put(SkippedGames.KEY_GAME, game)

                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(SKIPPED_GAMES_TABLE_NAME, null, values)

                db.setTransactionSuccessful()
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Error while trying to add Skipped Game to database")
            } finally {
                db.endTransaction()
            }
        }
        fun unskipGame(game: String) {
            // Make sure the game string doesn't contain single quotes or it will mess the query up.
            // Instead it should have double quotes
            val theGame = game.replace("'", "''")

            val db = writableDatabase
            db.beginTransaction()
            try {
                db.delete(SKIPPED_GAMES_TABLE_NAME, KEY_GAME + "='" + theGame + "'", null)
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                Log.d(TAG, "Error while trying to delete skipped game from database")
            } finally {
                db.endTransaction()
            }
        }

        fun getSkippedGamesFromDb(): List<String> {
            val games = mutableListOf<String>()

            // SELECT * FROM SkippedGamesTable
            val SKIPPED_GAMES_SELECT_QUERY = "SELECT * FROM ${SkippedGames.SKIPPED_GAMES_TABLE_NAME}"
            val db: SQLiteDatabase
            try {
                db = this.readableDatabase
            } catch (s: SQLException) {
                // We sometimes get an error opening the database.
                // Don't save the watched time. 's ok. Maybe next time.
                return listOf()
            }

            val cursor = db.rawQuery(SKIPPED_GAMES_SELECT_QUERY, null)
            try {
                if (cursor.moveToFirst()) {
                    do {
                        val game = cursor.getString(cursor.getColumnIndex(SkippedGames.KEY_GAME))
                        games.add(game)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Error while trying to get skipped games from database")
            } finally {
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
                db.close()
            }

            return games.toList()
        }


        fun unskipAllGames() {
            val db: SQLiteDatabase
            try {
                db = this.writableDatabase
            } catch (s: SQLException) {
                // Hopefully this doesn't happen...
                return
            }
            db.beginTransaction()
            try {
                // Order of deletions is important when foreign key relationships exist.
                db.delete(SkippedGames.SKIPPED_GAMES_TABLE_NAME, null, null)
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Error while trying to delete all skipped games")
            } finally {
                db.endTransaction()
                db.close()
            }
        }
    }
}