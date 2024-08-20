package com.llamalad7.mixinextras.sugar.impl.ref.generated;

import com.llamalad7.mixinextras.sugar.impl.ref.LocalRefRuntime;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

public final class LocalBooleanRefImpl implements LocalBooleanRef {
    private boolean value;
    private byte state = 1;

    public boolean get() {
        if (this.state != 0) {
            LocalRefRuntime.checkState((byte)this.state);
        }
        return this.value;
    }

    public void set(boolean bl) {
        if (this.state != 0) {
            LocalRefRuntime.checkState((byte)this.state);
        }
        this.value = bl;
    }

    public void init(boolean bl) {
        this.value = bl;
        this.state = 0;
    }

    public boolean dispose() {
        if (this.state != 0) {
            LocalRefRuntime.checkState((byte)this.state);
        }
        this.state = (byte)2;
        return this.value;
    }
}