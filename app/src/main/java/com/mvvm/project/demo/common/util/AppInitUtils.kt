package com.mvvm.project.demo.common.util

import com.mvvm.project.demo.DemoApplication

/**
 * App初始化各种工具类
 */
object AppInitUtils {

    fun onCreateInit(app: DemoApplication) {
        if (app == null) {
            return
        }

//        initFresco(app.applicationContext)
        ToastUtils.init(app)//初始化自定义土司
    }

//    private fun initFresco(context: Context) {
//        LogUtils.i("initFresco")
//        //缓存的试着
//        val cacheConfigBuilder: DiskCacheConfig.Builder = DiskCacheConfig.newBuilder(context)
//
//        val cacheDir: File = context.cacheDir
//        val frescoCacheDir = File(cacheDir, "fresco")
//        if (!frescoCacheDir.exists()) {
//            frescoCacheDir.mkdir()
//        }
//
//        cacheConfigBuilder.setVersion(BuildConfig.VERSION_CODE)
////                .setMaxCacheSize (1000L)
////                .setMaxCacheSizeOnLowDiskSpace (100L)
////                .setMaxCacheSizeOnVeryLowDiskSpace (10)
//                .setCacheErrorLogger(
//                        { cacheErrorCategory: CacheErrorLogger.CacheErrorCategory, clazz: Class<*>, _: String, throwable: Throwable? ->
//                            throwable?.printStackTrace()
//                            println("fresco 缓存错误.... cacheErrorCategory -> $cacheErrorCategory class $clazz ")
//                        })
//                .setBaseDirectoryName(frescoCacheDir.name)
//                .setBaseDirectoryPathSupplier({ frescoCacheDir })
//
//
//        //总体的配置
//        val config: ImagePipelineConfig = OkHttpImagePipelineConfigFactory
//                .newBuilder(context, Requests.getImageClient())//设置 使用 okhttp 客户端
////                .setSmallImageDiskCacheConfig(cacheConfigBuilder.build())//设置缓存
//                .setMainDiskCacheConfig(cacheConfigBuilder.build())
//                .setDownsampleEnabled(true)//设置图片压缩时支持多种类型的图片
//                .build()
//
//        Fresco.initialize(context, config)
//    }
}