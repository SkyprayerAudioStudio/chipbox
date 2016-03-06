package net.sigmabeta.chipbox.view.interfaces

interface ScanView {
    fun onScanFailed()

    fun onScanComplete()

    fun showCurrentFolder(name: String)

    fun showLastFile(name: String)

    fun updateFilesAdded(filesAdded: Int)

    fun updateBadFiles(badFiles: Int)
}