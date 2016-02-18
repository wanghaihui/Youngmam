package com.xiaobukuaipao.youngmam.annotation;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by xiaobu1 on 15-5-12.
 * 利用注解实现View初始化和事件绑定
 */
public class ViewUtils {

    /**
     * 注解View
     */
    public static void inject(Activity activity) {
        inject(activity, new ViewFinder(activity));
    }

    /**
     * 注解View事件
     * @param classObj
     * @param contentView
     */
    public static void inject(Object classObj, View contentView) {
        inject(classObj, new ViewFinder(contentView));
    }

    private static void inject(Object classObj, ViewFinder finder) {
        try {
            // 注解Views
            injectViews(classObj, finder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void injectViews(Object classObj, ViewFinder viewFinder) throws IllegalAccessException, IllegalArgumentException {
        Class<?> clazz = classObj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 应该是说方法里的参数必须是Annotation的子类
            if (field.isAnnotationPresent(ViewInject.class)) {
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                int id = viewInject.value();
                if (id > 0) {
                    field.setAccessible(true);

                    View view = viewFinder.findViewById(id);

                    if (view != null) {
                        try {
                            field.set(classObj, view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

}
