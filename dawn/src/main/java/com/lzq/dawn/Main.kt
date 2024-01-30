package com.lzq.dawn

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * @projectName com.lzq.dawn
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 16:13
 * @version
 * @description:
 */

fun main(){

    runBlocking {
        val async1 = async {
            asyncFun("Server A list 1")
        }
        println("------${async1.await().toString()}-------")
        val async2 = async {
            asyncFun("从 ${async1.await().second}")
        }
        println("------${async2.await().toString()}-------")
        val async3 = async {
            asyncFun("从 ${async2.await().second}")
        }
        println("------${async3.await().toString()}-------")

//        val result = listOf(
//            async { asyncFun("Server A") },
//            async { asyncFun("Server B") },
//            async { asyncFun("Server C") },
//            async { asyncFun("Server D") }
//        )
//        val results = awaitAll(*result.toTypedArray())
//
//        results.forEachIndexed { index, pair ->
//            println("$index ,从${pair.first} 接收到数据：${pair.second}")
//        }


    }
}



suspend fun asyncFun(funString: String): Pair<String, String> {
    delay(2000L)
    val data = "Async data from $funString"
    return funString to data

}



