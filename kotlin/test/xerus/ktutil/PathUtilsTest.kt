@file:Suppress("BlockingMethodInNonBlockingContext")

package xerus.ktutil

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.nio.file.Files

class PathUtilsTest: StringSpec({
	val path = SystemUtils.tempDir.toPath().resolve("${this.javaClass.simpleName}-${System.currentTimeMillis()}")
	"create & delete directory $path" {
		path.exists() shouldBe false
		path.createDirs() shouldBe path
		path.createDirs() shouldBe path
		path.exists() shouldBe true
		path.toFile().exists() shouldBe true
		path.delete() shouldBe true
	}
	
	"create file $path" {
		path.exists() shouldBe false
		path.createFile()
		path.exists() shouldBe true
		path.toFile().exists() shouldBe true
	}
	
	val newPath = SystemUtils.tempDir.toPath().resolve("${this.javaClass.simpleName}-${System.currentTimeMillis()}-2")
	"rename $path to $newPath and delete $newPath" {
		path.renameTo(newPath)
		path.exists() shouldBe false
		path.delete() shouldBe false
		newPath.exists() shouldBe true
		newPath.delete() shouldBe true
		newPath.exists() shouldBe false
	}
	
	val testText = "this is a test"
	"create files in $path" {
		val testFile = path.resolve("test1").createDirs().resolve("testFile")
		Files.write(testFile, listOf(testText))
		Files.write(testFile.parent.resolve("test2").createDirs().resolve("testFile2"), listOf(testText))
		Files.isRegularFile(testFile) shouldBe true
		path.resolve("randomdir").createDir()
	}
	
	"recursively move $path to $newPath" {
		val testFile = newPath.resolve("test1").createDirs().resolve("testFile")
		Files.write(testFile, listOf("hi"))
		path.moveRecursively(newPath)
		Files.isDirectory(newPath.resolve("randomdir")) shouldBe false
		Files.readAllLines(testFile).first() shouldBe testText
		Files.readAllLines(testFile.parent.resolve("test2").resolve("testFile2")).first() shouldBe testText
		path.exists() shouldBe false
	}
	
	"recursively delete $newPath" {
		newPath.deleteRecursively()
		newPath.exists() shouldBe false
	}
})