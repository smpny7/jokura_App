package net.jokura

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.jokura.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            //ボタンがクリックされたらAPIを叩く。
            HitAPITask().execute("https://jokura.net/api/test.php")
        }
    }

    inner class HitAPITask: AsyncTask<String, String, Array<String>>(){

        override fun doInBackground(vararg params: String?): Array<String>? {
            //ここでAPIを叩きます。バックグラウンドで処理する内容です。
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            val buffer: StringBuffer

            try {
                //param[0]にはAPIのURI(String)を入れます(後ほど)。
                //AsyncTask<...>の一つめがStringな理由はURIをStringで指定するからです。
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
                val state: Int = detailJsonObj.getInt("state")  // => 2016
                //公開年を取りたい時も同じようにすれば良いです。
                val address: String = detailJsonObj.getString("address")  // => Your Name.

//                val data = Server(1, "jokura-vanila.work", 2, 12, "1.14.4", 25565)

                val data: Array<String> = arrayOf("1", "jokura-vanila.work", "2", "12", "1.14.4", "25565")

                //Stringでreturnしてあげましょう。
                return data  // => Your Name. - 2016

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
        override fun onPostExecute(result:Array<String>) {
            super.onPostExecute(result)
            if(result == null) return

//            val (state, address, member_online, member_total, version, rcon) = result
            if (result[0] == "1") {
                state.text = "正常に稼働中"
                workingImageView.setImageResource(R.drawable.working)
            } else if (result[0] == "2") {
                state.text = "サーバ起動中"
                workingImageView.setImageResource(R.drawable.notworking)
            } else if (result[0] == "3") {
                state.text = "サーバ停止中"
                workingImageView.setImageResource(R.drawable.notworking)
            } else {
                state.text = "取得できませんでした"
                workingImageView.setImageResource(R.drawable.notworking)
            }

            address.text = result[1]
            member.text = result[2] + " / " + result[3]
            version.text = result[4]
            rcon.text = result[5]
        }
    }
}