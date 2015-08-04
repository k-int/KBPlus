package com.k_int.kbplus
import org.hibernate.Session

class PackageService{

	  /**
   * @return The scope value to be used by "Master Packages"
   */
  private RefdataValue getMasterScope() {
    // The Scope.
    RefdataCategory.lookupOrCreate("Package.Scope", "Master File")
   }

   private RefdataValue getCPRole(){
   	RefdataCategory.lookupOrCreate('Organisational Role','Content Provider')
   }
  /**
   * Method to update all Master list type packages.
   */
  def updateAllMasters(delta = true) {

    // Create the criteria.
    getAllProviders().each { Org pr ->
      Org.withNewSession ({ long prov_id, Session sess ->
        updateMasterFor (pr.id, delta)
      }.curry(pr.id))
    }
  }

  def setProvider(org_to_link,pkg_to_link){
  	def rel = RefdataCategory.lookupOrCreate('Organisational Role','Content Provider');

  	def newLink = new OrgRole(org:org_to_link,roleType:rel)
  	newLink.package = pkg_to_link
  	newLink.save(failOnError:true)
  	return newLink
  }
   /**
   * Method to create or update a Package containing a list of all titles
   * provided by the supplied Org.
   */
  def updateMasterFor (long provider_id, delta = true) {
      
    // Read in a provider.
    Org provider = Org.get(provider_id)

    if (provider) {
      log.debug ("Update or create master list for ${provider.name}")

      // Get the current master Package for this provider.

		Package master =  Package.createCriteria().get {
		  eq('packageScope', getMasterScope())
		  orgs {
		     and {
		        eq('roleType', getCPRole())
		        eq('org', provider)
		     }
		  }
		}
      // Update or create?
      if (master) {
  
        // Update...
        log.debug ("Found package ${master.id} for ${provider.name}")
  
        delta = delta ? master.lastUpdated : false
      } else {
  
        // Set delta to false...
        delta = false
  
        master = new Package()
  
        // Need to pass the system_save parameter to flag as systemComponent.
        master.save(failOnError:true)
  
        // Create new...
        log.debug ("Created Master Package ${master.id} for ${provider.name}.")
      }
  
      master.setName("${provider.name}: Master List")
      master.setPackageScope(getMasterScope())

      // Save.
      master.save(failOnError:true)
      setProvider(provider,master)
      provider.save(failOnError:true, flush:true)
      
      log.debug("Saved Master package ${master.id}")

      // Now query for all packages for the modified since the delta.
      c =  Package.createCriteria() 
      Set<Package> pkgs = c.list {
        c.and {
          c.add(
            "id",
            "ne",
            master.id)
  
          c.add(
          	orgs{
          		and{
          			eqId('org',provider_id)
          			eq('roleType',getCPRole())
          		}
          	})
  
          if (delta) {
            c.add(
              "lastUpdated",
              "gt",
              delta)
          }
        }
      } as Set
  
      log.debug ("${pkgs.size() ?: 'No'} packages have been updated since the last time this master was updated.")
  
      
      for (Package pkg in pkgs) {
  
        // We should now have a definitive list of tipps that have been changed since the last update.
        
        // Go through the tipps in chunks.
        //def tipps = pkg.tipps.collect { it.id }

        def tipps = TitleInstancePackagePlatform.executeQuery('select tipp.id from TitleInstancePackagePlatform as tipp where tipp.pkg=?',[pkg]);
        
        log.debug("Query returns ${tipps.size()} tipps");

        TitleInstancePackagePlatform.withNewSession {
         
          int counter = 1
  
          for (def t in tipps) {
            
            TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(t)
              
            // Do we need to update this tipp.
            if (!delta || (delta && tipp.lastUpdated > delta)) {
              TitleInstancePackagePlatform mt = setOrUpdateMasterTippFor (tipp.id, master.id)
            } else {
              log.debug ("TIPP ${tipp.id} has not been updated since last run. Skipping.")
            }
            log.debug ("TIPP ${counter} of ${tipps.size()} examined.")
            counter++
            tipp.discard();
          }
        }
      }
      log.debug("Finished updating master package ${master.id}")
    }
  }

    /**
   * @param tipp the tipp to base the master tipp on.
   * @param master the master package
   * @return the master tipp
   */
  public TitleInstancePackagePlatform setOrUpdateMasterTippFor (long tipp_id, long master_id) {
    
    Package master = Package.get(master_id)
    TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tipp_id)
    
    // Check the tipp isn't already a master.
    Package pkg = tipp.pkg
    if (pkg.status == getMasterScope()) {
      // Throw an exception.
      log.debug ("getMasterTipp called for TIPP {tipp.id} that is already a master. Returning supplied tipp.")
      return tipp
    }
    
    // Master TIPP
    TitleInstancePackagePlatform master_tipp = tipp.masterTipp
    
    if (!master_tipp) {
      
      log.debug ("No master TIPP associated with this TIPP directly. We should query for one.")
      
      // Now let's try and read an existing tipp from the master package.
      def mtp = master.getTipps().find {
        (it.title == tipp.getTitle()) &&
        (it.hostPlatformURL == tipp.gethostPlatformURL())
      }
      master_tipp = (mtp ? TitleInstancePackagePlatform.deproxy( mtp ) : null)
    }
    
    if (!master_tipp) {
      // Create a new master tipp.
      master_tipp = tipp.clone().save(failOnError:true)
      log.debug("Added master tipp ${master_tipp.id} to tipp ${tipp.id}")
    } else {
      
      log.debug("Found master tipp ${master_tipp.id} to tipp ${tipp.id}")
      master_tipp = tipp.sync(master_tipp)
    }
    
    // Ensure certain values are correct.
    master_tipp.with {
      setName(null)
      setPkg (master) 
   }
    
    
    // Save the master tipp.
    master_tipp.save(failOnError:true)
    master.save(failOnError:true)
    tipp.save(failOnError:true, flush:true)
    
    // Set as master for faster lookup.
    tipp.setMasterTipp(master_tipp)
    tipp.save(failOnError:true, flush:true)
    
    // Return the TIPP.
    master_tipp
  }
 
   /**
   * Get the Set of Orgs currently acting as a provider.
   */
  public Set<Org> getAllProviders () {

    log.debug ("Looking for all providers.")

    // The results set.
    LinkedHashSet results = []
    
    // Create the criteria.
    def c =  Package.createCriteria() 

    // Query for a list of packages and return the providers.
    def del_stat = RefdataCategory.lookupOrCreate( 'Package Status', 'Deleted' )

    def providers = Package.findAllByPackageStatusIsNull().each {
    
      // Add any provider that is set.
      if (it?.getContentProvider()) {
        results << (it.getContentProvider())
      }
    } 

    log.debug("Found ${results.size()} providers.")
    results
  }
}