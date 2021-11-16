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

            classDeclaration.primaryConstructor!!.accept(this, data)

            //包名
            val packageName = classDeclaration.containingFile!!.packageName.asString()

            //File
            val fileSpec = FileSpec.builder(packageName, "${classDeclaration.simpleName.asString()}_Binder")
            //Class
            val typeSpec = TypeSpec.classBuilder("${classDeclaration.simpleName.asString()}_Binder")

            val activityClass = ClassName(packageName, classDeclaration.simpleName.asString())
            //function
            val functionSpec = FunSpec.builder("bindView").addParameter("activity", activityClass)
                .addAnnotation(JvmStatic::class.java)

            val companion = TypeSpec.companionObjectBuilder()


            classDeclaration.getAllProperties().forEach { property ->

                property.annotations.forEach {
                    if ( it.shortName.asString() == "findView"){
                        functionSpec.addStatement("activity.${property.simpleName.asString()} = activity.findViewById(${it.arguments[0].value})")
                    }
                }

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

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {


        }


        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            super.visitPropertyDeclaration(property, data)
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

