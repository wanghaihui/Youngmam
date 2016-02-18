package com.xiaobukuaipao.youngmam.filter.easing;

/**
 * Created by xiaobu1 on 15-9-12.
 * 立方线--三次方
 */
public class Cubic implements Easing {

    /**
     * 减速
     * @param time
     * @param start
     * @param end
     * @param duration
     */
    @Override
    public double easeOut(double time, double start, double end, double duration) {
        time = time / duration - 1.0;
        return end * (time * time * time + 1) + start;
    }

    /**
     *  加速
     * @param time
     * @param start
     * @param end
     * @param duration
     */
    @Override
    public double easeIn(double time, double start, double end, double duration) {
        time /= duration;
        return end * time * time * time + start;
    }

    /**
     * 先加速后减速
     * @param time
     * @param start
     * @param end
     * @param duration
     */
    @Override
    public double easeInOut(double time, double start, double end, double duration) {
        time /= duration / 2;
        if (time < 1) {
            return end / 2 * time * time * time + start;
        }

        time -= 2;
        return end / 2 * (time * time * time + 2) + start;
    }
}
