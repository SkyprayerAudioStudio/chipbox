package net.sigmabeta.chipbox.model.database

import android.content.Context
import com.raizlabs.android.dbflow.sql.language.SQLite
import net.sigmabeta.chipbox.model.domain.Artist
import net.sigmabeta.chipbox.model.domain.Game
import net.sigmabeta.chipbox.model.domain.Track
import net.sigmabeta.chipbox.model.domain.Track_Table
import net.sigmabeta.chipbox.model.events.FileScanEvent
import net.sigmabeta.chipbox.model.file.Folder
import net.sigmabeta.chipbox.util.*
import org.apache.commons.io.FileUtils
import rx.Observable
import rx.Subscriber
import java.io.File
import java.util.*

val TRACK_LENGTH_DEFAULT = 150000L

class SongDatabaseHelper(val context: Context) {
    fun getSongList(): Observable<List<Track>> {
        return Observable.create(
                {
                    logInfo("[SongDatabaseHelper] Reading song list...")

                    val tracks = SQLite.select().from(Track::class.java)
                            .where()
                            .orderBy(Track_Table.title, true)
                            .queryList()

                    logVerbose("[SongDatabaseHelper] Found ${tracks.size} tracks.")

                    it.onNext(tracks)
                    it.onCompleted()
                }
        )
    }

    fun getSongListForArtist(artistId: Long): Observable<List<Track>> {
        return Observable.create(
                {
                    logInfo("[SongDatabaseHelper] Reading song list for artist #$artistId...")

                    val tracks = SQLite.select().from(Track::class.java)
                            .where(Track_Table.artistId.eq(artistId))
                            .orderBy(Track_Table.gameId, true)
                            .queryList()

                    logVerbose("[SongDatabaseHelper] Found ${tracks.size} tracks.")

                    it.onNext(tracks)
                    it.onCompleted()
                }
        )
    }

    fun getSongListForGame(gameId: Long): Observable<List<Track>> {
        return Observable.create(
                {
                    logInfo("[SongDatabaseHelper] Reading song list for game #$gameId...")

                    val tracks = SQLite.select().from(Track::class.java)
                            .where(Track_Table.gameId.eq(gameId))
                            .orderBy(Track_Table.trackNumber, true)
                            .queryList()

                    logVerbose("[SongDatabaseHelper] Found ${tracks.size} tracks.")

                    it.onNext(tracks)
                    it.onCompleted()
                }
        )
    }

    fun scanLibrary(): Observable<FileScanEvent> {
        return Observable.create(
                { sub ->
                    // OnSubscribe.call. it: String
                    clearTables()

                    logInfo("[SongDatabaseHelper] Scanning library...")

                    val startTime = System.currentTimeMillis()

                    val folders = Folder.getAll()

                    val gameMap = HashMap<Long, Game>()
                    val artistMap = HashMap<Long, Artist>()

                    folders.forEach { folder ->
                        folder.path?.let {
                            scanFolder(File(it), gameMap, artistMap, sub as Subscriber<FileScanEvent>)
                        }
                    }

                    val endTime = System.currentTimeMillis()
                    val scanDuration = (endTime - startTime) / 1000.0f

                    logInfo("[SongDatabaseHelper] Scanned library in ${scanDuration} seconds.")

                    sub.onCompleted()
                }
        )
    }

    private fun clearTables() {
        logInfo("[SongDatabaseHelper] Clearing library...")

        SQLite.delete(Artist::class.java).query()
        SQLite.delete(Game::class.java).query()
        SQLite.delete(Track::class.java).query()
    }

