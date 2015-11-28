package com.k_int.kbplus

import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.client.transport.TransportClient

class ESWrapperService {

  static transactional = false
  def grailsApplication
  def esclient = null;

  @javax.annotation.PostConstruct
  def init() {

    log.debug("ESWrapperService::init");

    def es_cluster_name = grailsApplication.config.aggr_es_cluster?:"elasticsearch"

    log.debug("es_cluster = ${es_cluster_name}");

    esclient = new TransportClient(ImmutableSettings.settingsBuilder {
      cluster {
        name = es_cluster_name
      }
      client {
        transport {
          sniff = true
        }
      }
    })

    esclient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300))
    log.debug("ES Init completed");
  }

  def getClient() {
    return esclient
  }

}
