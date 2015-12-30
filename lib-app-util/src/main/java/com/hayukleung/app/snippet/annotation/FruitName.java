package com.hayukleung.app.snippet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FruitName.java
 * <p/>
 *
 * 元注解-meta-annotation
 * - 1.@Target 被描述的注解可以用在什么地方
 * -- 1.ElementType.CONSTRUCTOR:用于描述构造器
 * -- 2.ElementType.FIELD:用于描述域
 * -- 3.ElementType.LOCAL_VARIABLE:用于描述局部变量
 * -- 4.ElementType.METHOD:用于描述方法
 * -- 5.ElementType.PACKAGE:用于描述包
 * -- 6.ElementType.PARAMETER:用于描述参数
 * -- 7.ElementType.TYPE:用于描述类、接口(包括注解类型) 或enum声明
 * - 2.@Retention 被描述的注解在什么范围内有效
 * -- 1.RetentionPolicy.SOURCE:在源文件中有效（即源文件保留）
 * -- 2.RetentionPolicy.CLASS:在class文件中有效（即class保留）
 * -- 3.RetentionPolicy.RUNTIME:在运行时有效（即运行时保留）
 * - 3.@Documented 描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化。
 * - 4.@Inherited 阐述了某个被标注的类型是被继承的。如果一个使用了@Inherited修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类。
 *
 * 自定义注解
 * - 使用@interface自定义注解时，自动继承了java.lang.annotation.Annotation接口，由编译程序自动完成其他细节。在定义注解时，不能继承其他的注解或接口。@interface用来声明一个注解，其中的每一个方法实际上是声明了一个配置参数。方法的名称就是参数的名称，返回值类型就是参数的类型（返回值类型只能是基本类型、Class、String、enum）。可以通过default来声明参数的默认值。
 * - 定义注解格式：public @interface 注解名 {定义体}
 * - 注解参数的可支持数据类型：
 * -- 1.所有基本数据类型（int,float,boolean,byte,double,char,long,short)
 * -- 2.String类型
 * -- 3.Class类型
 * -- 4.enum类型
 * -- 5.Annotation类型
 * -- 6.以上所有类型的数组
 * - Annotation类型里面的参数该怎么设定:
 * -- 第一,只能用public或默认(default)这两个访问权修饰.例如,String value();这里把方法设为defaul默认类型；　 　
 * -- 第二,参数成员只能用基本类型byte,short,char,int,long,float,double,boolean八种基本数据类型和 String,Enum,Class,annotations等数据类型,以及这一些类型的数组.例如,String value();这里的参数成员就为String;　　
 * -- 第三,如果只有一个参数成员,最好把参数名称设为"value",后加小括号.例:下面的例子FruitName注解就只有一个参数成员。
 *
 *
 * Created by hayukleung on 12/25/15.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FruitName {
    String value() default "";
}
