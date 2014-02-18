package com.k_int.kbplus

public class GokbDiffEngine {

  def static diff(ctx, oldpkg, newpkg, newTippClosure, updatedTippClosure, deletedTippClosure, pkgPropChangeClosure, auto_accept) {

    if (( oldpkg == null )||(newpkg==null)) {
      println("Error - null package passed to diff");
      return
    }
  
    if ( oldpkg.packageName != newpkg.packageName ) {
      println("packageName updated from ${oldpkg.packageName} to ${newpkg.packageName}");
      pkgPropChangeClosure(ctx, 'title', newpkg.packageName, auto_accept);
    }
    else {
      println("packageName consistent");
    }

    if ( oldpkg.packageId != newpkg.packageId ) {
      println("packageId updated from ${oldpkg.packageId} to ${newpkg.packageId}");
    }
    else {
      println("packageId consistent");
    }

    oldpkg.tipps.sort { it.titleId }
    newpkg.tipps.sort { it.titleId }

    def ai = oldpkg.tipps.iterator();
    def bi = newpkg.tipps.iterator();

    def tippa = ai.hasNext() ? ai.next() : null
    def tippb = bi.hasNext() ? bi.next() : null

    while ( tippa != null || tippb != null ) {

      if ( tippa != null &&
           tippb != null &&
           tippa.titleId == tippb.titleId ) {

        def tipp_diff = getTippDiff(tippa, tippb)

        if ( tipp_diff.size() == 0 ) {
        } 
        else {
          // See if any of the actual properties are null
          println("Got tipp diffs: ${tipp_diff}");
          updatedTippClosure(ctx, tippb, auto_accept)
        }

        tippa = ai.hasNext() ? ai.next() : null
        tippb = bi.hasNext() ? bi.next() : null
      }
      else if ( ( tippb != null ) &&
                  ( ( tippa == null ) ||
                    ( tippa.compareTo(tippb) > 0 ) ) ) {
        System.out.println("Title "+tippb+" Was added to the package");
        newTippClosure(ctx, tippb, auto_accept)
        tippb = bi.hasNext() ? bi.next() : null;
      }
      else {
        deletedTippClosure(ctx, tippa, auto_accept)
        System.out.println("Title "+tippa+" Was removed from the package");
        tippa = ai.hasNext() ? ai.next() : null;
      }
    }

  }

  def static getTippDiff(tippa, tippb) {
    def result = []

    if ( (tippa.url?:'').toString().compareTo((tippb.url?:'').toString()) == 0 ) {
    }
    else {
      result.add([field:'url',value:tippb.url])
    }

    if ( tippa.coverage.equals(tippb.coverage) ) {
    }
    else {
      result.add([field:'coverage',value:tippb.coverage])
    }

    // See if the coverage is the same?
    result;
  }

}
