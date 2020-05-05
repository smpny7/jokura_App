package net.jokura

import android.content.pm.ActivityInfo
import android.os.AsyncTask
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HitAPITask().execute("https://jokura.net/api")

        reload.setOnClickListener {
            //ボタンがクリックされたらAPIを叩く。
            HitAPITask().execute("https://jokura.net/api")
        }
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

//    data class Server (
//        val state : Int,
//        val address : String,
//        val memberOnline : Int,
//        val memberTotal : Int,
//        val version : String,
//        val rcon : Int
//    )
//
//    inner class HitAPITask: AsyncTask<String, String, String>(){
//
//        override fun doInBackground(vararg params: String?): String? {
//            //ここでAPIを叩きます。バックグラウンドで処理する内容です。
//            var connection: HttpURLConnection? = null
//            var reader: BufferedReader? = null
//            val buffer: StringBuffer
//
//            try {
//                //param[0]にはAPIのURI(String)を入れます(後ほど)。
//                //AsyncTask<...>の一つめがStringな理由はURIをStringで指定するからです。
//                val url = URL(params[0])
//                connection = url.openConnection() as HttpURLConnection
//                connection.connect()  //ここで指定したAPIを叩いてみてます。
//
//                //ここから叩いたAPIから帰ってきたデータを使えるよう処理していきます。
//
//                //とりあえず取得した文字をbufferに。
//                val stream = connection.inputStream
//                reader = BufferedReader(InputStreamReader(stream))
//                buffer = StringBuffer()
//                var line: String?
//                while (true) {
//                    line = reader.readLine()
//                    if (line == null) {
//                        break
//                    }
//                    buffer.append(line)
//                    //Log.d("CHECK", buffer.toString())
//                }
//
//                //ここからは、今回はJSONなので、いわゆるJsonをParseする作業（Jsonの中の一つ一つのデータを取るような感じ）をしていきます。
//
//                //先ほどbufferに入れた、取得した文字列
//                val jsonText = buffer.toString()
//
//                //JSONObjectを使って、まず全体のJSONObjectを取ります。
//                val parentJsonObj = JSONObject(jsonText)
//                //今回のJSONは配列になっているので（データは一つですが）、全体のJSONObjectから、getJSONArrayで配列"movies"を取ります。
//                val parentJsonArray = parentJsonObj.getJSONArray("server")
//
//                //JSONArrayの中身を取ります。映画"Your Name"のデータは、配列"movies"の０番目のデータなので、
//                val detailJsonObj = parentJsonArray.getJSONObject(0)  //これもJSONObjectとして取得
//
//                //moviesの0番目のデータのtitle項目をStringで取ります。これで中身を取れました。
//
//                val state: Int = detailJsonObj.getInt("state")
//                val address: String = detailJsonObj.getString("address")
//                val memberOnline: Int = detailJsonObj.getInt("member_online")
//                val memberTotal: Int = detailJsonObj.getInt("member_total")
//                val version: String = detailJsonObj.getString("version")
//                val rcon: Int = detailJsonObj.getInt("rcon")
//
////                val state: String = detailJsonObj.getString("state")
////                val address: String = detailJsonObj.getString("address")
////                val memberOnline: String = detailJsonObj.getString("member_online")
////                val memberTotal: String = detailJsonObj.getString("member_total")
////                val version: String = detailJsonObj.getString("version")
////                val rcon: String = detailJsonObj.getString("rcon")
//
////                val data = Server(1, "jokura-vanila.work", 2, 12, "1.14.4", 25565)
//                val data = Server(state, address, memberOnline, memberTotal, version, rcon)
//
////                return data.toString()
//
//                return "$state, $address"
//
////                return arrayOf(state, address, memberOnline, memberTotal, version, rcon)
//
//                //Stringでreturnしてあげましょう。
////                return data  // => Your Name. - 2016
//
//                //ここから下は、接続エラーとかJSONのエラーとかで失敗した時にエラーを処理する為のものです。
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            //finallyで接続を切断してあげましょう。
//            finally {
//                connection?.disconnect()
//                try {
//                    reader?.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//            //失敗した時はnullやエラーコードなどを返しましょう。
//            return null
//        }
//
//        //返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
//        override fun onPostExecute(result:String) {
//            super.onPostExecute(result)
//            if(result == null) return
//
////            val (state, address, member_online, member_total, version, rcon) = result
//
//            address.text = result
//
////            if (result[0] == "1") {
////                state.text = "正常に稼働中"
////                workingImageView.setImageResource(R.drawable.working)
////            } else if (result[0] == "2") {
////                state.text = "サーバ起動中"
////                workingImageView.setImageResource(R.drawable.notworking)
////            } else if (result[0] == "3") {
////                state.text = "サーバ停止中"
////                workingImageView.setImageResource(R.drawable.notworking)
////            } else {
////                state.text = "取得できませんでした"
////                workingImageView.setImageResource(R.drawable.notworking)
////            }
////
////            address.text = result[1]
////            member.text = result[2] + " / " + result[3]
////            version.text = result[4]
////            rcon.text = result[5]
//        }
//    }
//}