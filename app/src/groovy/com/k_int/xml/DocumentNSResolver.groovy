package com.k_int.xml

import javax.xml.namespace.NamespaceContext
import javax.xml.XMLConstants
import org.w3c.dom.Document

/**
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 *
 */
public class DocumentNSResolver implements NamespaceContext {

  // the delegate
  private Document sourceDocument;
  
  private static final String DEFAULT_NAMESPACE = "_"

  /**
   * This constructor stores the source document to search the namespaces in
   * it.
   * 
   * @param document source document
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
  public String getNamespaceURI(String prefix = XMLConstants.DEFAULT_NS_PREFIX) {
    
    // Lookup the prefix.
    String the_prefix = ((prefix == null) || XMLConstants.DEFAULT_NS_PREFIX.equals(prefix) || DEFAULT_NAMESPACE.equals(prefix)) ? null : prefix
    
    // Return the URI.
    String uri = sourceDocument.lookupNamespaceURI(the_prefix)
    
    uri
  }

  /* (non-Javadoc)
   * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
   */
  public String getPrefix(String namespaceURI) {    
    return sourceDocument.lookupPrefix(namespaceURI);
  }

  /* (non-Javadoc)
   * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
   */
  public Iterator getPrefixes(String namespaceURI) {
    // not implemented yet
    return null;
  }

}
