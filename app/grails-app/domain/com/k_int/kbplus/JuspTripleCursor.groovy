package com.k_int.kbplus

class JuspTripleCursor {

  String titleId
  String supplierId
  String juspLogin
  String haveUpTo

  static mapping = { 
    titleId column:'jusp_title_id', index:'jusp_cursor_idx' 
    supplierId column:'jusp_supplier_id', index:'jusp_cursor_idx' 
    juspLogin column:'jusp_login_id', index:'jusp_cursor_idx' 
  } 
}
