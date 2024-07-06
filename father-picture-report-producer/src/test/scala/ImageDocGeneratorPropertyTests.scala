import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalacheck.Gen
import ImageDocGeneratorInKorean._

class ImageDocGeneratorPropertyTests extends AnyPropSpec with ScalaCheckDrivenPropertyChecks {

  property("calculateScaledDimensions should never exceed max dimensions") {
    forAll(Gen.posNum[Int], Gen.posNum[Int], Gen.posNum[Int], Gen.posNum[Int]) {
      (width: Int, height: Int, maxWidth: Int, maxHeight: Int) =>
        val (scaledWidth, scaledHeight) = calculateScaledDimensions(width, height, maxWidth, maxHeight)
        assert(scaledWidth <= maxWidth && scaledHeight <= maxHeight)
    }
  }

  property("getFileExtension should always return lowercase") {
    forAll(Gen.alphaNumStr, Gen.alphaNumStr) { (name: String, ext: String) =>
      whenever(name.nonEmpty && ext.nonEmpty) {
        val fileName = s"$name.$ext"
        assert(getFileExtension(fileName) == ext.toLowerCase)
      }
    }
  }
}