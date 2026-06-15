package com.edham.logistics.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton

/**
 * Seven-page onboarding flow shown on first launch only. Skipping or
 * finishing both mark onboarding as completed and route the user to the
 * login screen.
 */
class OnboardingActivity : AppCompatActivity() {

    private val pages = listOf(
        OnboardingPage(R.string.onb_title_1, R.string.onb_desc_1, R.drawable.onb_hero_1),
        OnboardingPage(R.string.onb_title_2, R.string.onb_desc_2, R.drawable.onb_hero_2),
        OnboardingPage(R.string.onb_title_3, R.string.onb_desc_3, R.drawable.onb_hero_3),
        OnboardingPage(R.string.onb_title_4, R.string.onb_desc_4, R.drawable.onb_hero_4),
        OnboardingPage(R.string.onb_title_5, R.string.onb_desc_5, R.drawable.onb_hero_5),
        OnboardingPage(R.string.onb_title_6, R.string.onb_desc_6, R.drawable.onb_hero_6),
        OnboardingPage(R.string.onb_title_7, R.string.onb_desc_7, R.drawable.onb_hero_7),
    )

    private lateinit var pager: ViewPager2
    private lateinit var dotsContainer: LinearLayout
    private lateinit var btnNext: MaterialButton
    private lateinit var btnSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_v2)

        pager         = findViewById(R.id.onboardingPager)
        dotsContainer = findViewById(R.id.dotsContainer)
        btnNext       = findViewById(R.id.btnNext)
        btnSkip       = findViewById(R.id.btnSkip)

        // Set layout direction to RTL for Arabic swiping direction
        pager.layoutDirection = View.LAYOUT_DIRECTION_RTL

        pager.adapter = OnboardingAdapter(pages)
        renderDots(0)

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                renderDots(position)
                btnNext.setText(
                    if (position == pages.lastIndex) R.string.onb_get_started
                    else R.string.onb_next
                )
            }
        })

        btnNext.setOnClickListener {
            if (pager.currentItem < pages.lastIndex) {
                pager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }
        btnSkip.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        AuthSession.get(this).onboardingCompleted = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun renderDots(activeIndex: Int) {
        dotsContainer.removeAllViews()
        val density = resources.displayMetrics.density
        repeat(pages.size) { i ->
            val dot = View(this)
            val params = LinearLayout.LayoutParams(
                if (i == activeIndex) (24 * density).toInt() else (8 * density).toInt(),
                (8 * density).toInt()
            ).apply { marginEnd = (6 * density).toInt() }
            dot.layoutParams = params
            dot.background = if (i == activeIndex)
                getDrawable(R.drawable.dot_indicator_active)
            else
                getDrawable(R.drawable.dot_indicator_inactive)
            dotsContainer.addView(dot)
        }
    }

    // ---------------------------------------------------------------- Adapter

    private data class OnboardingPage(val titleRes: Int, val descRes: Int, val iconRes: Int)

    private class OnboardingAdapter(
        private val pages: List<OnboardingPage>
    ) : RecyclerView.Adapter<OnboardingAdapter.PageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.page_onboarding, parent, false)
            return PageViewHolder(view)
        }

        override fun getItemCount(): Int = pages.size

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            val page = pages[position]
            holder.icon.setImageResource(page.iconRes)
            holder.title.setText(page.titleRes)
            holder.desc.setText(page.descRes)
        }

        class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.onbIcon)
            val title: TextView = view.findViewById(R.id.onbTitle)
            val desc: TextView  = view.findViewById(R.id.onbDesc)
        }
    }
}
