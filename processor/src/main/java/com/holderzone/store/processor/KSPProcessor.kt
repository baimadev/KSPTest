package com.holderzone.store.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.holderzone.store.annotation.Route
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class BuilderProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {

    var functionSpec : FunSpec.Builder? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {

        //获取所有带注释的symbol（类、方法、属性。。。）
        val symbols = resolver.getSymbolsWithAnnotation(Route::class.java.name)
        val ret = symbols.filter { !it.validate() }.toList()

        // HashMap<String, Class<out Activity>>
        val activity = ClassName("android.app", "Activity")
        val hashMap = ClassName("java.util", "HashMap")
        val classK = ClassName("java.lang", "Class")
        val stringK = ClassName("kotlin", "String")
        val classActivity = classK.parameterizedBy(WildcardTypeName.producerOf(activity))
        val hashMapSC = hashMap.parameterizedBy(stringK,classActivity)

        functionSpec = FunSpec
            .builder("loadInfo")
            .addParameter(ParameterSpec.builder("map", hashMapSC).build())

        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach {
                it as KSClassDeclaration
                val activityClass = ClassName(it.getPackageName(), it.simpleName.asString())
                it.annotations.forEach {
                    //logger.error(it.arguments[0].toString())
                    val resValue = it.arguments.find{ it.name!!.asString() == "route" }!!.value
                    val string1 = "map[\"${resValue}\"]"
                    val string2 = " = ${activityClass}::class.java"
                    functionSpec?.addStatement(string1+string2)

                }
            }
            //.forEach { it.accept(BuilderVisitor(), Unit) }
        return ret
    }


    override fun finish() {
        super.finish()
        val funSpec = createFile("com.example.xrouter", functionSpec!!)
        writeFile(funSpec)
    }

    inner class BuilderVisitor : KSVisitorVoid() {
        @KspExperimental
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

            val activityClass = ClassName(classDeclaration.getPackageName(), classDeclaration.simpleName.asString())
            classDeclaration.annotations.forEach {
                if (it.shortName.asString() == "Route") {
                    val resValue = it.arguments.find{ it.name!!.asString() == "route" }!!.value
                    val string1 = "map[\"${resValue}\"]"
                    val string2 = " = ${activityClass}::class.java"
                    functionSpec?.addStatement(string1+string2)
                }
            }

        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

        }


        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            super.visitPropertyDeclaration(property, data)
        }
    }

    fun createFile(packageName:String, funSpec: FunSpec.Builder): FileSpec {
        //File
        val fileSpec = FileSpec.builder(packageName, "XRouterPathCollector")
        //Class
        val typeSpec = TypeSpec.classBuilder("XRouterPathCollector")
        //Companion
        //val companion = TypeSpec.companionObjectBuilder()

//        companion.addFunction(funSpec.build())
//        typeSpec.addType(companion.build())
        typeSpec.addFunction(funSpec.build())
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
