package com.k_int.kbplus.processing

public class OrgsProcessing {


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
      [ sourceProperty:'sector', targetProperty:'sectorName' ]
    ]
  ]
    
}
