package com.example.expencetracker2.di

import android.content.Context
import androidx.room.Room
import com.example.expencetracker2.data.auth.repoimpl.AuthRepoImpl
import com.example.expencetracker2.data.tracsaction.repoImpl.TransactionRepoImpl
import com.example.expencetracker2.data.tracsaction.local.db.ExpenseDatabase
import com.example.expencetracker2.data.tracsaction.local.dao.TransactionDao
import com.example.expencetracker2.domain.auth.repository.AuthRepo
import com.example.expencetracker2.domain.transaction.repository.TransactionRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object ProviderModule {
        @Provides
        @Singleton
        fun provideSupabaseClient(): SupabaseClient {
            return createSupabaseClient(
                supabaseUrl = "https://oslcfyaleocudgrerykh.supabase.co",
                supabaseKey = "sb_publishable_Y9sxT_PwbqUZcp5x0Y2NGQ_iSwQcsMP"
            ) {
                install(Postgrest)
                install(Auth)
                install(Realtime)
                install(Storage)
            }
        }

        @Provides
        @Singleton
        fun provideSupabaseAuth(client: SupabaseClient): Auth = client.auth

        @Provides
        @Singleton
        fun provideSupabasePostgrest(client: SupabaseClient): Postgrest = client.postgrest

        @Provides
        @Singleton
        fun provideSupabaseStorage(client: SupabaseClient): Storage = client.storage

        @Provides
        @Singleton
        fun provideSupabaseRealtime(client: SupabaseClient): Realtime = client.realtime

        @Provides
        @Singleton
        fun provideExpenseDatabase(@ApplicationContext context: Context): ExpenseDatabase {
            return Room.databaseBuilder(
                context,
                ExpenseDatabase::class.java,
                "expense_db"
            ).build()
        }

        @Provides
        @Singleton
        fun provideTransactionDao(database: ExpenseDatabase): TransactionDao {
            return database.transactionDao()
        }
    }

    @Binds
    @Singleton
    abstract fun bindAuthRepo(repoImpl: AuthRepoImpl): AuthRepo

    @Binds
    @Singleton
    abstract fun bindTransactionRepo(repoImpl: TransactionRepoImpl): TransactionRepo
}