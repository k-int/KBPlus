package com.k_int.kbplus

class DocstoreController {

  def docstoreService

  def index() { 
    def doc = Doc.findByUuid(params.id);
    if ( doc ) {
      docstoreService.retrieve(params.id, response, doc.mimeType, doc.filename);
    }
  }
}
