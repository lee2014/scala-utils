package office.word

import javax.xml.bind.JAXBException

import org.docx4j.XmlUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.fonts.PhysicalFont
import org.docx4j.fonts.PhysicalFonts

/**
  * Created by chengli on 14/06/2017.
  */
object SampleDocument {

  def createContent(wordDocumentPart: MainDocumentPart) = {
    try {
      PhysicalFonts.discoverPhysicalFonts()

      val physicalFontMap: java.util.Map[String, PhysicalFont] = PhysicalFonts.getPhysicalFonts
      val physicalFontMapIterator = physicalFontMap.entrySet.iterator()

      while (physicalFontMapIterator.hasNext) {
        var pairs = physicalFontMapIterator.next()

        if(pairs.getKey == null) {
          pairs = physicalFontMapIterator.next
        }
        val fontName = pairs.getKey

        val pf = pairs.getValue

        System.out.println("Added paragraph for " + fontName)
        addObject(wordDocumentPart, sampleText, fontName)

        // bold, italic etc
        var pfVariation = PhysicalFonts.getBoldForm(pf)
        if (pfVariation != null) {
          addObject(wordDocumentPart, sampleTextBold, pfVariation.getName )
        }

        pfVariation = PhysicalFonts.getBoldItalicForm(pf)
        if (pfVariation != null) {
          addObject(wordDocumentPart, sampleTextBoldItalic, pfVariation.getName)
        }
        pfVariation = PhysicalFonts.getItalicForm(pf)

        if (pfVariation != null) {
          addObject(wordDocumentPart, sampleTextItalic, pfVariation.getName)
        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }

  }

  @throws(classOf[JAXBException])
  def addObject(wordDocumentPart: MainDocumentPart, template: String, fontName: String) =  {

    val substitution = new java.util.HashMap[String, String]()
    substitution.put("fontname", fontName)
    val obj = XmlUtils.unmarshallFromTemplate(template, substitution)
    wordDocumentPart.addObject(obj)

  }

  final val sampleText = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
    "<w:r>" +
    "<w:rPr>" +
    "<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />" +
    "</w:rPr>" +
    "<w:t xml:space=\"preserve\">${fontname}</w:t>" +
    "</w:r>" +
    "</w:p>"

  final val sampleTextBold =	"<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
    "<w:r>" +
    "<w:rPr>" +
    "<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />" +
    "<w:b />" +
    "</w:rPr>" +
    "<w:t>${fontname} bold</w:t>" +
    "</w:r>" +
    "</w:p>"

  final val sampleTextItalic =	"<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
    "<w:r>" +
    "<w:rPr>" +
    "<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />" +
    "<w:i />" +
    "</w:rPr>" +
    "<w:t>${fontname} italic </w:t>" +
    "</w:r>" +
    "</w:p>"

  final val sampleTextBoldItalic ="<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
    "<w:r>" +
    "<w:rPr>" +
    "<w:rFonts w:ascii=\"${fontname}\" w:eastAsia=\"${fontname}\" w:hAnsi=\"${fontname}\" w:cs=\"${fontname}\" />" +
    "<w:b />" +
    "<w:i />" +
    "</w:rPr>" +
    "<w:t>${fontname} bold italic</w:t>" +
    "</w:r>" +
    "</w:p>"
}
