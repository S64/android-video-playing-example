package jp.s64.android.example.videoplaying

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import jp.s64.android.example.videoplaying.databinding.LauncherActivityBinding

class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: LauncherActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        binding = LauncherActivityBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }

        binding.videoTagActivityDefault.setOnClickListener {
            launchActivity(VideoTagActivity_Default::class.java)
        }

        binding.videoTagActivityHwa.setOnClickListener {
            launchActivity(VideoTagActivity_Hwa::class.java)
        }

        binding.videoTagActivitySwr.setOnClickListener {
            launchActivity(VideoTagActivity_Swr::class.java)
        }

        binding.videoTagActivityActivityFlag.setOnClickListener {
            launchActivity(VideoTagActivity_ActivityFlag::class.java)
        }
    }

    private fun launchActivity(clazz: Class<*>) {
        startActivity(Intent(
            this,
            clazz
        ))
    }

}
