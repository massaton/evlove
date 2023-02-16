package org.evlove.common.core.pojo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.SerializeUtil;
import cn.hutool.json.JSONUtil;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Base class for POJOs
 *
 * Realize the basic capabilities of POJO classes such as cloning, comparison, and data structure conversion
 *
 * @author massaton.github.io
 */
@NoArgsConstructor
public class BasePojo implements Serializable, Cloneable {

    /**
     * Shadow copy (native)
     * The implementation of clone needs to be declared, otherwise CloneNotSupportedException will be thrown when calling clone() method.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    /**
     * Shadow copy (overload)
     * Use Generics to avoid type casting by the user.
     */
    @SuppressWarnings("unchecked")
    public <T> T cloneShadow() {
        Object cloneObj = this.clone();
        return (T) cloneObj;
    }
    /**
     * Deep copy
     * Cloned by copying the stream after serialization, the object must implement the Serializable interface.
     */
    @SuppressWarnings("unchecked")
    public <T> T cloneDeep() {
        Object cloneObj = SerializeUtil.clone(this);
        return (T) cloneObj;
    }


    /**
     * Compare the hash code of two objects is consistent
     *
     * NOTE: After using lombok's @Data annotations, equals() and hashCode() will be rewritten,
     *       and the ability is consistent with equalsContent().
     *       '@Data' is equivalent to @Getter, @Setter, @RequiredArgsConstructor, @ToString @EqualsAndHashCode these 5 annotations collection.
     */
    public boolean equalsHash(Object obj) {
        return this.hashCode() == obj.hashCode();
    }
    /**
     * Compare whether the data content of two objects is consistent, even if they are of different CLASS
     */
    public boolean equalsContent(Object obj) {
        if (obj == null) {
            return false;
        }

        // Compare whether the serialized BYTE arrays are the same
        byte[] current = SerializeUtil.serialize(this);
        byte[] target = SerializeUtil.serialize(obj);

        return Arrays.equals(current, target);
    }


    /**
     * Fill POJO with Map
     */
    public void fill(Map<String, Object> map) {
        BeanUtil.copyProperties(map, this);
    }
    /**
     * Fill POJO with JSON string
     */
    public void fill(String json) {
        BasePojo jsonBean = JSONUtil.toBean(json, this.getClass(), false);
        this.fill(jsonBean);
    }
    /**
     * Fill POJO with other POJO, when the Class is different, only fill in the attributes with the same name.
     * Support RO、PO、BO、VO objects to convert each other.
     *
     * Use Hutool's BeanUtil, which is the same effect as Spring's BeanUtils.copyProperties(), but is easier to use.
     */
    public void fill(Object sourcePojo, String... ignoreProperties) {
        BeanUtil.copyProperties(sourcePojo, this, ignoreProperties);
    }


    /**
     * Convert POJO to Map
     */
    public Map<String, Object> toMap() {
        return BeanUtil.beanToMap(this);
    }
    /**
     * Convert POJO to JSON string
     *
     * Fields with NULL values are removed by default, fields with EMPTY are not removed.
     */
    public String toJson() {
        return JSONUtil.toJsonStr(this);
    }
    /**
     * Convert current POJO to specified POJO, when the Class is different, only the same attributes are copied.
     * Support RO、PO、BO、VO objects to convert each other.
     *
     * Use Hutool's BeanUtil, which is the same effect as Spring's BeanUtils.copyProperties(), but is easier to use.
     */
    public <T> T toBean(Class<T> returnClass, String... ignoreProperties) {
        return BeanUtil.copyProperties(this, returnClass, ignoreProperties);
    }
}
