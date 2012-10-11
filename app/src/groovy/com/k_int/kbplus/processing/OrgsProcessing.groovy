package com.k_int.kbplus.processing

import org.apache.commons.logging.LogFactory
import com.k_int.kbplus.*

public class OrgsProcessing {

  private static final log = LogFactory.getLog(this)

  public static def addIdentifier(value, appctx, type, domain_object) {
    log.debug("This is a testMethodCall");
    if ( domain_object ) {
      if ( value ) {
        def identifier = Identifier.lookupOrCreateCanonicalIdentifier(type,value);
        domain_object.ids.add(new IdentifierOccurrence(identifier:identifier,org:domain_object));
      }
      else {
        // Should we make sure there is no identifier attached?
      }
    }
    else {
      log.error("Domain object was null. this shouldn't be the case!");
      throw new RuntimeException("Domain object null in addIdentifier call for Org Record");
    }
  }


  public static def orgs_reconciliation_ruleset = [
    // Root identifies root object type
    domainClass:'com.k_int.kbplus.Org',
    recordMatching:[
      [ 
        matchingType:'simpleCorrespondence',
        pairs:[[sourceProperty:'_id',targetProperty:'impId']]
      ],
      [ 
        matchingType:'simpleCorrespondence',
        pairs:[[sourceProperty:'name',targetProperty:'name',transformation:'trim']]
      ]
    ],
    standardProcessing:[
      [ sourceProperty:'_id', targetProperty:'impId' ],
      [ sourceProperty:'name', targetProperty:'name', transformation:'trim' ],
      [ sourceProperty:'ipRange', targetProperty:'ipRange' ],
      [ sourceProperty:'sector', targetProperty:'sector' ],
      [ sourceProperty:'ringoldId', processingClosure: { value, appctx, domain_object -> OrgsProcessing.addIdentifier(value, appctx, 'Ringold', domain_object);}],
      [ sourceProperty:'ingentaId', processingClosure: { value, appctx, domain_object -> OrgsProcessing.addIdentifier(value, appctx, 'Ingenta', domain_object);}],
      [ sourceProperty:'jcId', processingClosure: { value, appctx, domain_object -> OrgsProcessing.addIdentifier(value, appctx, 'JC', domain_object);}],
      [ sourceProperty:'athensId', processingClosure: { value, appctx, domain_object -> OrgsProcessing.addIdentifier(value, appctx, 'Athens', domain_object);}],
      [ sourceProperty:'famId', processingClosure: { value, appctx, domain_object -> OrgsProcessing.addIdentifier(value, appctx, 'UKAMF', domain_object);}]
    ]
  ]
    
}
