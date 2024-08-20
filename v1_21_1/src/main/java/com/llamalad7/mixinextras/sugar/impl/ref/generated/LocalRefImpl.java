package com.llamalad7.mixinextras.sugar.impl.ref.generated;

import com.llamalad7.mixinextras.sugar.impl.ref.LocalRefRuntime;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

public class LocalRefImpl<T> implements LocalRef<T> {
    private T value;
    private byte state = 1;

    public LocalRefImpl() {
    }

    public T get() {
        if (this.state != 0) {
            LocalRefRuntime.checkState(this.state);
        }

        return this.value;
    }

    public void set(T var1) {
        if (this.state != 0) {
            LocalRefRuntime.checkState(this.state);
        }

        this.value = var1;
    }

    public void init(T var1) {
        this.value = var1;
        this.state = 0;
    }

    public Object dispose() {
        if (this.state != 0) {
            LocalRefRuntime.checkState(this.state);
        }

        this.state = 2;
        return this.value;
    }
}
