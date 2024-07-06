import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.Files
import org.apache.poi.xwpf.usermodel.XWPFDocument
import ImageDocGeneratorInKorean.*

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ImageDocGeneratorIntegrationTest extends AnyFunSuite {

  test("generateDocument should create a valid document with images") {
    val tempDir = Files.createTempDirectory("test_images").toFile
    val outputFile = new File(tempDir, "output.docx")

    // Create some test images
    for (i <- 1 to 3) {
      val img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
      val file = new File(tempDir, s"test_image_$i.png")
      ImageIO.write(img, "png", file)
    }

    generateDocument(tempDir.getAbsolutePath, outputFile.getAbsolutePath, "Test Document")

    assert(outputFile.exists(), "Output file should be created")

    val doc = new XWPFDocument(Files.newInputStream(outputFile.toPath))
    assert(doc.getTables.size > 0, "Document should contain at least one table")
    assert(doc.getTables.get(0).getNumberOfRows == 2, "Document should have two rows for three pictures")

    doc.close()
    println(tempDir.getAbsolutePath)
    tempDir.delete()
  }
}