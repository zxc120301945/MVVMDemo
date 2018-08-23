package com.mvvm.project.demo.model.net.suger;

public interface FunctionsAndActions {
    interface Action{
        public void run();
    }

    interface Consumer<T>{
        public void consume(T data);
    }

}
