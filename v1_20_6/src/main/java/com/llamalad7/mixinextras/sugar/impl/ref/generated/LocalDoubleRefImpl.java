package com.llamalad7.mixinextras.sugar.impl.ref.generated;

import com.llamalad7.mixinextras.sugar.impl.ref.LocalRefRuntime;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;

public final class LocalDoubleRefImpl
implements LocalDoubleRef {
    private double value;
    private byte state = 1;

    public double get() {
        if (this.state != 0) {
            LocalRefRuntime.checkState((byte)this.state);
        }
        return this.value;
    }

    public void set(double d) {
        if (this.state != 0) {
            LocalRefRuntime.checkState((byte)this.state);
        }
        this.value = d;
    }

    public void init(double d) {
        this.value = d;
        this.state = 0;
    }

    public double dispose() {
        if (this.state != 0) {
            LocalRefRuntime.checkState((byte)this.state);
        }
        this.state = (byte)2;
        return this.value;
    }
}