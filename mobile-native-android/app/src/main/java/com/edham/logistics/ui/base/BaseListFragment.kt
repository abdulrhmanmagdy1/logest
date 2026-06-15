package com.edham.logistics.ui.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edham.logistics.R

/**
 * Base fragment for list screens with empty state, loading, and error handling
 */
abstract class BaseListFragment : Fragment() {

    protected var swipeRefreshLayout: SwipeRefreshLayout? = null
    protected var recyclerView: RecyclerView? = null
    protected var shimmerContainer: FrameLayout? = null
    protected var emptyStateContainer: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh)
        shimmerContainer = view?.findViewById(R.id.shimmer_container)
        emptyStateContainer = view?.findViewById(R.id.empty_state_container)
        
        swipeRefreshLayout?.setColorSchemeResources(
            R.color.primary,
            R.color.success,
            R.color.warning
        )
        swipeRefreshLayout?.setProgressBackgroundColorSchemeResource(R.color.card_background)
        swipeRefreshLayout?.setOnRefreshListener { onRefresh() }
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun onRefresh()

    protected fun setupEmptyState(
        iconRes: Int = R.drawable.ic_logo_white,
        title: String = getString(R.string.no_data_title),
        subtitle: String = getString(R.string.no_data_subtitle),
        actionText: String? = null,
        actionCallback: (() -> Unit)? = null
    ) {
        emptyStateContainer?.findViewById<ImageView>(R.id.iv_empty_icon)?.setImageResource(iconRes)
        emptyStateContainer?.findViewById<TextView>(R.id.tv_empty_title)?.text = title
        emptyStateContainer?.findViewById<TextView>(R.id.tv_empty_subtitle)?.text = subtitle

        val btnAction = emptyStateContainer?.findViewById<TextView>(R.id.btn_empty_action)
        if (actionText != null && actionCallback != null) {
            btnAction?.visibility = View.VISIBLE
            btnAction?.text = actionText
            btnAction?.setOnClickListener { actionCallback() }
        } else {
            btnAction?.visibility = View.GONE
        }
    }

    protected fun showLoading() {
        shimmerContainer?.visibility = View.VISIBLE
        recyclerView?.visibility = View.GONE
        emptyStateContainer?.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun hideLoading() {
        shimmerContainer?.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun showEmptyState() {
        emptyStateContainer?.visibility = View.VISIBLE
        recyclerView?.visibility = View.GONE
        shimmerContainer?.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun showContent() {
        recyclerView?.visibility = View.VISIBLE
        emptyStateContainer?.visibility = View.GONE
        shimmerContainer?.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun showError() {
        emptyStateContainer?.visibility = View.VISIBLE
        recyclerView?.visibility = View.GONE
        shimmerContainer?.visibility = View.GONE
        swipeRefreshLayout?.isRefreshing = false
    }

    protected fun checkEmptyState(itemCount: Int) {
        if (itemCount == 0) {
            showEmptyState()
        } else {
            showContent()
        }
    }
}
