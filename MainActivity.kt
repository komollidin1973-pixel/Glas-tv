package com.glasstv.launcher

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var grid: GridLayout
    private lateinit var title: TextView
    private val appCards = mutableListOf<View>()
    private var currentSection = "Главная"
    private val sections = listOf("Главная", "Видео", "Музыка", "Игры", "Все приложения", "Настройки")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 13, 23)
        window.navigationBarColor = Color.rgb(8, 13, 23)
        buildUi()
        showApps("Главная")
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun bg(colors: IntArray, radius: Float, stroke: Int = 0): GradientDrawable =
        GradientDrawable(GradientDrawable.Orientation.TL_BR, colors).apply {
            cornerRadius = dp(radius.toInt()).toFloat()
            if (stroke != 0) setStroke(dp(1), stroke)
        }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            isFocusable = false
        }

    private fun buildUi() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(22), dp(18), dp(22), dp(18))
            background = bg(intArrayOf(Color.rgb(7, 12, 23), Color.rgb(16, 29, 51), Color.rgb(7, 12, 23)), 0f)
        }

        val sidebar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(18), dp(14), dp(18))
            background = bg(intArrayOf(0xCC26364B.toInt(), 0xAA101A2B.toInt()), 24f, 0x557FA9D8)
            layoutParams = LinearLayout.LayoutParams(dp(218), ViewGroup.LayoutParams.MATCH_PARENT).apply {
                rightMargin = dp(26)
            }
        }
        sidebar.addView(text("◈  GLASS TV", 21f, Color.WHITE, true).apply {
            setPadding(dp(12), dp(4), 0, dp(24))
        })
        sections.forEachIndexed { index, section ->
            val item = TextView(this).apply {
                text = "${listOf("⌂", "▣", "♫", "◉", "▦", "⚙")[index]}    $section"
                textSize = 16f
                setTextColor(if (section == currentSection) Color.WHITE else 0xFFD1DCEC.toInt())
                setPadding(dp(14), dp(15), dp(8), dp(15))
                isFocusable = true
                isFocusableInTouchMode = true
                background = if (section == currentSection)
                    bg(intArrayOf(0xCC3978B8.toInt(), 0xAA24527F.toInt()), 15f, 0x887FC5FF.toInt())
                else bg(intArrayOf(0x223B5068, 0x113B5068), 15f)
                setOnClickListener { selectSection(section) }
                setOnFocusChangeListener { v, focused ->
                    if (focused) v.background = bg(intArrayOf(0xDD3978B8.toInt(), 0xCC24527F.toInt()), 15f, 0xCC9BD6FF.toInt())
                    else v.background = if (section == currentSection)
                        bg(intArrayOf(0xCC3978B8.toInt(), 0xAA24527F.toInt()), 15f, 0x887FC5FF.toInt())
                    else bg(intArrayOf(0x223B5068, 0x113B5068), 15f)
                }
            }
            sidebar.addView(item, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54)).apply {
                bottomMargin = dp(5)
            })
            if (index == 0) item.requestFocus()
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        }
        title = text("Добро пожаловать", 30f, Color.WHITE, true)
        content.addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(56)))
        content.addView(text("Ваши приложения на большом экране", 15f, 0xFF9FB1C8.toInt()).apply {
            setPadding(0, 0, 0, dp(18))
        })
        val scroll = ScrollView(this)
        grid = GridLayout(this).apply {
            columnCount = 4
            useDefaultMargins = false
        }
        scroll.addView(grid)
        content.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        root.addView(sidebar)
        root.addView(content)
        setContentView(root)
    }

    private fun selectSection(section: String) {
        currentSection = section
        title.text = when (section) {
            "Видео" -> "Видео"
            "Музыка" -> "Музыка"
            "Игры" -> "Игры"
            "Настройки" -> "Настройки"
            "Все приложения" -> "Все приложения"
            else -> "Добро пожаловать"
        }
        showApps(section)
    }

    private fun showApps(section: String) {
        grid.removeAllViews()
        appCards.clear()
        val pm = packageManager
        val leanback = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val normal = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val found = (pm.queryIntentActivities(leanback, 0) + pm.queryIntentActivities(normal, 0))
            .distinctBy { it.activityInfo.packageName }
            .filter { it.activityInfo.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        val filtered = when (section) {
            "Видео" -> found.filter { match(it.loadLabel(pm).toString(), "video", "youtube", "netflix", "kino", "ivi", "media", "player", "film", "vlc", "теле", "кино", "фильм") }
            "Музыка" -> found.filter { match(it.loadLabel(pm).toString(), "music", "spotify", "audio", "музык", "radio", "sound") }
            "Игры" -> found.filter { it.activityInfo.applicationInfo.category == android.content.pm.ApplicationInfo.CATEGORY_GAME }
            else -> found
        }

        if (filtered.isEmpty()) {
            val empty = text(if (section == "Настройки") "Настройки появятся в следующем обновлении" else "Приложения не найдены", 18f, 0xFFB8C7D9.toInt())
            grid.addView(empty)
            return
        }

        filtered.forEach { info ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(10), dp(14), dp(10), dp(12))
                background = bg(intArrayOf(0xAA26364B.toInt(), 0x88202D40.toInt()), 20f, 0x446C91B8)
                isFocusable = true
                isFocusableInTouchMode = true
                setOnFocusChangeListener { v, focused ->
                    v.scaleX = if (focused) 1.07f else 1f
                    v.scaleY = if (focused) 1.07f else 1f
                    v.background = if (focused) bg(intArrayOf(0xEE3978B8.toInt(), 0xCC244A70.toInt()), 20f, 0xCC9BD6FF.toInt())
                        else bg(intArrayOf(0xAA26364B.toInt(), 0x88202D40.toInt()), 20f, 0x446C91B8)
                }
                setOnClickListener {
                    val launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                    if (launch != null) startActivity(launch)
                    else Toast.makeText(this@MainActivity, "Не удалось открыть приложение", Toast.LENGTH_SHORT).show()
                }
            }
            val icon = ImageView(this).apply {
                setImageDrawable(info.loadIcon(pm))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            card.addView(icon, LinearLayout.LayoutParams(dp(62), dp(62)))
            card.addView(text(info.loadLabel(pm).toString(), 14f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
                maxLines = 2
                setPadding(0, dp(10), 0, 0)
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(46)))
            val params = GridLayout.LayoutParams().apply {
                width = dp(145); height = dp(145)
                setMargins(dp(7), dp(7), dp(7), dp(7))
            }
            grid.addView(card, params)
            appCards.add(card)
        }
        if (appCards.isNotEmpty()) appCards.first().requestFocus()
    }

    private fun match(label: String, vararg terms: String): Boolean =
        terms.any { label.contains(it, ignoreCase = true) }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(this, "GLASS TV", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
