package com.achievement.app.domain.util

/**
 * PSN 图片 CDN(image.api.playstation.com)会按内容协商返回 AVIF,而低版本 Android
 * (API < 31)无法解码 AVIF,导致缩略图/头像空白。给 playstation 的 .png 链接追加
 * ?fm=png 可强制 CDN 返回 jpeg(全平台可解码)。非该域名的链接原样返回。
 *
 * sized() 再追加 ?w=<像素宽> 让 CDN 直接返回按显示尺寸缩放后的图片,
 * 实测游戏封面原图 ~865KB,加 w=276 后 ~15KB,是列表图片加载慢的主因。
 * 奖杯图标在另一域名 psnobj.prod.dl.playstation.net 上,它同样支持 ?w=
 * (实测 488KB → 57KB),但不认 ?fm=,所以只加宽度参数。
 */
object PsnImageUrl {
    private const val CDN_HOST = "image.api.playstation.com"
    private const val TROPHY_HOST = "psnobj.prod.dl.playstation.net"

    fun forceRaster(url: String?): String? {
        if (url == null) return null
        val path = url.substringBefore('?')
        return if (
            url.contains(CDN_HOST) &&
            path.endsWith(".png", ignoreCase = true) &&
            !hasParam(url, "fm")
        ) "$url?fm=png" else url
    }

    private fun hasParam(url: String, key: String): Boolean {
        return url.substringAfter('?', "")
            .split('&')
            .any { it.substringBefore('=').equals(key, ignoreCase = true) }
    }

    /** 生成按指定宽度缩放的可解码图片 URL,非 PSN CDN 的链接原样返回。 */
    fun sized(url: String?, widthPx: Int): String? {
        val raster = forceRaster(url) ?: return null
        val scalable = raster.contains(CDN_HOST) || raster.contains(TROPHY_HOST)
        if (!scalable || hasParam(raster, "w")) return raster
        val sep = if (raster.contains('?')) '&' else '?'
        return "$raster${sep}w=$widthPx"
    }
}
