package com.moneymanager.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class TransactionEntity : RealmObject {
    @PrimaryKey
    var transactionId: String = ""
    var balanceType: Int = 0
    var category: Int = 1
    var date: String? = null
    var amount: Int? = null
    var content: String? = null
}