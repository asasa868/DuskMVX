# 1.kotlin flow 学习

## map函数（将一个值映射成另一个值）

```kotlin
fun main() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow.map {
        it * it
    }.collect {
        println(it)
    }
}
```

## filter函数（过滤掉指定条件的数据）

```kotlin
fun main() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow.filter {
        it % 2 == 0
    }.map {
        it * it
    }.collect {
        println(it)
    }
}
```

## onEach函数（遍历每条数据）

```kotlin
fun main() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow.onEach {
        println(it)
    }.collect {
    }
}

fun main() {
    val flow = flowOf(1, 2, 3, 4, 5)
    flow.filter {
        it % 2 == 0
    }.onEach {
        println(it)
    }.map {
        it * it
    }.collect {
        println(it)
    }
}
```

## debounce函数（确保flow的各项数据存在一定的时间 如果时间点过于接近数据指挥保留最后一条）

```kotlin
fun main() {
    flow {
        emit(1)
        emit(2)
        kotlinx.coroutines.delay(600)
        emit(3)
        kotlinx.coroutines.delay(100)
        emit(4)
        kotlinx.coroutines.delay(100)
        emit(5)
    }.debounce(500)
        .collect {
            println(it)
        }
}
```

## sample函数（可以从数据流中 按照 一定的时间间隔 采样某条数据）

应用场景如 控制弹幕的发送数量

```kotlin
fun main() {
    flow {
        while (true) {
            emit("发送一条弹幕")
        }
    }.sample(2500)
        .flowOn(Dispatchers.IO)
        .collect {
            println(it)
        }
}
```

## reduce函数（会通过参数给 一个flow 的累计值和 当前值 可以在函数中对数据进行操作） 挂起函数（在它之后不能再接其他函数了）

```kotlin
fun main() {
    val result = flow {
        for (i in (1..100)) {
            emit(i)
        }
    }.reduce { accumulator: Int, value: Int -> accumulator + value }
    println(result)
}
```

## fold函数（和reduce类似，区别是fold函数传入初始值 这个初始值会作为首个积累值传入fold函数体中）

```kotlin
fun main() {
    val result = flow {
        for (i in ('A'..'z')) {
            emit(i.toString())
        }
    }.fold("fold:") { acc, value -> acc + value }
    println(result)
}
```

## flatMapConcat函数（flatMap 核心 就是将数据进行映射，合并，亚压平成一个 flow）

应用场景如 第二个网络请求依赖与第一个请求的结果

```kotlin
fun main() {
    flowOf(1, 2, 3)
        .flatMapConcat {
            flowOf("a${it}", "b${it}")
        }.collect {
            println(it)
        }
}
```

## flatMapMerge函数（和flatMapConcat函数的区别就是 flatMapConcat函数是按顺序的 而flatMapMerge 是并发的 哪个数据的时间短 就优先得到处理）

```kotlin
fun main() {
    flowOf(300, 200, 100)
        .flatMapConcat {
            flow {
                kotlinx.coroutines.delay(it.toLong())
                emit("a${it}")
                emit("b${it}")
            }
        }.collect {
            println(it)
        }
}
```

## flatMapLatest函数（flow1数据传递到flow2中会立刻处理 ，但是如果flow2中上一次数据还没处理完，就会取消剩余逻辑 开始处理最新的）

应用场景如 需要同时并发请求接口

```kotlin
fun main() {
    flow {
        emit(1)
        kotlinx.coroutines.delay(150)
        emit(2)
        kotlinx.coroutines.delay(50)
        emit(3)
    }.flatMapLatest {
        flow {
            kotlinx.coroutines.delay(100)
            emit("$it")
        }
    }.collect { println(it) }
}
```

## zip函数（连接两个或者多个flow，它们之间的运行关系是并行的,zip函数的规则是 只要其中一个flow中的数据全部处理结束 就会终止运行）

```kotlin
fun main() {
    val flow1 = flowOf("A", "B", "C")
    val flow2 = flowOf(1, 2, 3, 4, 5)
    flow1.zip(flow2) { a, b ->
        a + b
    }.collect { println(it) }
}
```

## buffer函数（提供一种背压的处理策略，解决数据上下游速度不一致问题,此策略并不会丢弃数据）

```kotlin
fun main() {
    flow {
        emit(1)
        kotlinx.coroutines.delay(1000)
        emit(2)
        kotlinx.coroutines.delay(2000)
        emit(3)
    }.onEach {
        println("$it is ready")
    }.buffer()
        .collect {
            delay(1000)
            println("$it is handled")
        }
}
```

## conflate函数（提供一种被压得处理策略，解决数据上下游速度不一致问题,如果当前数据处理完，此策略会丢弃中间多余数据，处理最新的数据）

应用场景如 股票软件会显示最新数据 后台会一直推送最新数据 客户端最需要处理最新的数据  过时的数据不需要在处理

```kotlin
fun main() {
    flow {
        var count = 0
        while (true) {
            emit(count)
            delay(1000)
            count++
        }
    }.conflate()
        .collect {
            println("start handle $it")
            delay(2000)
            println("finish handle $it")
        }
}
```