package com.moneymanager

import TransactionListAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneymanager.databinding.FragmentTransactionListBinding
import com.moneymanager.data.models.TransactionListModel

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null

    // _binding!! を使わずにアクセスするための関数
    private fun getBinding(): FragmentTransactionListBinding {
        return _binding ?: throw IllegalStateException("Binding is not initialized.")
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionListAdapter

    private fun createDummyData(): List<TransactionListModel> {
        return listOf(
            TransactionListModel(1, "2023/08/01", "食費", -5000, R.drawable.ic_list),
            TransactionListModel(2, "2023/08/05", "給料", 300000, R.drawable.ic_list),
            TransactionListModel(3, "2023/08/10", "交通費", -2000, R.drawable.ic_list),
            // ... 他のダミーデータとアイコンリソースIDを追加
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        val view = _binding?.root

        _binding?.let { binding ->
            recyclerView = binding.transactionListRecyclerView
            adapter = TransactionListAdapter(createDummyData()) { transaction: TransactionListModel ->
                val intent = Intent(requireContext(), TransactionEditActivity::class.java)
                intent.putExtra("transactionId", transaction.transactionId) // transactionId を渡す
                startActivity(intent)
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            binding.addTransactionButton.setOnClickListener {
                val intent = Intent(requireContext(), TransactionEditActivity::class.java)
                startActivity(intent)
            }
        }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
