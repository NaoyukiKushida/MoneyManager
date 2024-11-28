import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneymanager.data.models.TransactionListModel
import com.moneymanager.databinding.TransactionListItemBinding

class TransactionListAdapter(
    private val transactionList: List<TransactionListModel>,
    private val onItemClick: (TransactionListModel) -> Unit
) : RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: TransactionListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val viewBinding = TransactionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        if (transaction.date.isNotEmpty()) {
            holder.binding.textViewDate.visibility = View.VISIBLE
            holder.binding.textViewDate.text = transaction.date
        } else {
            holder.binding.textViewDate.visibility = View.GONE
        }
        holder.binding.imageViewCategoryIcon.setImageResource(transaction.iconResId)
        holder.binding.textViewContent.text = transaction.content
        holder.binding.textViewAmount.text = "${transaction.amount}円"

        holder.itemView.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactionList.size
}