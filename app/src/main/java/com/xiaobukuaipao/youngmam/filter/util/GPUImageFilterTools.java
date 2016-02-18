package com.xiaobukuaipao.youngmam.filter.util;

import android.content.Context;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.gpuimage.GPUImageFilter;
import com.xiaobukuaipao.youngmam.gpuimage.GPUImageToneCurveFilter;

/**
 * Created by xiaobu1 on 15-9-1.
 */
public class GPUImageFilterTools {

    public static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
        GPUImageToneCurveFilter curveFilter = new GPUImageToneCurveFilter();

        switch (type) {
            case NORMAL:
                return new GPUImageFilter();
            case ACV_AIMEI:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.aimei));
                return curveFilter;
            case ACV_DANLAN:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.danlan));
                return curveFilter;
            case ACV_DANHUANG:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.danhuang));
                return curveFilter;
            case ACV_FUGU:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.fugu));
                return curveFilter;
            case ACV_GAOLENG:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.gaoleng));
                return curveFilter;
            case ACV_HUAIJIU:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.huaijiu));
                return curveFilter;
            case ACV_JIAOPIAN:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.jiaopian));
                return curveFilter;
            case ACV_KEAI:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.keai));
                return curveFilter;
            case ACV_LOMO:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.lomo));
                return curveFilter;
            case ACV_MORENJIAQIANG:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.morenjiaqiang));
                return curveFilter;
            case ACV_NUANXIN:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.nuanxin));
                return curveFilter;
            case ACV_QINGXIN:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.qingxin));
                return curveFilter;
            case ACV_RIXI:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.rixi));
                return curveFilter;
            case ACV_WENNUAN:
                curveFilter.setFromCurveFileInputStream(context.getResources().openRawResource(
                        R.raw.wennuan));
                return curveFilter;
            default:
                throw new IllegalStateException("No filter of that type");
        }
    }

    public enum FilterType {
        NORMAL,
        ACV_AIMEI,
        ACV_DANLAN,
        ACV_DANHUANG,
        ACV_FUGU,
        ACV_GAOLENG,
        ACV_HUAIJIU,
        ACV_JIAOPIAN,
        ACV_KEAI,
        ACV_LOMO,
        ACV_MORENJIAQIANG,
        ACV_NUANXIN,
        ACV_QINGXIN,
        ACV_RIXI,
        ACV_WENNUAN
    }

    private abstract class Adjuster<T extends GPUImageFilter> {
        private T filter;

        @SuppressWarnings("unchecked")
        public Adjuster<T> filter(final GPUImageFilter filter) {
            this.filter = (T) filter;
            return this;
        }

        public T getFilter() {
            return filter;
        }

        public abstract void adjust(int percentage);

        protected float range(final int percentage, final float start, final float end) {
            return (end - start) * percentage / 100.0f + start;
        }

        protected int range(final int percentage, final int start, final int end) {
            return (end - start) * percentage / 100 + start;
        }
    }
}
