package com.moneymanager.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.moneymanager.R

object ToolbarUtils {

    fun setupToolbar(
        activity: AppCompatActivity,
        toolbarContainer: ConstraintLayout, // ToolbarではなくConstraintLayoutを受け取る
        title: String,
        leftButtonType: LeftButtonType = LeftButtonType.BACK,
        onLogoutClick: (() -> Unit)? = null,
        rightButtonDrawableId: Int? = null, // 右ボタンのdrawable IDを追加
        onRightButtonClick: (() -> Unit)? = null // 右ボタンクリック時のコールバックを追加
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

    private fun setupLogoutButton(activity: AppCompatActivity, button: ImageView, onLogoutClick: (() -> Unit)?) {
        button.setImageResource(R.drawable.logout)
        button.setPadding(16)
        button.setOnClickListener { onLogoutClick?.invoke() }
    }

    private fun setupBackButton(activity: AppCompatActivity, button: ImageView) {
        button.setImageResource(R.drawable.arrow_back)
        button.setPadding(16)
        button.setOnClickListener { activity.onBackPressedDispatcher.onBackPressed() }
    }
}

enum class LeftButtonType {
    LOGOUT, BACK, NONE
}
