package com.k_int.xml

import javax.xml.namespace.NamespaceContext
import javax.xml.XMLConstants
import org.w3c.dom.Document

public class DocumentNSResolver implements NamespaceContext {

  // the delegate
  private Document sourceDocument;

  /**
   * This constructor stores the source document to search the namespaces in
   * it.
   * 
   * @param document
   *            source document
   */
  public DocumentNSResolver(Document document) {
    sourceDocument = document;
  }

  /**
   * The lookup for the namespace uris is delegated to the stored document.
   * 
   * @param prefix
   *            to search for
   * @return uri
   */
  public String getNamespaceURI(String prefix) {

    String uri
    if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
      uri = sourceDocument.lookupNamespaceURI(null);
    } else {
      uri = sourceDocument.lookupNamespaceURI(prefix);
    }

    uri
  }

  /**
   * This method is not needed in this context, but can be implemented in a
   * similar way.
   */
  public String getPrefix(String namespaceURI) {
    return sourceDocument.lookupPrefix(namespaceURI);
  }

  public Iterator getPrefixes(String namespaceURI) {
    // not implemented yet
    return null;
  }

}
