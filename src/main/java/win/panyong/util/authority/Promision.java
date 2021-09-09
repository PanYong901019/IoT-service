package win.panyong.util.authority;

import win.panyong.util.authority.bean.PromisionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by pan on 2021/6/1 11:14 AM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Promision {
    PromisionType[] value() default {};
}
