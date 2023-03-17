package pro.themed.manager

interface Downloader {
    fun downloadFile(url: String): Long
}