package cx.ath.fortytwo.reproducer


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

class MyPlugin implements Plugin<Project> {
    void apply(Project project) {

        //The path of the generated source main files
        def generatedSrcFileDir = project.buildDir.absolutePath+'/generated/sources/annotationProcessor/java/main'
        //The target directory where we need the integration tests to land
        def generatedTestSrcFileDir = project.buildDir.absolutePath+'/generated/sources/annotationProcessor/java/test'
        //The suffix of the generated src file that will be used as regex to find the integration test runner files*/
        def testRunnerSrcFileSuffix = "**/*TestRunner.java"

        project.tasks.named('compileJava') {
            finalizedBy("installDist")
            doLast {
                def generatedSource = new File(new File(generatedSrcFileDir), 'cx/ath/fortytwo/reproducer/generated/MyTestRunner.java')
                generatedSource.parentFile.mkdirs()
                generatedSource.text = """ 
                    package cx.ath.fortytwo.reproducer.generated;
                    
                    public class MyTestRunner {}
                """

            }
        }

        def copyTestSrcFiles = project.tasks.create('copyTestSrcFiles', Copy.class) {
            from  generatedSrcFileDir
            include testRunnerSrcFileSuffix
            into    generatedTestSrcFileDir
        }

        def deleteTestSrcFilesInMain = project.tasks.create('deleteTestSrcFiles',  Delete.class) {
            delete project.fileTree(generatedSrcFileDir).matching {
                include testRunnerSrcFileSuffix
            }
        }

        //The path of the generated source class files
        def generatedSrcClassFileDir = project.buildDir.absolutePath+'/classes/java/main'
        //The target directory where we need the integration test classes to land
        def generatedTestClassDir = project.buildDir.absolutePath+'/classes/java/test'
        //The suffix of the generated classes that will be used as regex to find the integration test runner files*/
        def testRunnerClassesFileSuffix = "**/*TestRunner.class"

        //A task that is defined to copy the runner files into the integration test directory
        def copyTestClasses = project.tasks.create('copyTestClasses', Copy.class) {
            from  generatedSrcClassFileDir
            include testRunnerClassesFileSuffix
            into    generatedTestClassDir
        }

        //A task that deletes the runner files from the generated main source file directory
        def deleteTestClassesInMain = project.tasks.create('deleteTestClassesInMain',  Delete.class) {
            delete project.fileTree(generatedSrcClassFileDir).matching {
                include testRunnerClassesFileSuffix
            }
        }

        //These tasks are executed only when the source files are produced
        project.tasks.getByName('classes').finalizedBy  copyTestSrcFiles
        copyTestSrcFiles.finalizedBy deleteTestSrcFilesInMain


        //These tasks are executed only when the classes are produced
        deleteTestSrcFilesInMain.finalizedBy  copyTestClasses
        copyTestClasses.finalizedBy deleteTestClassesInMain


        project.tasks.register("installDist") {
            dependsOn("jar")
        }
    }
}