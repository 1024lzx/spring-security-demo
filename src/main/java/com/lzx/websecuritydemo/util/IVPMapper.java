package com.lzx.websecuritydemo.util;

public interface IVPMapper<V,P> {
    V po2vo(P p);
    P vo2po(V v);
}
