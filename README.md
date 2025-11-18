# <p align="center"> DuskMVX</p>

> **DuskMVX** 是一个Android框架，使用了：
> **组件化、Kotlin、MVVM、Jetpack、MVI、Kotlin-Coroutine**，
> 项目中的工具类是使用了[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)的工具类，
> 选择了平时项目常用的，其他没用到的就没有引入，目前只是搭建了基本的框架，还在不断完善当中。
> 
> 如果有发现框架中的错误点，可以在 **GitHub** 上提 **Issues**，
> 欢迎大家来Star和Issues。🎆🎆🎆
>
> 项目地址：[DuskMVX](https://github.com/asasa868/DuskMVX)
> [![](https://jitpack.io/v/asasa868/DuskMVX.svg)](https://jitpack.io/#asasa868/DuskMVX)



## 引入
**使用jitpack仓库**
1.在项目的根目录的build.gradle中添加
```groovy
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
2.在项目的build.gradle中添加
```groovy
       implementation 'com.github.asasa868:DuskMVX:xxx'
```
**使用maven仓库**
1.在项目的根目录的build.gradle中添加
```groovy
dependencyResolutionManagement {
		repositories {
			mavenCentral()
		}
	}
```
2.在项目的build.gradle中添加
```groovy
       implementation 'com.github.asasa868:DuskMVX:xxx'
```
## 具体用法
架构中封装了两种架构分别是MVVM，和MVI。
使用MVVM架构可以继承**BaseMvvmActivity**，使用MVI架构可以继承**BaseMviActivity**，
Fragment就是**BaseMvvmFragment**和**BaseMviFragment**，compose版本还在研究。


## 资料 
下面就是框架中使用到的库和未来版本要添加的功能所使用到的库（包括但不限于）。
* [Kotlin](https://github.com/JetBrains/kotlin)
* [Kotlin-Coroutines-Flow](https://github.com/JetBrains/kotlin)
* [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
* [Hilt](https://developer.android.com/jetpack/androidx/releases/hilt)
* [OkHttp](https://github.com/square/okhttp)
* [Retrofit](https://github.com/square/retrofit)
* [MMKV](https://github.com/Tencent/MMKV)
* [Coil](https://github.com/coil-kt/coil)
* [EventBus](https://github.com/greenrobot/EventBus)
* [Bugly](https://bugly.qq.com/v2/index)
* [LeakCanary](https://github.com/square/leakcanary)
* [AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)

