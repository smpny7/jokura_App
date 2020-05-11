package net.jokura

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_restart.*
import kotlinx.android.synthetic.main.activity_restart.btm
import kotlinx.android.synthetic.main.activity_restart.btn_restart
import kotlinx.android.synthetic.main.activity_restart.hd_reload
import kotlinx.android.synthetic.main.activity_top.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class BackupActivity : AppCompatActivity() {

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)

        HitAPITask().execute("https://jokura.net/api/backup")

        hd_back.setOnClickListener {
            finish()
            overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
        }

        hd_reload.setOnClickListener {
            //ボタンがクリックされたらAPIを叩く。
            HitAPITask().execute("https://jokura.net/api/backup")
        }

        btn_restart.setOnClickListener {
            fun View.notPressTwice() {
                this.isEnabled = false
                this.postDelayed({
                    this.isEnabled = true
                }, 2000L)
            }

            btn_restart.notPressTwice()
            hd_back.notPressTwice()
            hd_reload.notPressTwice()
            btm.notPressTwice()

            btn_restart.setImageResource(R.drawable.btn_backup_impossible)
            HitAPITask().execute("https://jokura.net/api/backup?check=1")
            Handler().postDelayed(Runnable {
                if (hd_possible_text.text == "実行可能です") {
                    HitAPITask().execute("https://jokura.net/api/backup/do.php?from=jokura.net")
                    Toast.makeText(applicationContext, "サーバに処理を送信しました！", Toast.LENGTH_LONG).show()
                    finish()
                    overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
                } else {
                    Toast.makeText(applicationContext, "Error: 処理を送信できませんでした", Toast.LENGTH_LONG).show()
                    finish()
                    overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
                }
            }, 2000L)
        }

        btm.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        val handler = Handler()
//        var r: Runnable? = null
//
//        r = Runnable {
//            HitAPITask().execute("https://jokura.net/api")
//            handler.postDelayed(r, 10000)
//        }
//        handler.post(r)
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
                val parentJsonArray = parentJsonObj.getJSONArray("backup")

                //JSONArrayの中身を取ります。映画"Your Name"のデータは、配列"movies"の０番目のデータなので、
                val detailJsonObj = parentJsonArray.getJSONObject(0)  //これもJSONObjectとして取得

                //moviesの0番目のデータのtitle項目をStringで取ります。これで中身を取れました。
//                val movieName: String = detailJsonObj.getString("state")  // => Your Name.
                //公開年を取りたい時も同じようにすれば良いです。
//                val year: Int = detailJsonObj.getInt("year")  // => 2016

                val able: Int = detailJsonObj.getInt("able")
                val check: Int = detailJsonObj.getInt("check")
//                val address: String = "a"
//                val memberOnline: Int = detailJsonObj.getInt("member_online")
//                val memberTotal: Int = detailJsonObj.getInt("member_total")
//                val version: String = detailJsonObj.getString("version")
//                val rcon: Int = detailJsonObj.getInt("rcon")

                //Stringでreturnしてあげましょう。
                return "$able, $check"  // => Your Name. - 2016

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

            val restart = result.split(",").map { it.trim() }

            when (restart[0].toIntOrNull()) {
                1 -> {
                    hd_possible_text.text = "実行可能です"
                    note_ttl.text = "注意事項"
                    note_text1.text = "・ボタン押下後，10秒後に再起動が行われます"
                    note_text2.text = "・サーバに残っているメンバーは切断されます"
                    note_ttl.setTextColor(Color.parseColor("#2699FB"));
                    note_ttl.setShadowLayer(6.toFloat(), 0.toFloat(), 3.toFloat(), Color.parseColor("#8CC7F6"));
                    note_text1.setTextColor(Color.parseColor("#4AB2FF"));
                    note_text2.setTextColor(Color.parseColor("#4AB2FF"));
                    hd_possible.setImageResource(R.drawable.hd_possible)
                    note_background.setImageResource(R.drawable.note_background_possible)
                    if (restart[1] == "1") {
                        btn_restart.setImageResource(R.drawable.btn_backup_impossible)
                    } else {
                        btn_restart.setImageResource(R.drawable.btn_backup_possible)
                    }
                }
                else -> {
                    hd_possible_text.text = "実行できません"
                    note_ttl.text = "考えられる原因"
                    note_text1.text = "・サーバが起動していない，又はアクセスできない"
                    note_text2.text = "・他のユーザが現在サーバ処理を行っている"
                    note_ttl.setTextColor(Color.parseColor("#FF6465"));
                    note_ttl.setShadowLayer(6.toFloat(), 0.toFloat(), 3.toFloat(), Color.parseColor("#F2ACAE"));
                    note_text1.setTextColor(Color.parseColor("#FF6465"));
                    note_text2.setTextColor(Color.parseColor("#FF6465"));
                    hd_possible.setImageResource(R.drawable.hd_impossible)
                    note_background.setImageResource(R.drawable.note_background_impossible)
                    btn_restart.setImageResource(R.drawable.btn_backup_impossible)
                }
            }
        }
    }
}
