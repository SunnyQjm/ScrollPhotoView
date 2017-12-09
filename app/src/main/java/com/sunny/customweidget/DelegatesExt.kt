package com.sunny.customweidget

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by sunny on 17-11-16.
 */
object DelegatesExt{
    
    fun <T> notNullSingleValue(): ReadWriteProperty<Any?, T> = NotNullSingleValueVar()
}

/**
 * 自定义非空的委托类
 * 只能被赋值一次，第二次赋值会抛出异常
 */
private class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T>{
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            value ?: throw IllegalStateException("${property.name} not initialized")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value =
                if(this.value == null) value
                else throw IllegalStateException("${property.name} already initialized")
    }

}