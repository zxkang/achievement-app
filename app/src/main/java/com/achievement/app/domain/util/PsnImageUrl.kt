package com.achievement.app.domain.util

/**
 * PSN 图片 CDN(image.api.playstation.com)会按内容协商返回 AVIF,而低版本 Android
 * (API < 31)无法解码 AVIF,导致缩略图/头像空白。给 playstation 的 .png 链接追加
 * ?fm=png 可强制 CDN 返回 jpeg(全平台可解码)。非该域名的链接原样返回。
 */
object PsnImageUrl {
    fun forceRaster(url: String?): String? {
        if (url == null) return null
        return if (
            url.contains("image.api.playstation.com") &&
            url.endsWith(".png") &&
            !url.contains("fm=")
        ) "$url?fm=png" else url
    }
}
