package com.example.evalapp_grupo12.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(context: Context) : SQLiteOpenHelper(context, "evalapp.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE usuario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "correo TEXT, " +
                    "clave TEXT, " +
                    "rol TEXT)"
        )

        db.execSQL(
            "CREATE TABLE documento (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "usuario_id INTEGER, " +
                    "titulo TEXT, " +
                    "tipo TEXT, " +
                    "uri TEXT)"
        )

        db.execSQL(
            "INSERT INTO usuario (nombre, correo, clave, rol) " +
                    "VALUES ('Instructor Demo', 'instructor@demo.cl', 'demo123', 'Instructor')"
        )

        db.execSQL(
            "INSERT INTO usuario (nombre, correo, clave, rol) " +
                    "VALUES ('Docente Demo', 'docente@demo.cl', 'demo123', 'Docente')"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS documento")
        onCreate(db)
    }
}
