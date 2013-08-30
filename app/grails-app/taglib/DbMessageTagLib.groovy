import com.k_int.kbplus.*

class DbMessageTagLib {

  def message = { attrs, body ->
    out << "This is a message from the DB"
  }
}

