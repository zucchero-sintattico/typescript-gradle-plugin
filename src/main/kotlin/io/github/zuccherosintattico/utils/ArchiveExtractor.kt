package io.github.zuccherosintattico.utils

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

internal object ArchiveExtractor {

    /**
     * Extracts a tar.gz archive to a destination directory.
     *
     * @param archiveFile the tar.gz archive file
     * @param destinationDir the destination directory
     * @return the path to the extracted directory
     */
    @Suppress("NestedBlockDepth")
    fun extractTarGz(archiveFile: File, destinationDir: File): Path {
        FileInputStream(archiveFile).use { fileIn ->
            BufferedInputStream(fileIn).use { bufferedIn ->
                GZIPInputStream(bufferedIn).use { gzipIn ->
                    TarArchiveInputStream(gzipIn).use { tarIn ->
                        var entry: TarArchiveEntry? = tarIn.nextEntry
                        while (entry != null) {
                            if (entry.isDirectory) {
                                Files.createDirectories(destinationDir.toPath().resolve(entry.name))
                            } else if (entry.isSymbolicLink) {
                                val linkTarget = Paths.get(entry.linkName)
                                Files.createSymbolicLink(destinationDir.resolve(entry.name).toPath(), linkTarget)
                            } else {
                                val entryFile = destinationDir.resolve(entry.name)
                                Files.createDirectories(entryFile.parentFile.toPath())
                                FileOutputStream(entryFile).use { fileOut ->
                                    tarIn.copyTo(fileOut)
                                }
                            }
                            entry = tarIn.nextEntry
                        }
                    }
                }
            }
        }
        val extractedDir = archiveFile.nameWithoutExtension.replace(".tar", "")
        return destinationDir.resolve(extractedDir).toPath()
    }

    /**
     * Extracts a zip archive to a destination directory.
     *
     * @param zipFile the zip archive file
     * @param destinationDir the destination directory
     * @return the path to the extracted directory
     */
    @Suppress("NestedBlockDepth", "MagicNumber")
    fun extractZip(zipFile: File, destinationDir: File): Path {
        FileInputStream(zipFile).use { fileIn ->
            BufferedInputStream(fileIn).use { bufferedIn ->
                ZipArchiveInputStream(bufferedIn).use { zipIn ->
                    var entry: ZipArchiveEntry? = zipIn.nextEntry
                    while (entry != null) {
                        if (entry.isDirectory) {
                            File(destinationDir, entry.name).mkdirs()
                        } else {
                            val entryFile = File(destinationDir, entry.name)
                            entryFile.parentFile?.mkdirs()
                            FileOutputStream(entryFile).use { fileOut ->
                                zipIn.copyTo(fileOut)
                            }
                        }
                        entry = zipIn.nextEntry
                    }
                }
            }
        }
        return destinationDir.resolve(zipFile.nameWithoutExtension).toPath()
    }
}
