package com.k_int.kbplus

import grails.plugin.cache.CacheEvict
import grails.plugin.cache.Cacheable
import grails.plugin.cache.CachePut

class MessageService {

  @Cacheable('message')
  String getMessage(String key,String locale) {
    log.debug("getMessage(${key},${locale})");
    def ci=ContentItem.findByKeyAndLocale(key,locale)
    if ( ci != null ) {
      return ci.content
    }

    // If we didn't find a locale specific string, try and return the default no-locale string
    return getMessage(key)
  }

  @Cacheable('message')
  String getMessage(String key) {
    log.debug("getMessage(${key})");
    def ci = ContentItem.findByKeyAndLocale(key,'')
    if ( ci != null )
      return ci.content
     
    return ''
  }

  @CachePut(value='message', key='#{message.key+message.locale}')
  void update(String message) {
  }
}
