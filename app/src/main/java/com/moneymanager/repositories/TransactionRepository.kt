package com.moneymanager.repositories

import android.util.Log
import com.moneymanager.entity.TransactionEntity
import com.moneymanager.data.models.repository.TransactionData
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date

object TransactionRepository {

    // TransactionData から TransactionEntity への変換
    fun fromDataToEntity(data: TransactionData): TransactionEntity {
        return TransactionEntity().apply {
            transactionId = data.transactionId
            balanceType = data.balanceType
            category = data.category
            date = data.date
            amount = data.amount
            content = data.content
        }
    }

    // TransactionEntity から TransactionData への変換
    fun fromEntityToData(entity: TransactionEntity): TransactionData {
        return TransactionData(
            transactionId = entity.transactionId,
            balanceType = entity.balanceType,
            category = entity.category,
            date = entity.date ?:Date().toString(),
            amount = entity.amount ?: 0,
            content = entity.content ?: ""
        )
    }

    // Transaction を Realm に保存
    suspend fun saveTransaction(realm: Realm, data: TransactionData) {
        return withContext(Dispatchers.IO) {
            realm.writeBlocking {
                val entity = fromDataToEntity(data)
                copyToRealm(entity, UpdatePolicy.ALL)
            }
        }
    }

    // Transaction 一覧を取得
    suspend fun getTransaction(realm: Realm): List<TransactionData> {
        return withContext(Dispatchers.IO) {
            // 全件取得
            val entities = realm.query<TransactionEntity>().find()
            entities.map { fromEntityToData(it) } // EntitiesをDataに変換
        }
    }

    // Transaction 一覧を取得
//    fun getTransactionsFlow(realm: Realm): Flow<List<TransactionData>> {
//        return realm.query<TransactionEntity>().asFlow().map { changes: ResultsChange<TransactionEntity> ->
//            when (changes) {
//                is InitialResults -> {
//                    Log.d("Realm", "Initial results: ${changes.list}")
//                    changes.list.map { fromEntityToData(it) }
//                }
//                is UpdatedResults -> {
//                    Log.d("Realm", "Updated results: ${changes.list}")
//                    changes.list.map { fromEntityToData(it) }
//                }
//                else -> {
//                    emptyList()
//                }
//            }
//        }
//    }

    /**
     * 指定されたIDのTransactionをRealmから削除します。
     *
     * @param realm 削除対象のTransactionが存在するRealmインスタンス。
     * @param id 削除するTransactionのID。
     */
    suspend fun deleteTransactions(realm: Realm, id: String) {
        withContext(Dispatchers.IO) {
            realm.writeBlocking {
                val frozenEntity = realm.query<TransactionEntity>("transactionId == $0", id).find().firstOrNull()
                if (frozenEntity != null) {
                    val liveEntity = findLatest(frozenEntity)
                    if (liveEntity != null) {
                        Log.d("delete", "target: $liveEntity")
                        delete(liveEntity)
                        val afterEntity = realm.query<TransactionEntity>().find()
                        Log.d("delete", "after: $afterEntity")
                    } else {
                        Log.e("delete", "Live entity not found for frozen entity with ID: $id")
                    }
                } else {
                    Log.e("delete", "Entity with ID $id not found.")
                }
            }
        }
    }
}