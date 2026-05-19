package com.anagha.cinemacipher

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {

    private var formulaInput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        applyNeoBrutalistStyling()

        val tvDisplay = findViewById<TextView>(R.id.tvDisplay)
        val tvGenreLabel = findViewById<TextView>(R.id.tvGenreLabel)
        val btnInfo = findViewById<Button>(R.id.btnInfo)
        val btnDecode = findViewById<Button>(R.id.btnDecode)
        val btnClearReel = findViewById<Button>(R.id.btnClearReel)
        val btnAppClose = findViewById<Button>(R.id.btnAppClose)

        val subBtnPlus = findViewById<Button>(R.id.subBtnPlus)
        val subBtnClr = findViewById<Button>(R.id.subBtnClr)

        val targetMatrix = listOf(
            findViewById<LinearLayout>(R.id.layoutWizard) to "🧙‍♂️",
            findViewById<LinearLayout>(R.id.layoutCowboy) to "🤠",
            findViewById<LinearLayout>(R.id.layoutAlien) to "👽",
            findViewById<LinearLayout>(R.id.layoutShark) to "🦈",
            findViewById<LinearLayout>(R.id.layoutCastle) to "🏰",
            findViewById<LinearLayout>(R.id.layoutSpace) to "🌌",
            findViewById<LinearLayout>(R.id.layoutTime) to "⏳",
            findViewById<LinearLayout>(R.id.layoutOcean) to "🚢",
            findViewById<LinearLayout>(R.id.layoutRomance) to "❤️",
            findViewById<LinearLayout>(R.id.layoutAction) to "💥",
            findViewById<LinearLayout>(R.id.layoutMusical) to "🎸",
            findViewById<LinearLayout>(R.id.layoutRoyalty) to "👑"
        )

        targetMatrix.forEach { (layoutContainer, elementSymbol) ->
            layoutContainer.setOnClickListener {
                layoutContainer.animate()
                    .scaleX(0.92f)
                    .scaleY(0.92f)
                    .alpha(0.8f)
                    .setDuration(80)
                    .withEndAction {
                        layoutContainer.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .alpha(1.0f)
                            .duration = 80
                    }

                formulaInput = if (
                    formulaInput.isEmpty() ||
                    tvDisplay.text == getString(R.string.tap_to_begin)
                ) {
                    elementSymbol
                } else {
                    getString(R.string.formula_append_template, formulaInput, elementSymbol)
                }
                tvDisplay.text = formulaInput

                tvGenreLabel.text = getString(R.string.genre_header_prefix) + calculateCurrentGenre(formulaInput)
                tvGenreLabel.setTextColor("#A0A0A0".toColorInt())
            }
        }

        subBtnPlus.setOnClickListener {
            if (formulaInput.isNotEmpty() && tvDisplay.text != getString(R.string.tap_to_begin)) {
                formulaInput = getString(R.string.operator_append_template, formulaInput, getString(R.string.op_plus))
                tvDisplay.text = formulaInput
            }
        }

        val resetActionBlock = {
            formulaInput = ""
            tvDisplay.text = getString(R.string.tap_to_begin)
            tvDisplay.setTextColor("#00FF00".toColorInt())
            tvGenreLabel.text = getString(R.string.genre_none)
            tvGenreLabel.setTextColor("#888888".toColorInt())
        }

        subBtnClr.setOnClickListener { resetActionBlock() }
        btnClearReel.setOnClickListener { resetActionBlock() }
        btnAppClose.setOnClickListener { finishAndRemoveTask() }

        // --- INFO MODAL ---
        btnInfo.setOnClickListener {
            val rootFrameLayout = android.widget.FrameLayout(this)
            val dialogLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 40, 48, 48)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            val casualTypeface = Typeface.create("casual", Typeface.BOLD)

            val titleView = TextView(this).apply {
                text = "THE CIPHER PROTOCOL"
                textSize = 22f
                setTextColor(Color.BLACK)
                typeface = casualTypeface
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 16, 0, 12) }
            }

            val ticketDivider = TextView(this).apply {
                text = "• • • • • • • • • • • • • • • • • • • • • • •"
                textSize = 14f
                setTextColor("#888888".toColorInt())
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16) }
            }

            val messageView = TextView(this).apply {
                text = "Welcome to Cinema Cipher.\n\nCombine cinematic symbols to decode hidden film narratives.\n\nPress DECODE to reveal your generated movie."
                textSize = 15f
                setTextColor("#222222".toColorInt())
                typeface = casualTypeface
                gravity = Gravity.CENTER
                setLineSpacing(0f, 1.2f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 24) }
            }

            dialogLayout.addView(titleView)
            dialogLayout.addView(ticketDivider)
            dialogLayout.addView(messageView)
            rootFrameLayout.addView(dialogLayout)

            val customTicketBackground = GradientDrawable().apply {
                setColor("#FEFED0".toColorInt())
                setStroke(6, Color.BLACK)
                cornerRadius = 24f
            }

            val dialog = AlertDialog.Builder(this)
                .setView(rootFrameLayout)
                .setPositiveButton("[ LET'S CREATE ]") { d, _ -> d.dismiss() }
                .create()

            val closeCrossView = TextView(this).apply {
                text = "✕"
                textSize = 16f
                setTextColor(Color.BLACK)
                typeface = casualTypeface
                gravity = Gravity.CENTER
                background = GradientDrawable().apply {
                    setColor("#E0E0E0".toColorInt())
                    setStroke(3, Color.BLACK)
                    shape = GradientDrawable.OVAL
                }
                layoutParams = android.widget.FrameLayout.LayoutParams(36.toPx(), 36.toPx()).apply {
                    gravity = Gravity.TOP or Gravity.END
                    setMargins(0, 16, 16, 0)
                }
                setOnClickListener { dialog.dismiss() }
            }
            rootFrameLayout.addView(closeCrossView)

            dialog.window?.setBackgroundDrawable(customTicketBackground)
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(Color.BLACK)
                typeface = casualTypeface
                textSize = 16f
                setPadding(32, 16, 32, 16)
            }
        }

        // --- DECODE LOGIC ENGINE ---
        btnDecode.setOnClickListener {
            val input = formulaInput
            if (input.isEmpty() || tvDisplay.text == getString(R.string.tap_to_begin)) {
                tvDisplay.text = getString(R.string.awaiting_formulas)
                tvDisplay.setTextColor("#00FF00".toColorInt())
                return@setOnClickListener
            }

            tvDisplay.text = getString(R.string.processing_text)
            tvDisplay.setTextColor("#00FF00".toColorInt())
            btnDecode.isEnabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                btnDecode.isEnabled = true

                when {
                    input.contains("🧙‍♂️") && input.contains("👽") && input.contains("🦈") && input.contains("🎸") -> {
                        tvDisplay.text = getString(R.string.movie_secret_cult)
                        tvDisplay.setTextColor("#E2FF00".toColorInt())
                        tvGenreLabel.text = getString(R.string.genre_header_prefix) + getString(R.string.genre_cult)
                        tvGenreLabel.setTextColor("#E2FF00".toColorInt())
                    }

                    input.contains("🧙‍♂️") && input.contains("🏰") && input.contains("👑") -> { tvDisplay.text = getString(R.string.movie_hp); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    input.contains("👽") && input.contains("🌌") && input.contains("🤠") -> { tvDisplay.text = getString(R.string.movie_sw); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    input.contains("🚢") && input.contains("❤️") && input.contains("💥") -> { tvDisplay.text = getString(R.string.movie_titanic); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    input.contains("🦈") && input.contains("🚢") && input.contains("💥") -> { tvDisplay.text = getString(R.string.movie_jaws); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }

                    (input.contains("🧙‍♂️") && input.contains("🏰")) -> { tvDisplay.text = getString(R.string.movie_shrek); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    (input.contains("🧙‍♂️") && input.contains("⏳")) -> { tvDisplay.text = getString(R.string.movie_merlin); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    (input.contains("🧙‍♂️") && input.contains("❤️")) -> { tvDisplay.text = getString(R.string.movie_beauty); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    (input.contains("🧙‍♂️") && input.contains("🎸")) -> { tvDisplay.text = getString(R.string.movie_scott); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    (input.contains("🏰") && input.contains("👑")) -> { tvDisplay.text = getString(R.string.movie_braveheart); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    (input.contains("🏰") && input.contains("❤️")) -> { tvDisplay.text = getString(R.string.movie_cinderella); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }

                    (input.contains("🤠") && input.contains("🌌")) -> { tvDisplay.text = getString(R.string.movie_bebop); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    (input.contains("👽") && input.contains("🌌")) -> { tvDisplay.text = getString(R.string.movie_alien); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    (input.contains("👽") && input.contains("🚢")) -> { tvDisplay.text = getString(R.string.movie_rim); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    (input.contains("👽") && input.contains("❤️")) -> { tvDisplay.text = getString(R.string.movie_et); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    (input.contains("👽") && input.contains("💥")) -> { tvDisplay.text = getString(R.string.movie_ind_day); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    (input.contains("🌌") && input.contains("🎸")) -> { tvDisplay.text = getString(R.string.movie_guardians); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }

                    (input.contains("🤠") && input.contains("❤️")) -> { tvDisplay.text = getString(R.string.movie_brokeback); tvDisplay.setTextColor("#FF79C6".toColorInt()); tvGenreLabel.setTextColor("#FF79C6".toColorInt()) }
                    (input.contains("🚢") && input.contains("❤️")) -> { tvDisplay.text = getString(R.string.movie_mamma_mia); tvDisplay.setTextColor("#FF79C6".toColorInt()); tvGenreLabel.setTextColor("#FF79C6".toColorInt()) }
                    (input.contains("⏳") && input.contains("❤️")) -> { tvDisplay.text = getString(R.string.movie_time_trav); tvDisplay.setTextColor("#FF79C6".toColorInt()); tvGenreLabel.setTextColor("#FF79C6".toColorInt()) }
                    (input.contains("⏳") && input.contains("🎸")) -> { tvDisplay.text = getString(R.string.movie_la_la); tvDisplay.setTextColor("#FF79C6".toColorInt()); tvGenreLabel.setTextColor("#FF79C6".toColorInt()) }

                    (input.contains("🤠") && input.contains("⏳")) -> { tvDisplay.text = getString(R.string.movie_bttf); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    (input.contains("🦈") && input.contains("🚢")) -> { tvDisplay.text = getString(R.string.movie_meg); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    (input.contains("🦈") && input.contains("🏰")) -> { tvDisplay.text = getString(R.string.movie_aquaman); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    (input.contains("🤠") && input.contains("💥")) -> { tvDisplay.text = getString(R.string.movie_django); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    (input.contains("🦈") && input.contains("💥")) -> { tvDisplay.text = getString(R.string.movie_blue_sea); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    (input.contains("🚢") && input.contains("👑")) -> { tvDisplay.text = getString(R.string.movie_pirates); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    (input.contains("💥") && input.contains("🎸")) -> { tvDisplay.text = getString(R.string.movie_fury_road); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }

                    input.contains("🧙‍♂️") || input.contains("🏰") -> { tvDisplay.text = getString(R.string.fallback_fantasy); tvDisplay.setTextColor("#BD93F9".toColorInt()); tvGenreLabel.setTextColor("#BD93F9".toColorInt()) }
                    input.contains("👽") || input.contains("🌌") -> { tvDisplay.text = getString(R.string.fallback_scifi); tvDisplay.setTextColor("#8BE9FD".toColorInt()); tvGenreLabel.setTextColor("#8BE9FD".toColorInt()) }
                    input.contains("🤠") -> { tvDisplay.text = getString(R.string.fallback_western); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    input.contains("🚢") || input.contains("🦈") -> { tvDisplay.text = getString(R.string.fallback_nautical); tvDisplay.setTextColor("#FF5555".toColorInt()); tvGenreLabel.setTextColor("#FF5555".toColorInt()) }
                    input.contains("🎸") -> { tvDisplay.text = getString(R.string.fallback_musical); tvDisplay.setTextColor("#FF79C6".toColorInt()); tvGenreLabel.setTextColor("#FF79C6".toColorInt()) }

                    else -> { tvDisplay.text = getString(R.string.nan_fallback); tvDisplay.setTextColor("#00FF00".toColorInt()); tvGenreLabel.setTextColor("#888888".toColorInt()) }
                }

                formulaInput = ""
            }, 500)
        }
    }

    private fun calculateCurrentGenre(buffer: String): String {
        return when {
            buffer.contains("🧙‍♂️") && buffer.contains("👽") && buffer.contains("🦈") && buffer.contains("🎸") -> getString(R.string.genre_cult)
            buffer.contains("🧙‍♂️") || buffer.contains("🏰") -> getString(R.string.genre_fantasy)
            buffer.contains("👽") || buffer.contains("🌌") -> getString(R.string.genre_scifi)
            buffer.contains("🤠") -> getString(R.string.genre_western)
            buffer.contains("🚢") || buffer.contains("🦈") -> getString(R.string.genre_nautical)
            buffer.contains("🎸") -> getString(R.string.genre_musical)
            buffer.contains("❤️") -> getString(R.string.genre_romance)
            buffer.contains("💥") -> getString(R.string.genre_action)
            else -> getString(R.string.genre_none)
        }
    }

    private fun applyNeoBrutalistStyling() {
        val screenContainer = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.screenContainer)
        val btnInfo = findViewById<Button>(R.id.btnInfo)
        val btnDecode = findViewById<Button>(R.id.btnDecode)
        val btnClearReel = findViewById<Button>(R.id.btnClearReel)
        val btnAppClose = findViewById<Button>(R.id.btnAppClose)

        val subBtnPlus = findViewById<Button>(R.id.subBtnPlus)
        val subBtnClr = findViewById<Button>(R.id.subBtnClr)

        screenContainer.background = GradientDrawable().apply {
            setColor("#121A12".toColorInt())
            setStroke(5, "#00FF00".toColorInt())
            cornerRadius = 16f
        }

        btnInfo.background = GradientDrawable().apply {
            setColor("#222222".toColorInt())
            setStroke(3, Color.WHITE)
            cornerRadius = 12f
        }

        btnAppClose.background = GradientDrawable().apply {
            setColor("#222222".toColorInt())
            setStroke(3, "#FF5555".toColorInt())
            cornerRadius = 12f
        }

        btnDecode.background = GradientDrawable().apply {
            setColor("#E2FF00".toColorInt())
            setStroke(6, Color.BLACK)
            cornerRadius = 14f
        }

        btnClearReel.background = GradientDrawable().apply {
            setColor("#1A1A1A".toColorInt())
            setStroke(4, "#FF5555".toColorInt())
            cornerRadius = 12f
        }

        subBtnPlus.background = GradientDrawable().apply {
            setColor("#00FF00".toColorInt())
            setStroke(3, Color.BLACK)
            cornerRadius = 8f
        }

        subBtnClr.background = GradientDrawable().apply {
            setColor("#222222".toColorInt())
            setStroke(3, "#FF5555".toColorInt())
            cornerRadius = 8f
        }

        val rows = listOf(
            listOf(R.id.layoutWizard, R.id.layoutCowboy, R.id.layoutAlien, R.id.layoutShark) to "#FF79C6",
            listOf(R.id.layoutCastle, R.id.layoutSpace, R.id.layoutTime, R.id.layoutOcean) to "#8BE9FD",
            listOf(R.id.layoutRomance, R.id.layoutAction, R.id.layoutMusical, R.id.layoutRoyalty) to "#FF5555"
        )

        rows.forEach { (idList, colorHex) ->
            idList.forEach { layoutId ->
                val cardLayout = findViewById<LinearLayout>(layoutId)
                cardLayout.background = GradientDrawable().apply {
                    setColor(colorHex.toColorInt())
                    setStroke(5, Color.BLACK)
                    cornerRadius = 16f
                }
                cardLayout.elevation = 12f
            }
        }
    }

    private fun Int.toPx(): Int = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
}