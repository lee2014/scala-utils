package office.word

import org.docx4j.Docx4J
import org.docx4j.convert.out.FOSettings
import org.docx4j.fonts.{IdentityPlusMapper, PhysicalFonts}
import org.docx4j.model.fields.FieldUpdater
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * Created by chengli on 14/06/2017.
  */
class ConvertOutPDFTest extends FunSuite with BeforeAndAfter {
  val inputfilepath = "/Users/chengli/Downloads/lee_resume.docx"
  val saveFO = true
  var wordMLPackage: WordprocessingMLPackage = _

  var regex: String = ".*(Courier New|Arial|" +
    "Times New Roman|Comic Sans|Georgia|Impact|" +
    "Lucida Console|Lucida Sans Unicode|Palatino Linotype|" +
    "Tahoma|Trebuchet|Verdana|Symbol|Webdings|Wingdings|MS Sans Serif|MS Serif).*"

  test("convert word to pdf") {
    PhysicalFonts.setRegex(regex)

    if (inputfilepath == null) {
      // Create a docx
      System.out.println("No imput path passed, creating dummy document")
      wordMLPackage = WordprocessingMLPackage.createPackage()
      SampleDocument.createContent(wordMLPackage.getMainDocumentPart)
    } else {
      // Load .docx or Flat OPC .xml
      System.out.println("Loading file from " + inputfilepath)
      wordMLPackage = WordprocessingMLPackage.load(new java.io.File(inputfilepath))
    }

    // Refresh the values of DOCPROPERTY fields
    var updater = new FieldUpdater(wordMLPackage)
    updater.update(true)

    var outputfilepath: String = null
    if (inputfilepath == null) {
      outputfilepath = System.getProperty("user.dir") + "/OUT_FontContent.pdf"
    } else {
      outputfilepath = inputfilepath + ".pdf"
    }

    // All methods write to an output stream
    val os = new java.io.FileOutputStream(outputfilepath)


    if (!Docx4J.pdfViaFO()) {
      System.out.println("Using Plutext's PDF Converter add docx4j-export-fo if you don't want that")

      // Docx4jProperties.setProperty("com.plutext.converter.URL", "http://localhost:80/v1/00000000-0000-0000-0000-000000000000/convert")
      Docx4J.toPDF(wordMLPackage, os)
      System.out.println("Saved: " + outputfilepath)

      System.exit(-1)
    }

    System.out.println("Attempting to use XSL FO")

    // Set up font mapper (optional)
    val fontMapper = new IdentityPlusMapper()
    wordMLPackage.setFontMapper(fontMapper)

    val font = PhysicalFonts.get("Arial Unicode MS")

    var foSettings: FOSettings = Docx4J.createFOSettings()
    if (saveFO) {
      foSettings.setFoDumpFile(new java.io.File(inputfilepath + ".fo"))
    }
    foSettings.setWmlPackage(wordMLPackage)
    Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL)

    System.out.println("Saved: " + outputfilepath)

    // Clean up, so any ObfuscatedFontPart temp files can be deleted
    if (wordMLPackage.getMainDocumentPart.getFontTablePart!=null) {
      wordMLPackage.getMainDocumentPart.getFontTablePart.deleteEmbeddedFontTempFiles()
    }
    // This would also do it, via finalize() methods
    updater = null
    foSettings = null
    wordMLPackage = null
  }
}
