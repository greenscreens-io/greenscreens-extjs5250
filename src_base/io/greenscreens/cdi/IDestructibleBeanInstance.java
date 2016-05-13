/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.cdi;

import javax.enterprise.inject.spi.Bean;

public interface IDestructibleBeanInstance<T> {

    /**
     * Returns CDI bean instance
     * @return
     */
    T getInstance();

    /**
     * Returns CDI bean class type
     * @return
     */
    Class<?> getBeanClass();

    /**
     * Returns Bean found by BeanManager
     * @return
     */
    Bean<T> getBean();

    /**
     * Destroys injected bean instance
     */
    void release();

}