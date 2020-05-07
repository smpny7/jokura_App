package net.jokura

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_restart.*
import kotlinx.android.synthetic.main.activity_top.*
import kotlinx.android.synthetic.main.activity_top.btm
import kotlinx.android.synthetic.main.activity_top.btn_restart
import kotlinx.android.synthetic.main.activity_top.hd_reload
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class TopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

        HitAPITask().execute("https://jokura.net/api/top")

        hd_reload.setOnClickListener {
            //ボタンがクリックされたらAPIを叩く。
            HitAPITask().execute("https://jokura.net/api/top")
        }

        btn_restart.setOnClickListener {
            val intent = Intent(this, RestartActivity::class.java)
            startActivity(intent)
        }

        btm.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val handler = Handler()
        var r: Runnable? = null

        r = Runnable {
            HitAPITask().execute("https://jokura.net/api/top")
            handler.postDelayed(r, 150000)
        }
        handler.post(r)

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

    inner class HitAPITask: AsyncTask<String, String, String>(){

        override fun doInBackground(vararg params: String?): String? {
            //ここでAPIを叩きます。バックグラウンドで処理する内容です。
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            val buffer: StringBuffer

            try {
                //param[0]にはAPIのURI(String)を入れます(後ほど)。
                //AsynkTask<...>の一つめがStringな理由はURIをStringで指定するからです。
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                connection.connect()  //ここで指定したAPIを叩いてみてます。

                //ここから叩いたAPIから帰ってきたデータを使えるよう処理していきます。

                //とりあえず取得した文字をbufferに。
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                buffer = StringBuffer()
                var line: String?
                while (true) {
                    line = reader.readLine()
                    if (line == null) {
                        break
                    }
                    buffer.append(line)
                    //Log.d("CHECK", buffer.toString())
                }

                //ここからは、今回はJSONなので、いわゆるJsonをParseする作業（Jsonの中の一つ一つのデータを取るような感じ）をしていきます。

                //先ほどbufferに入れた、取得した文字列
                val jsonText = buffer.toString()

                //JSONObjectを使って、まず全体のJSONObjectを取ります。
                val parentJsonObj = JSONObject(jsonText)
                //今回のJSONは配列になっているので（データは一つですが）、全体のJSONObjectから、getJSONArrayで配列"movies"を取ります。
                val parentJsonArray = parentJsonObj.getJSONArray("top")

                //JSONArrayの中身を取ります。映画"Your Name"のデータは、配列"movies"の０番目のデータなので、
                val detailJsonObj = parentJsonArray.getJSONObject(0)  //これもJSONObjectとして取得

                //moviesの0番目のデータのtitle項目をStringで取ります。これで中身を取れました。
//                val movieName: String = detailJsonObj.getString("state")  // => Your Name.
                //公開年を取りたい時も同じようにすれば良いです。
//                val year: Int = detailJsonObj.getInt("year")  // => 2016

                val state: Int = detailJsonObj.getInt("state")
                val backup: String = detailJsonObj.getString("backup")
                val online: Int = detailJsonObj.getInt("online")
                val Chazuke_0_: Int = detailJsonObj.getInt("Chazuke_0_")
                val chishige1217200: Int = detailJsonObj.getInt("chishige1217200")
                val fkddn123: Int = detailJsonObj.getInt("fkddn123")
                val kit130101: Int = detailJsonObj.getInt("kit130101")
                val meromin007: Int = detailJsonObj.getInt("meromin007")
                val RyuMura: Int = detailJsonObj.getInt("RyuMura")
                val suzumetengu: Int = detailJsonObj.getInt("suzumetengu")
                val TUIUS: Int = detailJsonObj.getInt("TUIUS")
                val Xx_piroboy_xX: Int = detailJsonObj.getInt("Xx_piroboy_xX")
                val YuuYuu523: Int = detailJsonObj.getInt("YuuYuu523")

                //Stringでreturnしてあげましょう。
                return "$state, $backup, $online, $Chazuke_0_, $chishige1217200, $fkddn123, $kit130101, $meromin007, $RyuMura, $suzumetengu, $TUIUS, $Xx_piroboy_xX, $YuuYuu523"  // => Your Name. - 2016

                //ここから下は、接続エラーとかJSONのエラーとかで失敗した時にエラーを処理する為のものです。
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            //finallyで接続を切断してあげましょう。
            finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            //失敗した時はnullやエラーコードなどを返しましょう。
            return null
        }

        //返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result == null) return

            val top = result.split(",").map { it.trim() }

            face1_gray.setVisibility(View.VISIBLE);
            face2_gray.setVisibility(View.VISIBLE);
            face3_gray.setVisibility(View.VISIBLE);
            face4_gray.setVisibility(View.VISIBLE);
            face5_gray.setVisibility(View.VISIBLE);
            face6_gray.setVisibility(View.VISIBLE);
            face7_gray.setVisibility(View.VISIBLE);
            face8_gray.setVisibility(View.VISIBLE);
            face9_gray.setVisibility(View.VISIBLE);
            face10_gray.setVisibility(View.VISIBLE);

            when (top[0].toIntOrNull()) {
                1 -> {
                    top_text1.text = "サーバの稼働状態 :  正常"
                    top_text2.text = "最終バックアップ :  " + top[1]
                    hd_text1.text = top[2]
                    if (top[3].toIntOrNull() == 1) face1_gray.setVisibility(View.INVISIBLE)
                    if (top[4].toIntOrNull() == 1) face2_gray.setVisibility(View.INVISIBLE)
                    if (top[5].toIntOrNull() == 1) face3_gray.setVisibility(View.INVISIBLE)
                    if (top[6].toIntOrNull() == 1) face4_gray.setVisibility(View.INVISIBLE)
                    if (top[7].toIntOrNull() == 1) face5_gray.setVisibility(View.INVISIBLE)
                    if (top[8].toIntOrNull() == 1) face6_gray.setVisibility(View.INVISIBLE)
                    if (top[9].toIntOrNull() == 1) face7_gray.setVisibility(View.INVISIBLE)
                    if (top[10].toIntOrNull() == 1) face8_gray.setVisibility(View.INVISIBLE)
                    if (top[11].toIntOrNull() == 1) face9_gray.setVisibility(View.INVISIBLE)
                    if (top[12].toIntOrNull() == 1) face10_gray.setVisibility(View.INVISIBLE)
                }
                2 -> {
                    top_text1.text = "サーバの稼働状態 :  起動中"
                    top_text2.text = "最終バックアップ :  " + top[1]
                    hd_text1.text = "-"
                }
                3 -> {
                    top_text1.text = "サーバの稼働状態 :  停止"
                    top_text2.text = "最終バックアップ :  " + top[1]
                    hd_text1.text = "-"
                }
                else -> {
                    top_text1.text = "サーバの稼働状態 :  取得不可"
                    top_text2.text = "最終バックアップ :  - - - - - - - -"
                    hd_text1.text = "-"
                }
            }
        }
    }
}
