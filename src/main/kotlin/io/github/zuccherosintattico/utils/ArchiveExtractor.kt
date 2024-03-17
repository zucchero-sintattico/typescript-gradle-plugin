package io.github.zuccherosintattico.utils

import org.gradle.internal.impldep.org.apache.tools.tar.TarEntry
import org.gradle.internal.impldep.org.apache.tools.tar.TarInputStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
            GZIPInputStream(BufferedInputStream(fileIn)).use { gzipIn ->
                TarInputStream(BufferedInputStream(gzipIn)).use { tarIn ->
                    var entry: TarEntry? = tarIn.nextEntry
                    while (entry != null) {
                        val entryFile = File(destinationDir, entry.name)
                        if (entry.isDirectory) {
                            entryFile.mkdirs()
                        } else if (entry.isSymbolicLink) {
                            val linkTarget = Paths.get(entry.linkName)
                            Files.createSymbolicLink(entryFile.toPath(), linkTarget)
                        } else {
                            entryFile.parentFile?.mkdirs()
                            BufferedOutputStream(FileOutputStream(entryFile)).use { out ->
                                tarIn.copyTo(out)
                            }
                        }
                        entry = tarIn.nextEntry
                    }
                }
            }
        }
        val filename = archiveFile.name.replace(NodeDistribution.SupportedExtensions.TAR_GZ, "")
        return destinationDir.resolve(filename).toPath()
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
        val buffer = ByteArray(1024)
        try {
            ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val entryFile = File(destinationDir, entry.name)
                    if (entry.isDirectory) {
                        entryFile.mkdirs()
                    } else {
                        entryFile.parentFile?.mkdirs()
                        BufferedOutputStream(FileOutputStream(entryFile)).use { fos ->
                            var len: Int
                            while (zis.read(buffer).also { len = it } > 0) {
                                fos.write(buffer, 0, len)
                            }
                        }
                    }
                    entry = zis.nextEntry
                }
            }
        } catch (e: IOException) {
            throw IllegalStateException("Error while extracting zip file: ${e.message}", e)
        }
        return destinationDir.resolve(zipFile.nameWithoutExtension).toPath()
    }
}
