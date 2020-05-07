package net.jokura

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        btn_restart.setOnClickListener {
            val intent = Intent(this, RestartActivity::class.java)
            startActivity(intent)
        }

        btm.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        fun monoBitmap(outBitmap: Bitmap, inBitmap: Bitmap) {
            val width: Int = outBitmap.width
            val height: Int = outBitmap.height

            var color: Int
            var mono: Int

            for (j in 0..height - 1 step 1) {
                for (i in 0..width - 1 step 1) {
                    color = inBitmap.getPixel(i, j)
                    mono = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
                    outBitmap.setPixel(i, j, Color.rgb(mono, mono, mono))
                }
            }
        }

        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=7027caeead3a9ba0d14a4ffcd4f0bc024917fe4b4fc660f809a8fb81c32fd530")
            .into(face1);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=307b0ab7db4d6a61ef9c46b4eb436cce1ea3d68381347520cce164b2b19e5b7a")
            .into(face2);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=bab87b6f20703581b9119401ad08c83d31ee6f8a6622519738f3a46d895ab169")
            .into(face3);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=b3b203fc695b6bd44cf1e19e2553b04e900c6291d13c668f3efa4f9b3e7f464")
            .into(face4);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=20fb08d8cfed0036460dd1cdd6dd418e45426988a84d8eee1860b96997a0bc93")
            .into(face5);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=1b367ba135b9eb0c231085fcba3f830a799ce6db46c47f3d3e96e95c5bbdf21c")
            .into(face6);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=4c7b0468044bfecacc43d00a3a69335a834b73937688292c20d3988cae58248d")
            .into(face7);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=41b8dbada89f0ec4643b0591bc894dbb935cf0c12e34c6501c2888da37ac3b77")
            .into(face8);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=590674548fc666e7e9c815e86dbd78f80ff37b0265281a18ff90db2ba68d13f4")
            .into(face9);
        Picasso.get()
            .load("https://jokura.net/assets/php/tool/skin.php?id=3afac34b592a8dcea0be6df00571be71490a335d56a0e903712386bdf01cd90e")
            .into(face10);

    }
}
