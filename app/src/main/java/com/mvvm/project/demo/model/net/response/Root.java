package com.mvvm.project.demo.model.net.response;

import java.lang.reflect.Type;

public class Root<T> {
    private Integer code;
    private Object message;
    private T data = null;
    /**
     * 不是"正常的"属性,只是为了方便在请求完成之后,如果出现了空值,根据这个类型参数来判断如何给 data属性赋值
     */
    private Type _typeParameter_;

    public Root() {
    }

    public Root(T data) {
        this.data = data;
    }

    public Integer getCode () {
        return code;
    }

    public void setCode (Integer code) {
        this.code = code;
    }

    public Object getMessage () {
        return message;
    }

    public void setMessage (Object message) {
        this.message = message;
    }

    public T getData () {
        return data;
    }

    public void setData (T data) {
        this.data = data;
    }

    public Type get_typeParameter_ () {
        return _typeParameter_;
    }

    public void set_typeParameter_ (Type _typeParameter_) {
        this._typeParameter_ = _typeParameter_;
    }
}
