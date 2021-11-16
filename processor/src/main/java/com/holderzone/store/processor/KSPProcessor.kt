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

            val activityClass = ClassName(classDeclaration.getPackageName(), classDeclaration.simpleName.asString())

            //function
            val functionSpec = FunSpec.builder("bindView").addParameter("activity", activityClass)
                .addAnnotation(JvmStatic::class.java)

            classDeclaration.getAllProperties().forEach { property ->
                property.annotations.forEach {
                    if (it.shortName.asString() == "findView") {
                        functionSpec.addStatement("activity.${property.simpleName.asString()} = activity.findViewById(${it.arguments[0].value})")
                    }
                }
            }

            writeFile(createFile(classDeclaration, functionSpec))
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

        }


        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            super.visitPropertyDeclaration(property, data)
        }
    }

    fun createFile(classDeclaration: KSClassDeclaration, funSpec: FunSpec.Builder): FileSpec {

        //File
        val fileSpec = FileSpec.builder(classDeclaration.getPackageName(), "${classDeclaration.simpleName.asString()}_Binder")
        //Class
        val typeSpec = TypeSpec.classBuilder("${classDeclaration.simpleName.asString()}_Binder")
        //Companion
        val companion = TypeSpec.companionObjectBuilder()

        companion.addFunction(funSpec.build())
        typeSpec.addType(companion.build())
        fileSpec.addType(typeSpec.build())
        return fileSpec.build()
    }

    fun writeFile(fileSpec: FileSpec) {
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            fileSpec.packageName,
            fileSpec.name
        )
        file.use {
            val content = fileSpec.toString().toByteArray()
            it.write(content)
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

fun KSClassDeclaration.getPackageName():String = this.containingFile!!.packageName.asString()
