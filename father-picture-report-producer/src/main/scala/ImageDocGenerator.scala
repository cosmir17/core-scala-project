import org.apache.logging.log4j.{LogManager, Logger}
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, FileOutputStream}
import javax.imageio.ImageIO
import javax.swing.SwingWorker
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*
import scala.math.Integral.Implicits.*
import scala.swing.*
import scala.swing.event.ButtonClicked
import scala.util.Try

object ImageDocGenerator extends SimpleSwingApplication:
  private val logger: Logger = LogManager.getLogger(getClass)

  // Define valid image extensions
  private val validImageExtensions = Set("jpg", "jpeg", "png", "gif, bmp")
  
  def top: Frame = new MainFrame with ReactiveControls

  trait ReactiveControls extends MainFrame:
    title = "Image Document Generator"
    preferredSize = new Dimension(400, 300)

    val folderPathLabel = new Label("Select folder containing images:")
    val folderPathTextField = new TextField(20)
    val browseButton = new Button("Browse")
    val titleLabel = new Label("Document Title:")
    val titleTextField = new TextField(20)
    val outputFileLabel = new Label("Output File Name:")
    val outputFileTextField = new TextField("output.docx")
    val generateButton = new Button("Generate Document")

    listenTo(browseButton)
    listenTo(generateButton)

    reactions += {
      case ButtonClicked(`browseButton`) =>
        val fileChooser = new FileChooser(new File("."))
        fileChooser.title = "Select Folder"
        fileChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
        val result = fileChooser.showOpenDialog(null)
        if result == FileChooser.Result.Approve then
          val selectedFolder = fileChooser.selectedFile
          folderPathTextField.text = selectedFolder.getAbsolutePath

      case ButtonClicked(`generateButton`) =>
        val folderPath = folderPathTextField.text
        val docTitle = titleTextField.text
        val initialOutputFile = outputFileTextField.text
        if folderPath.nonEmpty && docTitle.nonEmpty && initialOutputFile.nonEmpty then
          getUniqueFileName(initialOutputFile) match
            case Some(uniqueOutputFile) =>
              disableControls()
              showProgressDialog(folderPath, uniqueOutputFile, docTitle)
            case None =>
              Dialog.showMessage(contents.head, "Document generation cancelled.", title = "Cancelled")
        else
          Dialog.showMessage(contents.head, "Please fill in all fields.", title = "Error")
    }

    contents = new BoxPanel(Orientation.Vertical):
      contents += folderPathLabel
      contents += new FlowPanel:
        contents += folderPathTextField
        contents += Swing.HStrut(5)
        contents += browseButton
      contents += Swing.VStrut(10)
      contents += titleLabel
      contents += titleTextField
      contents += Swing.VStrut(10)
      contents += outputFileLabel
      contents += outputFileTextField
      contents += Swing.VStrut(10)
      contents += generateButton

    def disableControls(): Unit =
      List(folderPathTextField, browseButton, titleTextField, outputFileTextField, generateButton).foreach(_.enabled = false)

    def enableControls(): Unit =
      List(folderPathTextField, browseButton, titleTextField, outputFileTextField, generateButton).foreach(_.enabled = true)

    @tailrec
    final def getUniqueFileName(fileName: String): Option[String] =
      if !new File(fileName).exists() then
        Some(fileName)
      else
        Dialog.showInput(null, "File already exists. Enter a new file name:", initial = fileName) match
          case Some(newFileName) if newFileName.nonEmpty => getUniqueFileName(newFileName)
          case _ => None

    def showProgressDialog(folderPath: String, outputFile: String, docTitle: String): Unit =
      val progressDialog = new Dialog:
        title = "Generating Document"
        modal = true
        val progressBar = new ProgressBar
        progressBar.indeterminate = true
        contents = new BoxPanel(Orientation.Vertical):
          contents += new Label("Generating document, please wait...")
          contents += Swing.VStrut(10)
          contents += progressBar

      val worker = new SwingWorker[Unit, Unit]:
        override def doInBackground(): Unit =
          generateDocument(folderPath, outputFile, docTitle)

        override def done(): Unit =
          progressDialog.close()
          Dialog.showMessage(contents.head, "Document generated successfully!", title = "Success")
          enableControls()

      worker.execute()
      progressDialog.centerOnScreen()
      progressDialog.open()

  def generateDocument(folderPath: String, outputFile: String, docTitle: String): Unit =
    logger.info(s"Started Document generation")
    val document = new XWPFDocument()
    val paragraph = document.createParagraph()
    paragraph.setAlignment(ParagraphAlignment.CENTER)
    val run = paragraph.createRun()
    run.setText(docTitle)
    run.setFontSize(16)
    run.setBold(true)

    // Set page orientation to landscape and reduce margins
    val ctDocument = document.getDocument
    val ctBody = ctDocument.getBody
    val pgSz = ctBody.addNewSectPr().addNewPgSz()
    pgSz.setOrient(STPageOrientation.LANDSCAPE)
    pgSz.setW(java.math.BigInteger.valueOf(842 * 20))
    pgSz.setH(java.math.BigInteger.valueOf(595 * 20))

    // Reduce margins (values in twentieths of a point)
    val margin = 0.3 * 1440 // 0.3 inch in twentieths of a point
    val ctMargin = ctBody.addNewSectPr().addNewPgMar()
    ctMargin.setLeft(java.math.BigInteger.valueOf(margin.toLong))
    ctMargin.setRight(java.math.BigInteger.valueOf(margin.toLong))
    ctMargin.setTop(java.math.BigInteger.valueOf(margin.toLong))
    ctMargin.setBottom(java.math.BigInteger.valueOf(margin.toLong))

    val imageFolder = new File(folderPath)
    logger.info(s"Accessing image folder: ${imageFolder.getAbsolutePath}")
    if (!imageFolder.exists() || !imageFolder.isDirectory) {
      throw new IllegalArgumentException(s"Invalid folder path: ${imageFolder.getAbsolutePath}")
    }
    
    val imageFiles = imageFolder.listFiles()
      .filter(file =>
        !file.isHidden &&
          file.isFile &&
          validImageExtensions.contains(getFileExtension(file.getName).toLowerCase)
      )
      .sorted
      .toList


    for (chunk <- imageFiles.grouped(6)) do
      val (quotient, remainder) = chunk.size /% 3
      val table = (chunk.size, quotient, remainder) match
        case (6, _, _) => document.createTable(4, 3)
        case (_, quotient, 0) => document.createTable(quotient * 2, 3)
        case (_, quotient, _) => document.createTable((quotient + 1) * 2, 3)

      table.setWidth("100%")

      for ((imageFile, index) <- chunk.zipWithIndex) do
        val imageRow = index / 3 * 2
        val col = index % 3

        val rowCount = table.getNumberOfRows
        val rowPossibility = (imageRow + 1) <= rowCount

        val imageCellTry = for
          rowQueried <- Try ( table.getRow(imageRow) )
          cell <- Try ( rowQueried.getCell(col) )
        yield cell

        val imageCell = imageCellTry.toEither match
          case Left(throwable) =>
            logger.error(s"Document generation failed while producing a table. Error: ${throwable.getMessage}")
            throw throwable
          case Right(cell) => cell

        imageCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)
        val commentCell = table.getRow(imageRow + 1).getCell(col)
        commentCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER)

        while (imageCell.getParagraphs.size() > 0) do
          imageCell.removeParagraph(0)
        while (commentCell.getParagraphs.size() > 0) do
          commentCell.removeParagraph(0)

        val imageName = imageFile.getName
        val description = imageName.substring(0, imageName.lastIndexOf("."))

        // Add image
        val imageParagraph = imageCell.addParagraph()
        imageParagraph.setSpacingBefore(0)
        imageParagraph.setSpacingAfter(0)
        imageParagraph.setAlignment(ParagraphAlignment.CENTER)
        imageParagraph.setVerticalAlignment(TextAlignment.CENTER)
        val imageRun = imageParagraph.createRun()
        val img = ImageIO.read(imageFile)
        val (scaledWidth, scaledHeight) = calculateScaledDimensions(img.getWidth, img.getHeight, 250, 180)
        imageRun.addPicture(convertBufferedImageToInputStream(img), Document.PICTURE_TYPE_JPEG, imageName, Units.toEMU(scaledWidth), Units.toEMU(scaledHeight))

        imageRun.setVerticalAlignment("CENTRE")

        // Add comment
        val commentParagraph = commentCell.addParagraph()
        commentParagraph.setAlignment(ParagraphAlignment.CENTER)
        commentParagraph.setVerticalAlignment(TextAlignment.CENTER)
        commentParagraph.setSpacingBefore(0)
        commentParagraph.setSpacingAfter(0)
        val commentRun = commentParagraph.createRun()
        commentRun.setText(description)
        commentRun.setFontSize(8)
        commentRun.setVerticalAlignment("CENTER")

    val out = new FileOutputStream(outputFile)
    document.write(out)
    out.close()
    document.close()
    logger.info(s"Document generated: $outputFile")

  private def calculateScaledDimensions(width: Int, height: Int, maxWidth: Int, maxHeight: Int): (Int, Int) =
    val aspectRatio = width.toDouble / height
    if width <= maxWidth && height <= maxHeight then
      (width, height)
    else if aspectRatio > maxWidth.toDouble / maxHeight then
      (maxWidth, (maxWidth / aspectRatio).toInt)
    else
      ((maxHeight * aspectRatio).toInt, maxHeight)

  private def scaleImage(img: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage =
    val scaledImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
    val g = scaledImg.createGraphics()
    g.drawImage(img, 0, 0, targetWidth, targetHeight, null)
    g.dispose()
    scaledImg

  private def convertBufferedImageToInputStream(image: BufferedImage): ByteArrayInputStream = {
    logger.info(s"Started converting an image")
    val outputStream = new ByteArrayOutputStream()
    ImageIO.write(image, "jpg", outputStream)
    val byteArray = outputStream.toByteArray
    new ByteArrayInputStream(byteArray)
  }

  def getFileExtension(fileName: String): String =
    if (fileName.startsWith(".") && fileName.indexOfSlice(".", 1) == -1) {
      // Hidden file with no extension
      fileName.drop(1)
    } else {
      fileName.lastIndexOf('.') match {
        case i if i > 0 && i < fileName.length - 1 => fileName.drop(i + 1)
        case _ => ""
      }
    }.toLowerCase