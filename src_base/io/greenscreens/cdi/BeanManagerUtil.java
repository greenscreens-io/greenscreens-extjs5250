/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.cdi;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Singleton class used to find CDI bean and wraps it into destructible instance.
 * It is used as an internal bean finder.
 */
@Singleton
public class BeanManagerUtil {

    @Inject
    @Default
    private BeanManager beanManager;

    /**
     * Finds CDI bean by class type and defined qualifier annotations
     * @param type - class type implemented in CDI bean
     * @param qualifiers - additional bean qualifier
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T> IDestructibleBeanInstance<T> getDestructibleBeanInstance(final Class<T> type, final Annotation... qualifiers) {

        final Set<Bean<?>> beansF = new HashSet<Bean<?>>();
        final Set<Bean<?>> beans = beanManager.getBeans(Object.class, qualifiers);

        final Iterator<Bean<?>> iterator = beans.iterator();
        while (iterator.hasNext()) {
            final Bean<?> bean = iterator.next();

            if (type.isInterface()) {
                final Class<?>[] intfs = bean.getBeanClass().getInterfaces();
                for (final Class<?> intf : intfs) {
                    if (type.equals(intf)) {
                        beansF.add(bean);
                    }
                }
            } else {
                if (bean.getBeanClass().equals(type)) {
                    beansF.add(bean);
                }
            }
        }
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beansF);
        return getDestructibleBeanInstance(bean);
    }

    /**
     * Wraps CDI bean into custom destructible instance
     * @param bean
     * @return
     */
    public final <T> IDestructibleBeanInstance<T> getDestructibleBeanInstance(final Bean<T> bean) {
        IDestructibleBeanInstance<T> result = null;
        if (bean != null) {
            final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
            if (creationalContext != null) {
                final T instance = bean.create(creationalContext);
                result = new DestructibleBeanInstance<T>(instance, bean, creationalContext);
            }
        }
        return result;
    }

    /**
     * Returns internal Java CDI BeanManager
     * @return
     */
    public final BeanManager getBeanManager() {
        return beanManager;
    }

    /**
     * Set custom BeanManager
     * @param beanManager
     */
    public synchronized void setBeanManager(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public final String toString() {
        return "BeanManagerUtil [beanManager=" + beanManager + ", getBeanManager()=" + getBeanManager() + "]";
    }

}
