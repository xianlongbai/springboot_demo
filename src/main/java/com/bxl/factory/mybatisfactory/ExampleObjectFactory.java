package com.bxl.factory.mybatisfactory;

import com.bxl.entity.User;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by root on 2018/9/19.
 */
public class ExampleObjectFactory extends DefaultObjectFactory {

    /**
     * 在创建返回对象时调用
     * @param type
     * @return
     */
    @Override
    public Object create(Class type) {
        if (type.equals(User.class)){
            User p = (User)super.create(type);
            p.setAge(22);
            p.setName("keven");
            p.setPassword("123");
            return p;
        }
        return super.create(type);
    }

    /**
     * 在session创建时调用（也就是在加载config.xml）
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()){
            String keyValue = String.valueOf(iterator.next());
            System.out.println("结果生成工厂的参数："+properties.getProperty(keyValue));
        }
        super.setProperties(properties);
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
