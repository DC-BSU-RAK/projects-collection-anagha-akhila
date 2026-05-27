package com.anagha.midnight_fm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anagha.midnight_fm.databinding.ActivityMidnightRadioBinding

data class RetroTrack(val artist: String, val title: String, val album: String)

class MidnightRadioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMidnightRadioBinding
    private var selectedGenre: String = ""
    private val mainHandler = Handler(Looper.getMainLooper())

    private var streamingSecondsCount = 0
    private var currentTrackIndex = 0

    private val synthwavePlaylist = listOf(
        RetroTrack("Kavinsky", "Nightcall", "Outrun (1986)"),
        RetroTrack("The Midnight", "Sunset", "Endless Summer (1984)"),
        RetroTrack("College & Electric Youth", "A Real Hero", "Pacific Coast (1985)")
    )

    private val darksynthPlaylist = listOf(
        RetroTrack("Carpenter Brut", "Turbo Killer", "Trilogy (1987)"),
        RetroTrack("Perturbator", "Future Club", "Dangerous Days (1984)")
    )

    private val futureFunkPlaylist = listOf(
        RetroTrack("Macross 82-99", "Fun Tonight", "SAILORWAVE (1988)"),
        RetroTrack("Yung Bae", "Bae City Rollaz", "Bae 2 (1985)")
    )

    private val popRockPlaylist = listOf(
        RetroTrack("Michael Jackson", "Billie Jean", "Thriller (1982)"),
        RetroTrack("Michael Jackson", "Beat It", "Thriller (1982)"),
        RetroTrack("Prince", "Purple Rain", "Purple Rain (1984)")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMidnightRadioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences("midnight_fm_console", Context.MODE_PRIVATE)

        startVinylRotation()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.layoutSplashView.visibility = View.GONE
            switchWorkspaceView(binding.layoutDashboardView)
        }, 3000)

        binding.cardGenreSynthwave.setOnClickListener { selectGenreRow("Synthwave", binding.cardGenreSynthwave) }
        binding.cardGenreDarksynth.setOnClickListener { selectGenreRow("Darksynth", binding.cardGenreDarksynth) }
        binding.cardGenreFutureFunk.setOnClickListener { selectGenreRow("Future Funk", binding.cardGenreFutureFunk) }
        binding.cardGenrePopRock.setOnClickListener { selectGenreRow("80s Pop Rock", binding.cardGenrePopRock) }

        binding.btnGenerateFrequency.setOnClickListener {
            if (selectedGenre.isEmpty()) {
                Toast.makeText(this, "Please select a genre matrix line first!", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvPlayerActiveMood.text = "GENRE: ${selectedGenre.uppercase()}"
                binding.tvPlayerWaveIcon.text = when(selectedGenre) {
                    "Synthwave" -> "🔮"
                    "Darksynth" -> "🏰"
                    "Future Funk" -> "⚡"
                    else -> "🎸"
                }

                currentTrackIndex = 0
                switchWorkspaceView(binding.layoutPlayerView)
                updateMetadataDisplay()
                startLivePlaybackTimer()
            }
        }

        binding.btnNextTrack.setOnClickListener {
            val targetPlaylist = getCurrentPlaylist()
            currentTrackIndex = (currentTrackIndex + 1) % targetPlaylist.size
            updateMetadataDisplay()
            Toast.makeText(this, "🔄 Re-indexing satellite frequencies...", Toast.LENGTH_SHORT).show()
        }

        binding.btnStreamSpotify.setOnClickListener {
            val track = getCurrentPlaylist()[currentTrackIndex]
            val searchQuery = "${track.artist} ${track.title} 1980s"

            if (preferences.getBoolean("spotify_alerts", true)) {
                Toast.makeText(this, "🚀 Broadcasting search query to YouTube...", Toast.LENGTH_SHORT).show()
            }

            val youtubeIntent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", searchQuery)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (youtubeIntent.resolveActivity(packageManager) != null) {
                startActivity(youtubeIntent)
            } else {
                val webFallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery)))
                startActivity(webFallback)
            }
        }

        binding.btnExitApp.setOnClickListener {
            mainHandler.removeCallbacksAndMessages(null)
            finishAffinity()
        }

        binding.btnDisconnectStation.setOnClickListener {
            streamingSecondsCount = 0
            switchWorkspaceView(binding.layoutDashboardView)
            updateBottomNavHighlight(binding.btnNavTune)
        }

        binding.btnNavTune.setOnClickListener {
            updateBottomNavHighlight(binding.btnNavTune)
            if (streamingSecondsCount > 0) switchWorkspaceView(binding.layoutPlayerView) else switchWorkspaceView(binding.layoutDashboardView)
        }
        binding.btnNavArchive.setOnClickListener {
            updateBottomNavHighlight(binding.btnNavArchive)
            switchWorkspaceView(binding.layoutArchiveView)
        }
        binding.btnNavConsole.setOnClickListener {
            updateBottomNavHighlight(binding.btnNavConsole)
            switchWorkspaceView(binding.layoutConsoleSettingsView)
        }

        binding.btnInfoModalTrigger.setOnClickListener { binding.layoutCustomInfoModal.visibility = View.VISIBLE }
        binding.btnDismissModal.setOnClickListener { binding.layoutCustomInfoModal.visibility = View.GONE }

        binding.switchTrackAutoplay.isChecked = preferences.getBoolean("track_autoplay", false)
        binding.switchSpotifyAlerts.isChecked = preferences.getBoolean("spotify_alerts", true)

        binding.switchTrackAutoplay.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("track_autoplay", isChecked).apply()
        }
        binding.switchSpotifyAlerts.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("spotify_alerts", isChecked).apply()
        }
    }

    private fun getCurrentPlaylist(): List<RetroTrack> {
        return when(selectedGenre) {
            "Synthwave" -> synthwavePlaylist
            "Darksynth" -> darksynthPlaylist
            "Future Funk" -> futureFunkPlaylist
            else -> popRockPlaylist
        }
    }

    private fun updateMetadataDisplay() {
        val currentTrack = getCurrentPlaylist()[currentTrackIndex]
        binding.tvPlayerArtist.text = "ARTIST: ${currentTrack.artist.uppercase()}"
        binding.tvPlayerSongTitle.text = "TRACK: ${currentTrack.title.uppercase()}"
        binding.tvPlayerAlbum.text = "ALBUM: ${currentTrack.album.uppercase()}"
    }

    private fun selectGenreRow(genreName: String, targetCard: androidx.cardview.widget.CardView) {
        selectedGenre = genreName
        binding.cardGenreSynthwave.setCardBackgroundColor(Color.parseColor("#120E24"))
        binding.cardGenreDarksynth.setCardBackgroundColor(Color.parseColor("#120E24"))
        binding.cardGenreFutureFunk.setCardBackgroundColor(Color.parseColor("#120E24"))
        binding.cardGenrePopRock.setCardBackgroundColor(Color.parseColor("#120E24"))
        targetCard.setCardBackgroundColor(Color.parseColor("#3D1B40"))
    }

    private fun switchWorkspaceView(targetView: View) {
        binding.layoutDashboardView.visibility = View.GONE
        binding.layoutPlayerView.visibility = View.GONE
        binding.layoutArchiveView.visibility = View.GONE
        binding.layoutConsoleSettingsView.visibility = View.GONE
        targetView.visibility = View.VISIBLE
    }

    private fun updateBottomNavHighlight(selectedButton: View) {
        binding.btnNavTune.setBackgroundColor(Color.parseColor("#120E24"))
        binding.btnNavTune.setTextColor(Color.parseColor("#8A84A3"))
        binding.btnNavArchive.setBackgroundColor(Color.parseColor("#120E24"))
        binding.btnNavArchive.setTextColor(Color.parseColor("#8A84A3"))
        binding.btnNavConsole.setBackgroundColor(Color.parseColor("#120E24"))
        binding.btnNavConsole.setTextColor(Color.parseColor("#8A84A3"))

        if (selectedButton is android.widget.Button) {
            selectedButton.setBackgroundColor(Color.parseColor("#FF1A75"))
            selectedButton.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun startLivePlaybackTimer() {
        streamingSecondsCount = 0
        binding.tvPlayerTimer.text = "00:00"

        mainHandler.post(object : Runnable {
            override fun run() {
                if (binding.layoutPlayerView.visibility == View.VISIBLE) {
                    streamingSecondsCount++
                    val minutes = streamingSecondsCount / 60
                    val seconds = streamingSecondsCount % 60
                    binding.tvPlayerTimer.text = String.format("%02d:%02d", minutes, seconds)

                    val preferences = getSharedPreferences("midnight_fm_console", Context.MODE_PRIVATE)
                    val playlist = getCurrentPlaylist()

                    if (preferences.getBoolean("track_autoplay", false) && streamingSecondsCount % 10 == 0) {
                        currentTrackIndex = (currentTrackIndex + 1) % playlist.size
                        updateMetadataDisplay()
                    }

                    binding.eqBar1.layoutParams.height = (20..75).random()
                    binding.eqBar2.layoutParams.height = (40..100).random()
                    binding.eqBar3.layoutParams.height = (15..55).random()
                    binding.eqBar4.layoutParams.height = (30..90).random()
                    binding.eqBar5.layoutParams.height = (20..80).random()
                    binding.layoutEqualizerBars.requestLayout()

                    mainHandler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun startVinylRotation() {
        val rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2500
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        binding.ivVinylGraphicDisk.startAnimation(rotate)
    }
}