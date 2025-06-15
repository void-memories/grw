package dev.namn.cli.managers

import com.github.ajalt.clikt.core.CliktError
import dev.namn.cli.utils.abort
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object AndroidManifestManager {
    private val manifestFile: File by lazy {
        File(System.getProperty("user.dir")).walkTopDown().find { it.name == "AndroidManifest.xml" }
            ?: abort("AndroidManifest.xml not found")
    }

    private val document by lazy {
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(manifestFile)
            .apply { documentElement.normalize() }
    }

    fun findLauncherActivity(packageName: String): String {
        val activities = document.getElementsByTagName("activity")
        for (i in 0 until activities.length) {
            val elem = activities.item(i) as Element
            if (elem.getElementsByTagName("intent-filter").let { filters ->
                    (0 until filters.length).any { j ->
                        val f = filters.item(j) as Element
                        f.getElementsByTagName("action")
                            .findAttr("android:name", "android.intent.action.MAIN") &&
                                f.getElementsByTagName("category")
                                    .findAttr("android:name", "android.intent.category.LAUNCHER")
                    }
                }) {
                return elem.getAttribute("android:name").let { name ->
                    if (name.startsWith(".")) packageName + name else name
                }
            }
        }
        abort("Launcher activity not found")
    }

    private fun NodeList.findAttr(attr: String, value: String) =
        (0 until length).any { (item(it) as Element).getAttribute(attr) == value }
}
