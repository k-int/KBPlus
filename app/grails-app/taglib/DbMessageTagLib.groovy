import com.k_int.kbplus.*


import org.springframework.web.servlet.support.RequestContextUtils as RCU

class DbMessageTagLib {

  def messageService

  def dbContent = { attrs, body ->
    def locale = RCU.getLocale(request)
    out << messageService.getMessage(attrs.key, locale?.toString())
  }
}

