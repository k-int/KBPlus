package com.k_int.kbplus

public class GokbDiffEngine {

  def static diff(ctx, oldpkg, newpkg, newTippClosure, updatedTippClosure, deletedTippClosure, pkgPropChangeClosure) {

    if (( oldpkg == null )||(newpkg==null)) {
      println("Error - null package passed to diff");
      return
    }
  
    if ( oldpkg.packageName != newpkg.packageName ) {
      println("packageName updated from ${oldpkg.packageName} to ${newpkg.packageName}");
      pkgPropChangeClosure(ctx, 'title', newpkg.packageName);
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

      System.out.println("Compare "+tippa+" and "+tippb);
      if ( tippa != null &&
           tippb != null &&
           tippa.titleId == tippb.titleId ) {
        System.out.println("  "+tippa+"    =    "+tippb);
        updatedTippClosure(ctx, tippb)
        // See if any of the actual properties are null
        tippa = ai.hasNext() ? ai.next() : null
        tippb = bi.hasNext() ? bi.next() : null
      }
      else if ( ( tippb != null ) &&
                  ( ( tippa == null ) ||
                    ( tippa.compareTo(tippb) > 0 ) ) ) {
        System.out.println("Title "+tippb+" Was added to the package");
        newTippClosure(ctx, tippb)
        tippb = bi.hasNext() ? bi.next() : null;
      }
      else {
        deletedTippClosure(ctx, tippa)
        System.out.println("Title "+tippa+" Was removed from the package");
        tippa = ai.hasNext() ? ai.next() : null;
      }
    }

  }

}
