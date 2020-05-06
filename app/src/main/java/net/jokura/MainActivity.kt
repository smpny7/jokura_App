package net.jokura

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private var r: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reload.setOnClickListener {
            HitAPITask().execute("https://jokura.net/api")
        }

        r = Runnable {
            HitAPITask().execute("https://jokura.net/api")
            handler.postDelayed(r, 1000)
        }
        handler.post(r)
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
                val parentJsonArray = parentJsonObj.getJSONArray("server")

                //JSONArrayの中身を取ります。映画"Your Name"のデータは、配列"movies"の０番目のデータなので、
                val detailJsonObj = parentJsonArray.getJSONObject(0)  //これもJSONObjectとして取得

                //moviesの0番目のデータのtitle項目をStringで取ります。これで中身を取れました。
//                val movieName: String = detailJsonObj.getString("state")  // => Your Name.
                //公開年を取りたい時も同じようにすれば良いです。
//                val year: Int = detailJsonObj.getInt("year")  // => 2016

                val state: Int = detailJsonObj.getInt("state")
                val address: String = detailJsonObj.getString("address")
                val memberOnline: Int = detailJsonObj.getInt("member_online")
                val memberTotal: Int = detailJsonObj.getInt("member_total")
                val version: String = detailJsonObj.getString("version")
                val rcon: Int = detailJsonObj.getInt("rcon")

                //Stringでreturnしてあげましょう。
                return "$state, $address, $memberOnline, $memberTotal, $version, $rcon"  // => Your Name. - 2016

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

            val server = result.split(",").map { it.trim() }

            when (server[0].toIntOrNull()) {
                1 -> {
                    state.text = "正常に稼働中"
                    address.text = server[1]
                    member.text = server[2] + " / " + server[3]
                    version.text = server[4]
                    rcon.text = server[5]
                    workingImageView.setImageResource(R.drawable.working)
                }
                2 -> {
                    state.text = "サーバ起動中"
                    address.text = "- - - - - - - - - - - - - -"
                    member.text = "- / -"
                    version.text = "- - - - -"
                    rcon.text = "25565"
                    workingImageView.setImageResource(R.drawable.notworking)
                }
                3 -> {
                    state.text = "サーバ停止中"
                    address.text = "- - - - - - - - - - - - - -"
                    member.text = "- / -"
                    version.text = "- - - - -"
                    rcon.text = "25565"
                    workingImageView.setImageResource(R.drawable.notworking)
                }
                else -> {
                    state.text = "取得できませんでした"
                    address.text = "- - - - - - - - - - - - - -"
                    member.text = "- / -"
                    version.text = "- - - - -"
                    rcon.text = "- - - - -"
                    workingImageView.setImageResource(R.drawable.notworking)
                }
            }
        }
    }
}
