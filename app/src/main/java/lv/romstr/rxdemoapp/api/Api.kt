package lv.romstr.rxdemoapp.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("{month}/{day}/date")
    fun date(@Path("month") month: String, @Path("day") day: String): Single<String>

    @GET("random/date")
    fun randomDate(): Single<String>

    @GET("{number}/math")
    fun math(@Path("number") number: String): Single<String>

    @GET("{year}/year")
    fun year(@Path("year") year: String): Single<String>

    @GET("{number}/trivia")
    fun trivia(@Path("number") number: String): Single<String>

}