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



## 引入与候选坐标

本次修复未发布新版本。仓库当前候选配置为 `io.github.asasa868:Dusk:0.0.30`，这只说明生成候选产物的坐标，不代表该版本已在 Maven Central 公开可用。先通过下述验证，再使用已实际发布的版本。

开发机运行 `bash gradlew :dawn:verifyCandidate`，通过 Windows 构建机生成候选并回传到本次命令打印的报告目录，其中仓库相对路径为 `dawn/build/candidate-repository`；持续集成使用 `CI=true bash gradlew :dawn:verifyCandidate` 时直接生成在当前工程的同一路径。独立消费项目可把 `DUSKMVX_CANDIDATE_REPO` 指向实际候选仓库，仅使用候选坐标验证：

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri(System.getenv('DUSKMVX_CANDIDATE_REPO')) }
    }
}
```

```groovy
implementation 'io.github.asasa868:Dusk:0.0.30'
```

确认 Maven Central 发布完成后只需 `google()` 与 `mavenCentral()`，删除临时候选仓库；版本必须以维护者核验的公开产物为准。处理器是框架内部编译依赖，不会通过 Dawn 运行时依赖传递到应用，消费注解使用独立的注解包。

JitPack 是另一条构建渠道，仓库配置明确以多模块格式发布 Dawn：仓库 `https://jitpack.io`，目标为 `com.github.asasa868.DuskMVX:dawn:已验证标签`。配置通过 JitPack 的版本变量覆盖渠道专用坐标，不把 Central 坐标用于 JitPack。这里的“已验证标签”需替换为渠道返回成功且提交一致的真实标签；本次没有创建标签或验证远端版本，不能据此声称此目标已可公开解析。多模块规则见 [JitPack 官方说明](https://docs.jitpack.io/building/#multi-module-projects)。

## 发布前置条件

1. 在拟发布提交上手动运行“候选验证与发布门禁”的 `workflow_dispatch`，将 `release_tag` 留空时只验证候选。流程完成构建、处理器及 Android 回归、模拟器生命周期仪器测试、静态检查、安全扫描和独立消费者验证，生成同提交和产物摘要绑定的附件；框架、处理器、设备生命周期及消费者各自必须有实际通过的测试报告，缺失、零用例、跳过或失败均拒绝通过。
2. 真实发布获得单独授权后，才可填写 `release_tag` 运行受控入口；标签只能是当前候选版本或带小写 `v` 前缀的同版本。流程先完成全部候选检查，再等待 `release` 环境人工审核，并在创建标签前重新校验同提交候选摘要。维护者须同时检查必要设备生命周期记录。
3. 仓库管理员须配置覆盖全部标签、无排除项且启用创建限制的有效规则集，并为 `release` 环境设置必要审核人；规则最多只允许单一自动化应用绕过，管理员角色、组织管理员或团队等人工绕过均被门禁拒绝。工作流通过接口校验实际策略，未配置则失败。受审核的发布自动化仍须具备该规则允许的创建权限，权限不足时保留失败，不关闭保护或改用未审核凭据。本次不配置远端策略，也不创建标签或发布。
4. 受控入口创建同提交标签后，必须确认指定 JitPack 版本的请求状态、数据格式、构建状态、提交和模块全部成功，才创建唯一 GitHub Release。受控入口在同一运行内完成后续渠道与发布门禁，直接推送标签不会创建 GitHub Release；渠道尚未完成时该运行失败，维护者可稍后重跑发布任务，已有标签必须仍指向同一提交。
5. 外部 JitPack 可能直接监听标签，标签后的检查无法阻止它提前开始构建。受控入口把候选检查与人工授权安排在创建标签之前；覆盖全部标签的远端创建限制仍是防止其他入口绕过的必要条件，未经远端策略核验不得声称所有标签路径已被阻断。
6. Maven Central 保留人工签名及认证发布入口。真实发布、版本调整和凭据轮换均需另行授权；不将开发者本地配置或真实凭据用于候选检查，也不把敏感值写入公开元数据。

CI 与 JitPack 使用 `CI=true bash gradlew` 走项目原生 Wrapper，不依赖开发者的 Windows 转发工具；开发机默认仍通过 Windows 构建机。发布门禁的纯离线回归可运行 `python3 -m unittest discover -s scripts/tests -p "test_release_gate.py"`。
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

