package com.xiaobukuaipao.youngmam.filter.easing;

/**
 * Created by xiaobu1 on 15-9-12.
 * 过渡过程
 */
public interface Easing {
    double easeOut(double time, double start, double end, double duration);
    double easeIn(double time, double start, double end, double duration);
    double easeInOut(double time, double start, double end, double duration);
}
