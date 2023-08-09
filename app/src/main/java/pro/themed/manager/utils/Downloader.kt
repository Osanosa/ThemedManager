package pro.themed.manager.utils

interface Downloader {
    fun downloadFile(url: String): Long
}