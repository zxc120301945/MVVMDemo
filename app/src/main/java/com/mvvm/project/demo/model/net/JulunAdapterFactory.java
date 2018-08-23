package com.mvvm.project.demo.model.net;

import com.mvvm.project.demo.R;
import com.mvvm.project.demo.model.net.suger.RunOnMainSchedulerTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by nirack on 17-6-1.
 */

public class JulunAdapterFactory extends CallAdapter.Factory  {
    private static class JulunCallAdapter<SOURCE> implements CallAdapter<R, SOURCE>{
        private final CallAdapter realAdapter;

        private JulunCallAdapter (CallAdapter realAdapter) {
            this.realAdapter = realAdapter;
        }

        @Override
        public Type responseType () {
            return realAdapter.responseType ();
        }

        /**
         * 适配 除了 Completable 之外 所有的 ReactX标准的接口,
         * @param call
         * @return
         */
        @Override
        public SOURCE adapt (Call<R> call) {
            final Object adapt = realAdapter.adapt (call);
            final RunOnMainSchedulerTransformer transformer = new RunOnMainSchedulerTransformer ();
            if (adapt instanceof Single) {
                return (SOURCE) ((Single) adapt).compose (transformer);
            }else if (adapt instanceof Maybe) {
                return (SOURCE) ((Maybe) adapt).compose (transformer);
            }else if (adapt instanceof Observable) {
                return (SOURCE) ((Observable) adapt).compose (transformer);
            }else if (adapt instanceof Flowable) {
                return (SOURCE) ((Flowable) adapt).compose (transformer);
            }/*else if (adapt instanceof Completable) {
                return (SOURCE) ((Completable) adapt).compose (transformer);
            }*/else {
                throw new UnsupportedOperationException("service定义不符合要求,应该为ReactiveX定义的几种接口之一[Single,Maybe,Observable,Flowable],此外 Completable 不要使用");
            }
        }
    }

    private final CallAdapter.Factory realFactory;

    private JulunAdapterFactory(CallAdapter.Factory realFactory) {
        this.realFactory = realFactory;
    }

    public static JulunAdapterFactory create(){
        return new JulunAdapterFactory (RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));
    }
    @Override
    public CallAdapter<?, ?> get (Type type, Annotation[] annotations, Retrofit retrofit) {
        final CallAdapter<?, ?> callAdapter = realFactory.get (type, annotations, retrofit);
        return new JulunCallAdapter (callAdapter);
    }
}
