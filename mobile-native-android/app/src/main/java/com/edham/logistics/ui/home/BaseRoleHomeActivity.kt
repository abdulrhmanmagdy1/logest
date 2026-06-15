package com.edham.logistics.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.app.UserRole
import com.edham.logistics.ui.BaseActivity
import com.edham.logistics.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView

/**
 * Common scaffold for role-specific home screens.
 *
 * Subclasses provide:
 *  - The role string used in the toolbar title and nav header.
 *  - The drawer menu resource.
 *  - The dashboard content (built into a vertical [LinearLayout]).
 *
 * The base class handles the toolbar, drawer wiring, nav header binding,
 * generic menu items (Settings / Support / Logout) and the back-press
 * behaviour for an open drawer.
 */
abstract class BaseRoleHomeActivity : BaseActivity() {

    protected lateinit var session: AuthSession
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    protected lateinit var contentContainer: LinearLayout

    abstract val role: UserRole
    @get:MenuRes abstract val menuRes: Int
    abstract val toolbarTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_home)

        session = AuthSession.get(this)

        drawer            = findViewById<DrawerLayout>(R.id.drawerLayout)
        navigationView    = findViewById<NavigationView>(R.id.navigationView)
        contentContainer  = findViewById<LinearLayout>(R.id.contentContainer)

        setupToolbar()
        setupNavigationView()
        bindNavHeader()

        contentContainer.removeAllViews()
        buildDashboard(contentContainer)
    }

    /** Subclasses populate the scrollable dashboard area with their widgets. */
    abstract fun buildDashboard(parent: LinearLayout)

    /** Optional hook so subclasses can react to non-generic menu taps. */
    protected open fun onCustomMenuItemSelected(item: MenuItem): Boolean = false

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = toolbarTitle
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
            else drawer.openDrawer(GravityCompat.START)
        }
    }

    private fun setupNavigationView() {
        navigationView.menu.clear()
        navigationView.inflateMenu(menuRes)
        navigationView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_logout -> { logout(); true }
                else -> {
                    if (!onCustomMenuItemSelected(item)) {
                        // Default: just toast which item was tapped (placeholder for
                        // the real navigation graph that ships in Phase 3).
                        showComingSoon(item.title?.toString().orEmpty())
                    }
                    true
                }
            }
        }
    }

    private fun bindNavHeader() {
        val header = navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.navHeaderName).text =
            session.displayName ?: getString(role.titleRes)
        header.findViewById<TextView>(R.id.navHeaderEmail).text =
            session.email.orEmpty()
        header.findViewById<TextView>(R.id.navHeaderRole).text =
            getString(role.titleRes)
    }

    private fun logout() {
        session.signOut()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // ----------------------------------------------------------- UI helpers

    protected fun dp(value: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()

    protected fun addSectionTitle(parent: LinearLayout, text: String) {
        parent.addView(TextView(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(20), 0, dp(12))
        })
    }

    protected fun addStatCard(
        parent: ViewGroup,
        title: String,
        value: String,
        accent: Int = ContextCompat.getColor(this, R.color.brand_primary)
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                setColor(Color.parseColor("#062E54"))
                setStroke(dp(1), Color.parseColor("#22FFFFFF"))
            }
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            .apply { setMargins(dp(4), dp(4), dp(4), dp(4)) }
        card.layoutParams = lp

        card.addView(TextView(this).apply {
            this.text = title
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
        })
        card.addView(TextView(this).apply {
            this.text = value
            setTextColor(accent)
            textSize = 22f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        })
        parent.addView(card)
    }

    protected fun addStatRow(parent: LinearLayout, items: List<Pair<String, String>>) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }
        items.forEach { (title, value) -> addStatCard(row, title, value) }
        parent.addView(row)
    }

    protected fun addListItem(
        parent: LinearLayout,
        title: String,
        subtitle: String,
        trailing: String? = null
    ) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GradientDrawable().apply {
                cornerRadius = dp(14).toFloat()
                setColor(Color.parseColor("#062E54"))
            }
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        textCol.addView(TextView(this).apply {
            this.text = title
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        })
        textCol.addView(TextView(this).apply {
            this.text = subtitle
            setTextColor(Color.parseColor("#99FFFFFF"))
            textSize = 12f
            setPadding(0, dp(2), 0, 0)
        })
        container.addView(textCol)

        if (trailing != null) {
            container.addView(TextView(this).apply {
                this.text = trailing
                setTextColor(ContextCompat.getColor(this@BaseRoleHomeActivity, R.color.brand_primary))
                textSize = 13f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            })
        }

        parent.addView(container)
    }

    protected fun showComingSoon(label: String) {
        android.widget.Toast.makeText(
            this,
            "$label — قريباً",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}
