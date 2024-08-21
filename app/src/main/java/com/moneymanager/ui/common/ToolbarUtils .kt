package com.moneymanager.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.moneymanager.R

object ToolbarUtils {

    /**
     * `ConstraintLayout` 内のツールバーを設定します。
     *
     * @param activity 現在のアクティビティ (`AppCompatActivity`)。
     * @param toolbarContainer ツールバーの要素（左ボタン、タイトル、右ボタン）を含む `ConstraintLayout`。
     * @param title ツールバーのタイトルに表示するテキスト。
     * @param leftButtonType 表示する左ボタンの種類 (LOGOUT, BACK, または NONE)。デフォルトは BACK。
     * @param onLogoutClick ログアウトボタンがクリックされたときに実行するコールバック（オプション）。
     * @param rightButtonDrawableId 右ボタンに表示するドロウアブルのリソースID（オプション）。
     * @param onRightButtonClick 右ボタンがクリックされたときに実行するコールバック（オプション）。
     */
    fun setupToolbar(
        activity: AppCompatActivity,
        toolbarContainer: ConstraintLayout, // ToolbarではなくConstraintLayoutを受け取る
        title: String,
        leftButtonType: LeftButtonType = LeftButtonType.BACK,
        onLogoutClick: (() -> Unit)? = null,
        rightButtonDrawableId: Int? = null, // 右ボタンのdrawable IDを追加
        onRightButtonClick: (() -> Unit)? = null, // 右ボタンクリック時のコールバックを追加
    ) {
        val leftButton: ImageView = toolbarContainer.findViewById(R.id.left_button)
        val titleTextView: TextView = toolbarContainer.findViewById(R.id.title)
        val rightButton: ImageView = toolbarContainer.findViewById(R.id.right_button)

        titleTextView.text = title

        when (leftButtonType) {
            LeftButtonType.LOGOUT -> setupLogoutButton(activity, leftButton, onLogoutClick)
            LeftButtonType.BACK -> setupBackButton(activity, leftButton)
            LeftButtonType.NONE -> leftButton.visibility = View.GONE
        }

        // 右ボタンの設定
        rightButtonDrawableId?.let {
            rightButton.setImageResource(it)
            rightButton.visibility = View.VISIBLE
            rightButton.setOnClickListener { onRightButtonClick?.invoke() }
        } ?: run {
            rightButton.visibility = View.GONE
        }
    }

    /**
     * 指定された ImageView をログアウトボタンとして設定します。
     *
     * @param activity 現在のアクティビティ (`AppCompatActivity`)。
     * @param button 設定する ImageView。
     * @param onLogoutClick ボタンがクリックされたときに実行するコールバック。
     */
    private fun setupLogoutButton(
        activity: AppCompatActivity,
        button: ImageView,
        onLogoutClick: (() -> Unit)?,
    ) {
        button.setImageResource(R.drawable.logout)
        button.setPadding(16)
        button.setOnClickListener { onLogoutClick?.invoke() }
    }

    /**
     * 指定された ImageView を戻るボタンとして設定します。
     *
     * @param activity 現在のアクティビティ (`AppCompatActivity`)。
     * @param button 設定する ImageView。
     */
    private fun setupBackButton(activity: AppCompatActivity, button: ImageView) {
        button.setImageResource(R.drawable.arrow_back)
        button.setPadding(16)
        button.setOnClickListener { activity.onBackPressedDispatcher.onBackPressed() }
    }
}

enum class LeftButtonType {
    LOGOUT, BACK, NONE
}
