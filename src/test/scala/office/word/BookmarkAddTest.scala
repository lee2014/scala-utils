package office.word

import java.math.BigInteger

import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement

import org.docx4j.XmlUtils
import org.docx4j.jaxb.Context
import org.docx4j.openpackaging.io.SaveToZipFile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.CTBookmark
import org.docx4j.wml.CTMarkupRange
import org.docx4j.wml.P
import org.docx4j.wml.P.Hyperlink
import org.docx4j.wml.R

import org.scalatest.{BeforeAndAfterAll, FunSuite}

/**
  * Created by chengli on 14/06/2017.
  */
class BookmarkAddTest extends FunSuite with BeforeAndAfterAll {
  val context: JAXBContext = org.docx4j.jaxb.Context.jc

  test("test bookmark") {

    val wordMLPackage = WordprocessingMLPackage.createPackage()

    val outputfilepath = "/Users/chengli/Downloads/test.docx"

    wordMLPackage.getMainDocumentPart.addParagraphOfText("x")
    wordMLPackage.getMainDocumentPart.addParagraphOfText("x")
    wordMLPackage.getMainDocumentPart.addParagraphOfText("hello world")

    val p = wordMLPackage.getMainDocumentPart.getContent.get(2).asInstanceOf[P]
    val r = p.getContent.get(0).asInstanceOf[R]

    val bookmarkName = "abcd"
    bookmarkRun(p,r, bookmarkName, 123)

    wordMLPackage.getMainDocumentPart.addParagraphOfText("x")
    wordMLPackage.getMainDocumentPart.addParagraphOfText("x")

    // Now add an internal hyperlink to it
    val h: Hyperlink = MainDocumentPart.hyperlinkToBookmark(bookmarkName, "link to bookmark")
    wordMLPackage.getMainDocumentPart.addParagraphOfText("some text").getContent.add(h)

    System.out.println( XmlUtils.marshaltoString(p, true)  )

    val saver: SaveToZipFile = new SaveToZipFile(wordMLPackage)
    saver.save(outputfilepath)
  }

  def bookmarkRun(p: P, r: R, name: String, id: Int) {

    // Find the index
    val index = p.getContent.indexOf(r)

    if (index < 0) {
      System.out.println("P does not contain R!")
      return
    }

    val factory = Context.getWmlObjectFactory
    val ID = BigInteger.valueOf(id)


    // Add bookmark end first
    val mr = factory.createCTMarkupRange()
    mr.setId(ID)
    val bmEnd: JAXBElement[CTMarkupRange] = factory.createBodyBookmarkEnd(mr)
    p.getContent.add(index + 1, bmEnd)

    // Next, bookmark start
    val bm = factory.createCTBookmark()
    bm.setId(ID)
    bm.setName(name)
    val bmStart: JAXBElement[CTBookmark] =  factory.createBodyBookmarkStart(bm)
    p.getContent.add(index, bmStart)

  }
}
