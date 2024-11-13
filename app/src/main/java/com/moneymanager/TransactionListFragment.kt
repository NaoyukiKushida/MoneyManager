package com.moneymanager

import TransactionListAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.moneymanager.databinding.FragmentTransactionListBinding
import com.moneymanager.data.models.TransactionListModel
import com.moneymanager.data.models.repository.TransactionData
import com.moneymanager.entity.TransactionEntity
import com.moneymanager.repositories.CategoryRepository
import com.moneymanager.repositories.TransactionRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionListAdapter

    private val config = RealmConfiguration.Builder(schema = setOf(TransactionEntity::class)).build()
    private val realm = Realm.open(config)
    private var transactionsDatas: List<TransactionData> = listOf()

    // ログイン状態の変化を監視するリスナー
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser != null) {
            // ログイン済みの場合、Realm データを取得してリストを更新
            fetchAndDisplayTransactions()
        } else {
            // ログアウト時は空のリストを表示
            adapter = TransactionListAdapter(emptyList()) { }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding を使用してレイアウトを inflate
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        val view = _binding?.root

        _binding?.let { binding ->
            // RecyclerView を取得
            recyclerView = binding.transactionListRecyclerView

            // 初期表示時にログイン状態を確認
            val myApplication = activity?.application as? MyApplication
            if (myApplication != null && myApplication.auth.currentUser != null) {
                // ログイン済みの場合、取引データを取得して表示
                fetchAndDisplayTransactions()
            } else {
                // 未ログインの場合は空のリストを表示
                adapter = TransactionListAdapter(emptyList()) { }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

            // 追加ボタンのクリックリスナーを設定
            binding.addTransactionButton.setOnClickListener {
                val intent = Intent(requireContext(), TransactionEditActivity::class.java)
                startActivity(intent)
            }
        }

        return view
    }

    // Realm からデータを取得して表示する関数
    private fun fetchAndDisplayTransactions() {
        lifecycleScope.launch {
            // suspend 関数 getTransaction をコルーチン内で呼び出す
            transactionsDatas = TransactionRepository.getTransaction(realm)

            // 日付の昇順でソート
            val sortedTransactions = transactionsDatas.sortedBy { it.date }

            // 日付でグループ化し、日付ごとにリストを作成
            val groupedTransactions = sortedTransactions.groupBy {
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val date = try {
                    it.date?.let { date -> dateFormat.parse(date) }
                } catch (e: ParseException) {
                    // エラー処理: パースに失敗した場合
                    Date() // または適切なデフォルト値を設定
                }
                if (date != null) {
                    dateFormat.format(date)
                } else {
                    "" // date が null の場合の処理を追加
                }
            }

            // グループ化されたデータから TransactionListModel のリストを作成
            val transactionListModels = groupedTransactions.flatMap { (date, transactions) ->
                transactions.mapIndexed { index, data ->
                    // カテゴリ名に応じてアイコンを設定
                    val category = CategoryRepository.getCategoryById(data.category)
                    val iconResId = when (category?.name) {
                        "食費" -> R.drawable.ic_food
                        "日用品" -> R.drawable.ic_daily
                        "趣味・娯楽" -> R.drawable.ic_hobby
                        "交際費" -> R.drawable.ic_entertainment
                        "交通費" -> R.drawable.ic_traffic
                        "衣服・美容" -> R.drawable.ic_cloths
                        "健康・医療" -> R.drawable.ic_heart
                        "自動車" -> R.drawable.ic_bicycle
                        else -> R.drawable.ic_question // デフォルトのアイコン
                    }
                    // TransactionListModel を作成
                    TransactionListModel(
                        data.transactionId,
                        (if (index == 0) date.toString() else ""), // 日付はグループの先頭のみ表示
                        data.content,
                        if (data.balanceType == 0) -data.amount else data.amount, // 支出の場合は金額を負の値にする
                        iconResId
                    )
                }
            }

            // アダプターを作成し、RecyclerView に設定
            adapter = TransactionListAdapter(transactionListModels) { transaction: TransactionListModel ->
                // リストアイテムのクリックリスナーを設定
                val intent = Intent(requireContext(), TransactionEditActivity::class.java)
                intent.putExtra("transactionId", transaction.transactionId)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onStart() {
        super.onStart()
        // AuthStateListenerを登録
        (activity?.application as MyApplication).auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        // AuthStateListenerを解除
        (activity?.application as MyApplication).auth.removeAuthStateListener(authStateListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}