package lv.romstr.rxdemoapp.api

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create


private const val BASE_URL = "http://numbersapi.com/"

fun numbers(): Api =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .client(OkHttpClient())
        .build()
        .create()

sealed class RequestType(private val call: (Api) -> Single<String>) {

    fun invoke(api: Api): Single<String> = call(api)

    class DateRequest(month: String, day: String) : RequestType({ it.date(month, day) })

    object RandomDateRequest : RequestType({ it.randomDate() })

    class MathRequest(number: String) : RequestType({ it.math(number) })

    class TriviaRequest(number: String) : RequestType({ it.trivia(number) })

    class YearRequest(year: String) : RequestType({ it.year(year) })

}