package cx.ath.fortytwo.reproducer


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.configurations {
            bootstrap
        }



        // Note: Since Gradle-5, annotation-processor dependencies should *only* be applied to '*compileOnly' and '*annotationProcessor' configs
//        def stratumProcessorDep = 'com.netflix.stratumagent:stratum-function-annotation-processor:latest.release'
//        project.dependencies.add('annotationProcessor', stratumProcessorDep)
//        project.dependencies.add('implementation', stratumProcessorDep)
//        project.dependencies.add('testImplementation', stratumProcessorDep)
//        project.dependencies.add('testAnnotationProcessor', stratumProcessorDep)
//
//        // used by the generated code
//        project.dependencies.add('implementation', 'com.netflix.stratumagent:stratum-function-proto-definition:latest.release')
//        project.dependencies.add('implementation', 'com.netflix.stratumagent:stratum-function-api:latest.release')
//        project.dependencies.add('implementation', 'com.google.inject:guice')
//        project.dependencies.add('implementation', 'com.google.inject.extensions:guice-assistedinject')

        //The path of the generated source main files
        def generatedSrcFileDir = project.buildDir.absolutePath+'/generated/sources/annotationProcessor/java/main'
        //The target directory where we need the integration tests to land
        def generatedIntegrationTestSrcFileDir = project.buildDir.absolutePath+'/generated/sources/annotationProcessor/java/integTest'
        //The suffix of the generated src file that will be used as regex to find the integration test runner files*/
        def integrationTestRunnerSrcFileSuffix = "**/*IntegrationTestRunner.java"

        project.tasks.named('compileJava') {
            finalizedBy("installDist")
            doLast {
                def generatedSource = new File(new File(generatedSrcFileDir), 'cx/ath/fortytwo/reproducer/generated/MyIntegrationTestRunner.java')
                generatedSource.parentFile.mkdirs()
                generatedSource.text = """ 
                    package cx.ath.fortytwo.reproducer.generated;
                    
                    public class MyIntegrationTestRunner {}
                """

            }
        }

        def copyIntegrationTestSrcFiles = project.tasks.create('copyIntegrationTestSrcFiles', Copy.class) {
            from  generatedSrcFileDir
            include integrationTestRunnerSrcFileSuffix
            into    generatedIntegrationTestSrcFileDir
        }

        def deleteIntegrationTestSrcFilesInMain = project.tasks.create('deleteIntegrationTestSrcFiles',  Delete.class) {
            delete project.fileTree(generatedSrcFileDir).matching {
                include integrationTestRunnerSrcFileSuffix
            }
        }

        //The path of the generated source class files
        def generatedSrcClassFileDir = project.buildDir.absolutePath+'/classes/java/main'
        //The target directory where we need the integration test classes to land
        def generatedIntegrationTestClassDir = project.buildDir.absolutePath+'/classes/java/integTest'
        //The suffix of the generated classes that will be used as regex to find the integration test runner files*/
        def integrationTestRunnerClassesFileSuffix = "**/*IntegrationTestRunner.class"

        //A task that is defined to copy the runner files into the integration test directory
        def copyIntegrationTestClasses = project.tasks.create('copyIntegrationTestClasses', Copy.class) {
            from  generatedSrcClassFileDir
            include integrationTestRunnerClassesFileSuffix
            into    generatedIntegrationTestClassDir
        }

        //A task that deletes the runner files from the generated main source file directory
        def deleteIntegrationTestClassesInMain = project.tasks.create('deleteIntegrationTestClassesInMain',  Delete.class) {
            delete project.fileTree(generatedSrcClassFileDir).matching {
                include integrationTestRunnerClassesFileSuffix
            }
        }

        //These tasks are executed only when the source files are produced
        project.tasks.getByName('classes').finalizedBy  copyIntegrationTestSrcFiles
        copyIntegrationTestSrcFiles.finalizedBy deleteIntegrationTestSrcFilesInMain


        //These tasks are executed only when the classes are produced
        deleteIntegrationTestSrcFilesInMain.finalizedBy  copyIntegrationTestClasses
        copyIntegrationTestClasses.finalizedBy deleteIntegrationTestClassesInMain


        project.tasks.register("installDist") {
            dependsOn("jar")
        }
    }
}