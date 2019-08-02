package com.mvvm.project.demo.model.net;


import android.support.annotation.NonNull;

import com.mvvm.project.demo.model.net.suger.FunctionsAndActions;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;
import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class CancelableObservableSubscriber<T> implements Observer<T>, FlowableSubscriber<T>,
        SingleObserver<T>, MaybeObserver<T> {

    private static class DefaultConsumer<T> implements FunctionsAndActions.Consumer<T>,FunctionsAndActions.Action{
        @Override
        public void run ()  { }

        @Override
        public void consume (T data) { }
    }

    private Subscription subscription;
    private Disposable disposable;
    /**
     * 如果是作为 FlowableSubscriber ,onSubscriber的时候请求的次数,如果为null抛出异常,如果为0,则认为是Long.MAX_VALUE
     */
    private Long flowableRequestTimes = null;

    public Long getFlowableRequestTimes () {
        return flowableRequestTimes;
    }

    /**
     * @see #flowableRequestTimes
     * @param flowableRequestTimes
     * @return
     */
    public CancelableObservableSubscriber<T> withFlowableRequestTimes (Long flowableRequestTimes) {
        this.flowableRequestTimes = flowableRequestTimes;
        return this;
    }

    @Override
    public void onSubscribe (@io.reactivex.annotations.NonNull Subscription s) {
        this.subscription = s;
        if(flowableRequestTimes ==null){
            throw new RuntimeException ("使用 FlowableSubscriber 的时候一定要设置 flowableRequestTimes ");
        }
        if(flowableRequestTimes ==0){
            flowableRequestTimes = Long.MAX_VALUE;
        }
        s.request (flowableRequestTimes);
        onSubscribe ();
    }

    @Override
    public void onSubscribe (@NonNull Disposable d) {
        this.disposable = d;
        onSubscribe ();
    }

    public void onSubscribe () {
        Requests.INSTANCE.bindCaller(this);
    }

    /**
     * 几个需要特殊处理的 ,非 200 的 业务 code
     */
    private int[] specifiedCodes = new int[0];
    /**
     * 调用者的id
     */
    private final String requestCaller;

    private final DefaultConsumer DEFAULT_CONSUMER = new DefaultConsumer ();

    private FunctionsAndActions.Consumer<? super T> onNext = DEFAULT_CONSUMER;
    private FunctionsAndActions.Consumer<Throwable> onError = DEFAULT_CONSUMER;
    private FunctionsAndActions.Action onCompleted = DEFAULT_CONSUMER;

    private FunctionsAndActions.Action finalCall = DEFAULT_CONSUMER;

    private CancelableObservableSubscriber(RequestCaller requestCaller) {
        this.requestCaller = requestCaller.getRequestCallerId ();
    }

    public CancelableObservableSubscriber() {
        requestCaller = "";
    }

    public static <T> CancelableObservableSubscriber<T> create(RequestCaller requestCaller){
        return new CancelableObservableSubscriber<> (requestCaller);
    }

    public static <T> CancelableObservableSubscriber<T> createWithoutCaller(){
        return new CancelableObservableSubscriber<> ();
    }

    public static <T> CancelableObservableSubscriber<T> create(@NonNull RequestCaller requestCaller, @NonNull FunctionsAndActions.Consumer<? super T> whenSuccess){
        return new CancelableObservableSubscriber<T> (requestCaller).withOnSuccess (whenSuccess);
    }


    public CancelableObservableSubscriber<T> ifError (@NonNull FunctionsAndActions.Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public CancelableObservableSubscriber<T> withOnSuccess (@NonNull FunctionsAndActions.Consumer<? super T> onNext) {
        this.onNext = onNext;
        return this;
    }

    public CancelableObservableSubscriber<T> withOnCompleted(@NonNull FunctionsAndActions.Action onCompleted) {
        this.onCompleted = onCompleted;
        return this;
    }

    public CancelableObservableSubscriber<T> withFinalCall(@NonNull FunctionsAndActions.Action call) {
        this.finalCall = call;
        return this;
    }

    public CancelableObservableSubscriber<T> withSpecifiedCodes (int... specifiedCodes) {
        this.specifiedCodes = specifiedCodes;
        return this;
    }

    @Override
    public void onSuccess (@io.reactivex.annotations.NonNull T t) {
        onNext(t);
    }

    @Override
    public void onNext(@NonNull T t) {
        doCleanUp ();
        onNext.consume (t);
        if(finalCall !=null) {
            finalCall.run ();
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        doCleanUp ();
        if(onError != null) {
            onError.consume (e);
        }
        if(finalCall !=null) {
            finalCall.run ();
        }
    }

    @Override
    public void onComplete () {
        doCleanUp ();
        onCompleted.run ();
    }

    private void doCleanUp () {
        Requests.INSTANCE.removeFromCacheWhenRequestIsCompleted(requestCaller, this);
    }

    /**
     * 取消请求的响应
     */
    public void cancel(){
        if(this.subscription != null){
            this.subscription.cancel ();
        }else {
            if(this.disposable !=null){
                this.disposable.dispose ();
            }
        }
    }

    public int[] getSpecifiedCodes () {
        return specifiedCodes;
    }

    public String getRequestCaller () {
        return requestCaller;
    }

}