    private fun scanFolder(folder: File, gameMap: HashMap<Long, Game>, artistMap: HashMap<Long, Artist>, sub: Subscriber<FileScanEvent>) {
        val folderPath = folder.absolutePath
        logInfo("[SongDatabaseHelper] Reading files from library folder: ${folderPath}")

        sub.onNext(FileScanEvent(FileScanEvent.TYPE_FOLDER, folderPath))

        var folderGameId: Long? = null

        // Iterate through every file in the folder.
        val children = folder.listFiles()

        if (children != null) {
            Arrays.sort(children)

            var trackCount = 1

            for (file in children) {
                if (!file.isHidden) {
                    if (file.isDirectory) {
                        scanFolder(file, gameMap, artistMap, sub)
                    } else {
                        val filePath = file.absolutePath
                        val fileExtension = getFileExtension(filePath)

                        if (fileExtension != null) {
                            // Check that the file has an extension we care about before trying to read out of it.
                            if (EXTENSIONS_MUSIC.contains(fileExtension)) {
                                if (EXTENSIONS_MULTI_TRACK.contains(fileExtension)) {
                                    folderGameId = readMultipleTracks(artistMap, file, filePath, gameMap, sub)
                                    if (folderGameId <= 0) {
                                        sub.onNext(FileScanEvent(FileScanEvent.TYPE_BAD_TRACK, file.name))
                                    }
                                } else {
                                    folderGameId = readSingleTrack(artistMap, file, filePath, gameMap, sub, trackCount)

                                    if (folderGameId > 0) {
                                        trackCount += 1
                                    }
                                }
                            } else if (EXTENSIONS_IMAGES.contains(fileExtension)) {
                                if (folderGameId != null) {
                                    copyImageToInternal(folderGameId, file)
                                } else {
                                    logError("[SongDatabaseHelper] Found image, but game ID unknown: ${filePath}")
                                }
                            }
                        }
                    }
                }
            }

        } else if (!folder.exists()) {
            logError("[SongDatabaseHelper] Folder no longer exists: ${folderPath}")
        } else {
            logError("[SongDatabaseHelper] Folder contains no tracks:  ${folderPath}")
        }
    }

    private fun readSingleTrack(artistMap: HashMap<Long, Artist>, file: File, filePath: String, gameMap: HashMap<Long, Game>, sub: Subscriber<FileScanEvent>, trackNumber: Int): Long {
        val track = readSingleTrackFile(filePath, trackNumber)

        if (track != null) {
            var folderGameId = addTrackToDb(artistMap, gameMap, track)

            sub.onNext(FileScanEvent(FileScanEvent.TYPE_TRACK, file.name))
            return folderGameId
        } else {
            logError("[SongDatabaseHelper] Couldn't read track at ${filePath}")
            sub.onNext(FileScanEvent(FileScanEvent.TYPE_BAD_TRACK, file.name))

            return -1
        }
    }

    private fun readMultipleTracks(artistMap: HashMap<Long, Artist>, file: File, filePath: String, gameMap: HashMap<Long, Game>, sub: Subscriber<FileScanEvent>): Long {
        val tracks = readMultipleTrackFile(filePath) ?: return -1

        var folderGameId = -1L
        tracks.forEach { track ->
            folderGameId = addTrackToDb(artistMap, gameMap, track)
            sub.onNext(FileScanEvent(FileScanEvent.TYPE_TRACK, file.name))
        }

        return folderGameId
    }

    private fun addTrackToDb(artistMap: HashMap<Long, Artist>, gameMap: HashMap<Long, Game>, track: Track): Long {
        var folderGameId = Game.getId(track.gameTitle, track.platform, gameMap)
        val artistId = Artist.getId(track.artist, artistMap)

        track.gameId = folderGameId
        track.artistId = artistId

        track.insert()
        return folderGameId
    }

    private fun copyImageToInternal(gameId: Long, sourceFile: File) {
        val storageDir = context.getExternalFilesDir(null)

        val targetDirPath = storageDir.absolutePath + "/images/" + gameId.toString()
        val targetDir = File(targetDirPath)

        targetDir.mkdirs()

        val sourcePath = sourceFile.path
        val extensionStart = sourcePath.lastIndexOf('.')
        val fileExtension = sourcePath.substring(extensionStart)

        val targetFilePath = targetDirPath + "/local" + fileExtension
        val targetFile = File(targetFilePath)

        FileUtils.copyFile(sourceFile, targetFile)

        logInfo("[SongDatabaseHelper] Copied image: ${sourcePath} to ${targetFilePath}")

        Game.addLocalImage(gameId, "file://" + targetFilePath)
    }

    companion object {
        val ADD_STATUS_GOOD = 0
        val ADD_STATUS_EXISTS = 1
        val ADD_STATUS_DB_ERROR = 2

        private fun getTrackFromDb(id: Long): Track {
            return SQLite.select()
                    .from(Track::class.java)
                    .where(Track_Table.id.eq(id))
                    .querySingle()
        }
    }
}
