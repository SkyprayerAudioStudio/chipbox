package net.sigmabeta.chipbox.backend

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.session.PlaybackState
import net.sigmabeta.chipbox.model.objects.AudioConfig
import net.sigmabeta.chipbox.model.objects.Track
import net.sigmabeta.chipbox.util.external.*
import net.sigmabeta.chipbox.util.logDebug
import net.sigmabeta.chipbox.util.logError
import net.sigmabeta.chipbox.util.logInfo
import net.sigmabeta.chipbox.util.logVerbose
import net.sigmabeta.chipbox.view.interfaces.BackendView
import java.util.*
import javax.inject.Inject

class Player @Inject constructor(val audioConfig: AudioConfig,
                                 val audioManager: AudioManager,
                                 val context: Context): AudioManager.OnAudioFocusChangeListener {
    var backendView: BackendView? = null

    var state = PlaybackState.STATE_STOPPED

    var position = 0L

    var playbackQueue: ArrayList<Track>? = null
    var playbackQueuePosition: Int? = null

    var playingTrack: Track? = null
        set (value) {
            teardown()

            if (value != null) {
                loadTrackNative(value,
                        audioConfig.sampleRate,
                        audioConfig.minBufferSize.toLong())
            }

            field = value
        }

    var nativeBuffer = ShortArray(audioConfig.minBufferSize)

    var audioTrack: AudioTrack? = null

    var ducking = false
    var focusLossPaused = false

    fun playbackLoop() {
        logDebug("[Player] Starting playback loop.")

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)

        // If audioTrack is not null, we're likely resuming a paused track.
        if (audioTrack == null) {
            if (!initializeAudioTrack())
                return
        } else {
            logVerbose("[Player] AudioTrack already setup; resuming playback.")
        }

        var duckVolume = 1.0f

        // Begin playback loop
        audioTrack?.play()

        while (state == PlaybackState.STATE_PLAYING) {
            if (isTrackOver()) {
                logVerbose("[Player] Track has ended.")

                // TODO Calling this here prevents the original loop from ending, and starts a new one, which breaks everything
                if (!skipToNext()) {
                    logInfo("[Player] No more tracks to play.")
                    stop()
                    break
                }
            }

            // Get the next samples from the native player.
            readNextSamples(nativeBuffer)
            val error = getLastError()

            if (error == null) {
                // Check if necessary to make volume adjustments
                if (ducking) {
                    logDebug("[Player] Ducking behind other app...")

                    if (duckVolume > 0.3f) {
                        duckVolume -= 0.2f
                        logVerbose("[Player] Lowering volume to $duckVolume...")
                    }

                    audioTrack?.setVolume(duckVolume)
                } else {
                    if (duckVolume < 1.0f) {
                        duckVolume += 0.1f
                        logVerbose("[Player] Raising volume to $duckVolume...")
                    }

                    audioTrack?.setVolume(duckVolume)
                }

                audioTrack?.write(nativeBuffer, 0, audioConfig.minBufferSize)
            } else {
                logError("[Player] GME Error: ${error}")
                stop()
                break
            }
        }

        logVerbose("[Player] Playback loop has ended..")
    }

    fun play() {
        if (state == PlaybackState.STATE_PLAYING) {
            logError("[Player] Received play command, but already PLAYING a track: ${playingTrack?.path}")
            return
        }

        val focusResult = requestAudioFocus()
        if (focusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            val localTrack = playingTrack

            if (localTrack != null) {
                logVerbose("[Player] Playing track: ${localTrack.title}")
                state = PlaybackState.STATE_PLAYING

                if (backendView == null) {
                    PlayerService.start(context)
                } else {
                    backendView?.play(localTrack)
                }

                // Start a thread for the playback loop.
                Thread { playbackLoop() }.start()
            } else {
                logError("[Player] Received play command, but no Track selected.")
            }
        } else {
            logError("[Player] Unable to gain audio focus.")
        }
    }

    fun play(track: Track) {
        if (state == PlaybackState.STATE_PLAYING) {
            pause()
        }

        logVerbose("[Player] Playing new track: ${track.title}")

        playingTrack = track

        playbackQueue = null
        playbackQueuePosition = null

        play()
    }

    fun play(playbackQueue: ArrayList<Track>, position: Int) {
        if (position < playbackQueue.size) {
            if (state == PlaybackState.STATE_PLAYING) {
                pause()
            }

            logVerbose("[Player] Playing new playlist, starting from track ${position} of ${playbackQueue.size}.")

            this.playbackQueue = playbackQueue
            playbackQueuePosition = position

            playingTrack = playbackQueue.get(position)

            play()
        } else {
            logError("[Player] Tried to play new playlist, but invalid track number: ${position} of ${playbackQueue.size}")
        }
    }

    fun play(position: Int) {
        val queue = playbackQueue
        if (queue != null) {
            if (position < queue.size) {
                playbackQueuePosition = position
                playingTrack = queue.get(position)

                play()
            } else {
                logError("[Player] Cannot play track #${position} of ${queue.size}.")
            }
        } else {
            logError("[Player] Cannot play track #${position}: no playback queue exists.")
        }
    }

    fun skipToNext(): Boolean {
        val queue = playbackQueue
        var position = playbackQueuePosition

        if (queue != null && position != null && position < queue.size - 1) {
            if (state == PlaybackState.STATE_PLAYING) {
                pause()
            }

            position += 1

            playingTrack = queue.get(position)
            playbackQueuePosition = position

            play()
            logInfo("[Player] Loading track ${position} of ${queue.size}.")
            return true
        }

        return false
    }

    fun skipToPrev() {
        val queue = playbackQueue
        var position = playbackQueuePosition

        if (queue != null && position != null && position >= 0) {
            if (state == PlaybackState.STATE_PLAYING) {
                pause()
            }

            position -= 1

            playingTrack = queue.get(position)
            playbackQueuePosition = position

            logInfo("[Player] Loading track ${position} of ${queue.size}.")
        }
    }

    fun pause() {
        if (state != PlaybackState.STATE_PLAYING) {
            logError("[Player] Received pause command, but not currently PLAYING.")
            return
        }

        logVerbose("[Player] Pausing track: ${playingTrack?.title}")

        state = PlaybackState.STATE_PAUSED

        audioTrack?.pause()

        backendView?.pause()
    }

    fun stop() {
        if (state == PlaybackState.STATE_STOPPED) {
            logError("[Player] Received stop command, but already STOPPED.")
            return
        }

        logVerbose("[Player] Stopping track: ${playingTrack?.title}")

        state = PlaybackState.STATE_STOPPED

        audioTrack?.pause()
        audioTrack?.flush()
        audioTrack?.release()
        audioTrack = null

        playingTrack = null

        teardown()

        audioManager.abandonAudioFocus(this)

        backendView?.stop()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                logVerbose("[Player] Focus lost. Stopping...")
                stop()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                logVerbose("[Player] Focus lost temporarily. Pausing...")
                pause()

                focusLossPaused = true
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                logVerbose("[Player] Focus lost temporarily, but can duck. Lowering volume...")
                ducking = true
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                logVerbose("[Player] Focus gained. Resuming...")

                ducking = false

                if (focusLossPaused) {
                    play()
                    focusLossPaused = false
                }
            }
        }
    }

    private fun requestAudioFocus(): Int {
        return audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
    }

    private fun initializeAudioTrack(): Boolean {
        logVerbose("[Player] Initializing audio track.\n" +
                "[Player] Sample Rate: ${audioConfig.sampleRate}\n" +
                "[Player] Buffer size: ${audioConfig.minBufferSize}")

        audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                audioConfig.sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                audioConfig.minBufferSize,
                AudioTrack.MODE_STREAM)

        if (audioTrack == null) {
            logError("[Player] Failed to initialize AudioTrack.")
            return false
        }

        // Get updates on playback position every second (one frame is equal to one sample).
        audioTrack?.setPositionNotificationPeriod(audioConfig.sampleRate)

        // Set a listener to update the UI's playback position.
        audioTrack?.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack) {
                // Do nothing
            }

            override fun onPeriodicNotification(track: AudioTrack) {
                // TODO Update the UI.
                // logVerbose("[Player] Has played ${getMillisPlayed() / 1000} seconds.")
            }
        })

        return true
    }
}