package jp.s64.android.example.videoplaying

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import jp.s64.android.example.videoplaying.databinding.VideoTagActivityBinding

abstract class AbsVideoTagActivity : AppCompatActivity() {

    companion object {

        private val BODY: String = """
            <!DOCTYPE html>
            <html>
                <head></head>
                <body>
                    <video
                        style="display: block; object-fit: fill;"
                        width="100%"
                        autoplay
                        muted
                        controls>
                        <source
                            src="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                            type="video/mp4"/>
                    </video>
                </body>
            </html>
        """.trimIndent()

        private val DEFAULT_SCRIPT: String = """
            window.myLeakFunction = (function() {
                const hoge = new Date()
                while (true) {
                    console.log('Hello!')
                    if ((new Date()) - hoge > 100) {
                        break
                    }
                }
                console.log('Schedule next...')
                setTimeout(window.myLeakFunction, 200)
            })
            window.myLeakFunction()
        """.trimIndent()

    }

    private lateinit var binding: VideoTagActivityBinding

    abstract val webViewLayerType: Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VideoTagActivityBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.layerType.text = "$webViewLayerType"
        binding.activityHwaFlag.text = "${isHwaActivityFlagEnabled()}"

        binding.scriptEditor.setText(DEFAULT_SCRIPT)
        binding.executeJs.setOnClickListener {
            getWebViews().forEach {
                it.evaluateJavascript(binding.scriptEditor.text.toString(), null)
            }
        }

        binding.invisibleProgress.setOnClickListener {
            getProgressBars().forEach {
                it.visibility = View.INVISIBLE
            }
        }

        binding.goneProgress.setOnClickListener {
            getProgressBars().forEach {
                it.visibility = View.GONE
            }
        }

        binding.visibleProgress.setOnClickListener {
            getProgressBars().forEach {
                it.visibility = View.VISIBLE
            }
        }

        binding.addWebView.setOnClickListener {
            addWebView()
        }
        binding.addWebView.performClick() // init
    }

    override fun onDestroy() {
        super.onDestroy()
        getWebViews().forEach {
            it.destroy()
        }
    }

    private fun isHwaActivityFlagEnabled(): Boolean {
        return (packageManager.getActivityInfo(componentName, 0)
            .flags and ActivityInfo.FLAG_HARDWARE_ACCELERATED) == ActivityInfo.FLAG_HARDWARE_ACCELERATED
    }

    private fun getWebViews(): List<WebView> {
        return binding.webViewContainer
                .children.map { it as FrameLayout }
                .map { it.children.find { it is WebView } as WebView }
                .toList()
    }

    private fun getProgressBars(): List<ProgressBar> {
        return binding.webViewContainer
                .children.map { it as FrameLayout }
                .map { it.children.find { it is ProgressBar } as ProgressBar }
                .toList()
    }

    private fun addWebView() {
        val myContainer = FrameLayout(binding.root.context).also {
            binding.webViewContainer.addView(
                    it,
                    FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                    )
            )
        }.also { frame ->
            val webView = WebView(frame.context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                webViewLayerType?.let {
                    setLayerType(it, null)
                }
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(
                        "https://example.com",
                        BODY,
                        "text/html",
                        Charsets.UTF_8.name(),
                        null
                )
            }.also {
                frame.addView(
                        it,
                        FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                        )
                )
            }
            val progress = ProgressBar(frame.context, null, android.R.attr.progressBarStyle).also {
                frame.addView(
                        it,
                        FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER
                        )
                )
            }
        }


    }

}
