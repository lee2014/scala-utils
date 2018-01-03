package geoip

import java.io.File
import java.net.InetAddress

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import com.maxmind.geoip2.DatabaseReader

/**
  * Created by chengli at 21/07/2017
  */
class IpTest extends FunSuite with BeforeAndAfterAll {

  test("try maxmind geoip") {
    val classLoader = getClass.getClassLoader
    val database = new File(classLoader.getResource("./GeoLite2-City.mmdb").getFile)

    // This creates the DatabaseReader object, which should be reused across
    // lookups.
    val reader = new DatabaseReader.Builder(database).build

    val ipAddress = InetAddress.getByName("121.101.0.0")


    // Replace "city" with the appropriate method for your database, e.g.,
    // "country".
    val response = reader.city(ipAddress)

    val country = response.getCountry
    System.out.println(country.getIsoCode) // 'US'
    System.out.println(country.getName) // 'United States'
    System.out.println(country.getNames.get("zh-CN")) // '美国'


    val subdivision = response.getMostSpecificSubdivision
    System.out.println(subdivision.getName) // 'Minnesota'
    System.out.println(subdivision.getNames)
    System.out.println(subdivision.getNames.get("zh-CN")) // 'Minnesota'
    System.out.println(subdivision.getIsoCode) // 'MN'


    val city = response.getCity
    System.out.println(city.getNames) // 'Minneapolis'

    val postal = response.getPostal
    System.out.println(postal.getCode) // '55455'


    val location = response.getLocation
    System.out.println(location.getLatitude) // 44.9733
    System.out.println(location.getLongitude) // -93.2323

  }
}
