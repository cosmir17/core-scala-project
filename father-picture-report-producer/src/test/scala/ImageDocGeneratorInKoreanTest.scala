import org.scalatest.funsuite.AnyFunSuite
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import ImageDocGeneratorInKorean._

class ImageDocGeneratorInKoreanTest extends AnyFunSuite {

    test("calculateScaledDimensions should maintain aspect ratio") {
        assert(calculateScaledDimensions(100, 100, 50, 50) == (50, 50))
        assert(calculateScaledDimensions(200, 100, 100, 100) == (100, 50))
        assert(calculateScaledDimensions(100, 200, 100, 100) == (50, 100))
    }

    test("calculateScaledDimensions should not upscale images") {
        assert(calculateScaledDimensions(50, 50, 100, 100) == (50, 50))
    }

    test("scaleImage should create image with correct dimensions") {
        val original = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
        val scaled = scaleImage(original, 50, 50)
        assert(scaled.getWidth == 50)
        assert(scaled.getHeight == 50)
    }

    test("convertBufferedImageToInputStream should produce valid image data") {
        val original = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
        val inputStream = convertBufferedImageToInputStream(original)
        val image = ImageIO.read(inputStream)
        assert(image != null)
        assert(image.getWidth == 100)
        assert(image.getHeight == 100)
    }

    test("getFileExtension should return correct extension") {
        assert(getFileExtension("file.txt") == "txt")
        assert(getFileExtension("image.jpg") == "jpg")
        assert(getFileExtension("document.pdf") == "pdf")
        assert(getFileExtension("no_extension") == "")
        assert(getFileExtension(".hidden_file") == "hidden_file")
        assert(getFileExtension(".gitignore") == "gitignore")
        assert(getFileExtension(".hidden.file") == "file")
        assert(getFileExtension("multiple.dots.in.name.xlsx") == "xlsx")
        assert(getFileExtension("") == "")
        assert(getFileExtension(".") == "")
        assert(getFileExtension("..") == "")
        assert(getFileExtension(".hidden.") == "")
        assert(getFileExtension(".hidden.file.txt") == "txt")
    }
}