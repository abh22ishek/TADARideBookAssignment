package com.example.tadaassignment.data.remote.mock

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

/**
 * Stands in for a real backend by short-circuiting every request with canned
 * JSON. Lives at the OkHttp layer so the repository behaves as it would
 * against a live server.
 */
class MockInterceptor @Inject constructor(
    private val gson: Gson
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val json = when {
            path.endsWith("/area") -> {
                val lat = request.url.queryParameter("lat")?.toDoubleOrNull() ?: MockDataSource.DEFAULT_LAT
                val lng = request.url.queryParameter("lng")?.toDoubleOrNull() ?: MockDataSource.DEFAULT_LNG
                gson.toJson(MockDataSource.areaAt(lat, lng))
            }

            path.endsWith("/books") && request.method == "GET" -> {
                val year = request.url.queryParameter("year")?.toIntOrNull() ?: 0
                val month = request.url.queryParameter("month")?.toIntOrNull() ?: 0
                gson.toJson(bookedRoutes[monthKey(year, month)].orEmpty())
            }

            path.endsWith("/books") -> {
                val bodyString = request.body?.let { body ->
                    okio.Buffer().also { body.writeTo(it) }.readUtf8()
                }.orEmpty()
                val bookRequest = gson.fromJson(bodyString, BookRequest::class.java)
                val now = Calendar.getInstance()
                val bookResponse = BookResponse(
                    id = "book-${System.currentTimeMillis()}",
                    a = bookRequest.a,
                    b = bookRequest.b,
                    price = estimatePrice(bookRequest.a, bookRequest.b)
                )
                bookedRoutes
                    .getOrPut(monthKey(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1)) {
                        CopyOnWriteArrayList()
                    }
                    .add(bookResponse)
                gson.toJson(bookResponse)
            }

            else -> "{}"
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("Mocked")
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private companion object {
        val bookedRoutes = ConcurrentHashMap<String, CopyOnWriteArrayList<BookResponse>>()

        fun monthKey(year: Int, month: Int) = "$year-$month"
    }
}
