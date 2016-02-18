package com.xiaobukuaipao.youngmam.filter;

import com.xiaobukuaipao.youngmam.filter.util.GPUImageFilterTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-9-18.
 */
public class EffectService {
    private static EffectService mInstance;

    public static EffectService getInstance() {
        if (mInstance == null) {
            synchronized (EffectService.class) {
                if (mInstance == null) {
                    mInstance = new EffectService();
                }
            }
        }
        return mInstance;
    }

    private EffectService() {

    }

    public List<FilterEffect> getLocalFilters() {
        List<FilterEffect> filters = new ArrayList<FilterEffect>();

        filters.add(new FilterEffect("原图", GPUImageFilterTools.FilterType.NORMAL, 0));
        filters.add(new FilterEffect("芙蓉", GPUImageFilterTools.FilterType.ACV_AIMEI, 0));
        filters.add(new FilterEffect("夏时", GPUImageFilterTools.FilterType.ACV_DANLAN, 0));
        filters.add(new FilterEffect("暮色", GPUImageFilterTools.FilterType.ACV_DANHUANG, 0));
        filters.add(new FilterEffect("流年", GPUImageFilterTools.FilterType.ACV_FUGU, 0));
        filters.add(new FilterEffect("烟雨", GPUImageFilterTools.FilterType.ACV_GAOLENG, 0));
        filters.add(new FilterEffect("秋水", GPUImageFilterTools.FilterType.ACV_HUAIJIU, 0));
        filters.add(new FilterEffect("空灵", GPUImageFilterTools.FilterType.ACV_JIAOPIAN, 0));
        filters.add(new FilterEffect("花晨", GPUImageFilterTools.FilterType.ACV_KEAI, 0));
        filters.add(new FilterEffect("夕颜", GPUImageFilterTools.FilterType.ACV_LOMO, 0));
        filters.add(new FilterEffect("晓风", GPUImageFilterTools.FilterType.ACV_MORENJIAQIANG, 0));
        filters.add(new FilterEffect("光年", GPUImageFilterTools.FilterType.ACV_NUANXIN, 0));
        filters.add(new FilterEffect("未央", GPUImageFilterTools.FilterType.ACV_QINGXIN, 0));
        filters.add(new FilterEffect("风和", GPUImageFilterTools.FilterType.ACV_RIXI, 0));
        filters.add(new FilterEffect("春暖", GPUImageFilterTools.FilterType.ACV_WENNUAN, 0));

        return filters;
    }
}
