package com.hayukleung.designpattern.AbstractFactory;

/**
 * 抽象工厂
 *
 * @author lyq
 */
public abstract class AbstractCreator {
    /**
     * 创建A产品族
     *
     * @return
     */
    public abstract AbstractProductA createProductA();

    /**
     * 创建B产品族
     *
     * @return
     */
    public abstract AbstractProductB createProductB();
}
