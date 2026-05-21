package com.doceriadaduda.data.local.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.doceriadaduda.data.local.local.dao.DespesaDao
import com.doceriadaduda.data.local.local.dao.FechamentoDao
import com.doceriadaduda.data.local.local.dao.ProdutoDao
import com.doceriadaduda.data.local.local.dao.VendaDao
import com.doceriadaduda.model.Despesa
import com.doceriadaduda.model.Fechamento
import com.doceriadaduda.model.Produto
import com.doceriadaduda.model.Venda

@Database(entities = [Produto::class, Venda::class, Despesa::class, Fechamento::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun produtoDao(): ProdutoDao
    abstract fun vendaDao(): VendaDao
    abstract fun despesaDao(): DespesaDao
    abstract fun fechamentoDao(): FechamentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "doceria.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE vendas ADD COLUMN taxaCartao REAL NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE vendas ADD COLUMN valorLiquido REAL NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE produtos ADD COLUMN precoCusto REAL NOT NULL DEFAULT 0.0")
            }
        }
    }
}
