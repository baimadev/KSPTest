package com.holderzone.store.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.holderzone.store.annotation.MyClass
import com.holderzone.store.annotation.findView
import com.squareup.kotlinpoet.*

class BuilderProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        //获取所有带注释的symbol（类、方法、属性。。。）
        val symbols = resolver.getSymbolsWithAnnotation(MyClass::class.java.name)
        val ret = symbols.filter { !it.validate() }.toList()

        symbols
            //过滤出类
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(BuilderVisitor(), Unit) }
        return ret
    }


    inner class BuilderVisitor : KSVisitorVoid() {
        @KspExperimental
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

            //classDeclaration.primaryConstructor!!.accept(this, data)

            classDeclaration.getAllProperties().forEach {
                //相当于回调visitPropertyDeclaration
                it.accept(this, data)
            }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {


        }

        @KspExperimental
        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            super.visitPropertyDeclaration(property, data)

            val parent = property.parentDeclaration as KSClassDeclaration
            //包名
            val packageName = parent.containingFile!!.packageName.asString()

            //File
            val fileSpec = FileSpec.builder(packageName, "${parent.simpleName.asString()}_Binder")
            //Class
            val typeSpec = TypeSpec.classBuilder("${parent.simpleName.asString()}_Binder")

            val activityClass = ClassName(packageName, parent.simpleName.asString())
            //function
            val functionSpec = FunSpec.builder("bindView").addParameter("activity", activityClass)
                .addAnnotation(JvmStatic::class.java)

            val companion = TypeSpec.companionObjectBuilder()



            property.getAnnotationsByType(findView::class).forEach {
                functionSpec.addStatement("activity.${property.simpleName.asString()} = activity.findViewById(${it.value})")
            }


            companion.addFunction(functionSpec.build())
            typeSpec.addType(companion.build())
            fileSpec.addType(typeSpec.build())


            val file = codeGenerator.createNewFile(
                Dependencies.ALL_FILES,
                fileSpec.packageName,
                fileSpec.name
            )
            file.use {
                val content = fileSpec.build().toString().toByteArray()
                it.write(content)
            }
        }
    }


}

class BuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return BuilderProcessor(environment.codeGenerator, environment.logger)
    }
}

