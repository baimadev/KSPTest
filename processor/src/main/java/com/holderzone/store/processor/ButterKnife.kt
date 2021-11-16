package com.holderzone.store.processor

class ButterKnife {
    companion object {
        fun bindView(target: Any) {
            val classs = target.javaClass
            val claName = classs.name + "_Binder"
            val clazz = Class.forName(claName)
            val ob = clazz.newInstance()
            val bindMethod = clazz.getMethod("bindView", target.javaClass)
            bindMethod.invoke(ob, target)
        }
    }
}