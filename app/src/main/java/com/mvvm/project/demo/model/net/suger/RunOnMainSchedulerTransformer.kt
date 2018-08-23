package com.mvvm.project.demo.model.net.suger

import CustomizedIoScheduler
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import org.reactivestreams.Publisher

class RunOnMainSchedulerTransformer<T> : ObservableTransformer<T, T>, SingleTransformer<T, T>
        , MaybeTransformer<T, T>, CompletableTransformer, FlowableTransformer<T, T> {
    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(CustomizedIoScheduler).observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.subscribeOn(CustomizedIoScheduler).observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(CustomizedIoScheduler).observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(CustomizedIoScheduler).observeOn(AndroidSchedulers.mainThread())
    }

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(CustomizedIoScheduler).observeOn(AndroidSchedulers.mainThread())
    }

}