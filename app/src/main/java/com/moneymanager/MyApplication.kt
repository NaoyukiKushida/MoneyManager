package com.moneymanager

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.moneymanager.entity.TransactionEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApplication : Application() {
    lateinit var auth: FirebaseAuth
    private var realm: Realm? = null // null 許容型に変更

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
    }

    // ログイン時にRealmデータベースを作成
    fun createRealm() {
        val config = RealmConfiguration.create(setOf(TransactionEntity::class))
        realm = Realm.open(config)
    }

    // ログアウト時にRealmデータベースを削除
    fun deleteRealm() {
        realm?.close() // nullチェックを追加
        realm?.configuration?.let { Realm.deleteRealm(it) } // nullチェックを追加
        realm = null // ログアウト後にrealmをnullに設定
    }
}