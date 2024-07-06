import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.Files
import ImageDocGeneratorInKorean.*

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ImageDocGeneratorPerformanceTest extends AnyFunSuite {

  test("generateDocument should complete within a reasonable time for many images") {
    val tempDir = Files.createTempDirectory("perf_test_images").toFile
    val outputFile = new File(tempDir, "perf_output.docx")

    // Create many test images
    for (i <- 1 to 100) {
      val img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
      val file = new File(tempDir, s"perf_image_$i.png")
      ImageIO.write(img, "png", file)
    }

    val startTime = System.currentTimeMillis()
    generateDocument(tempDir.getAbsolutePath, outputFile.getAbsolutePath, "Performance Test Document")
    val endTime = System.currentTimeMillis()

    val duration = endTime - startTime
    assert(duration < 30000, s"Document generation took too long: $duration ms")

    tempDir.delete()
  }
}