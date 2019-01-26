/*
 * Created on Jun 12, 2003
 *
 */
package fr.umlv.tatoo.cc.lexer.charset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import fr.umlv.tatoo.cc.lexer.charset.encoding.Encoding;

/**
 * @author jcervell
 *
 */
public class CharacterSet  {

  public static final Comparator<CharacterInterval> LEXICO =
    new Comparator<CharacterInterval>() {
    public int compare(CharacterInterval i1, CharacterInterval i2) {
      int dec=i1.getBegin()-i2.getBegin();
      if (dec==0)
        return i2.getEnd() - i1.getEnd();
      return dec;
    }
  };


  public CharacterSet(List<CharacterInterval> intervalList,
      boolean negate, Encoding encoding) {
    TreeSet<CharacterInterval> set = new TreeSet<CharacterInterval>(LEXICO);
    set.addAll(intervalList);
    if (negate)
      list = buildNegativeList(set,encoding);
    else
      list = buildPositiveList(set);
  }

  private List<CharacterInterval> buildPositiveList(TreeSet<CharacterInterval> set) {
    ArrayList<CharacterInterval> list = new ArrayList<CharacterInterval>();
    Iterator<CharacterInterval> i = set.iterator();
    CharacterInterval toAdd = i.next();
    for (; i.hasNext();) {
      CharacterInterval interval = i.next();
      if (interval.joinable(toAdd)) {
        toAdd = toAdd.join(interval);
      } else {
        list.add(toAdd);
        toAdd = interval;
      }
    }
    list.add(toAdd);
    return list;
  }

  private List<CharacterInterval>
    buildNegativeList(TreeSet<CharacterInterval> set, Encoding encoding) {
    ArrayList<CharacterInterval> list = new ArrayList<CharacterInterval>();
    Iterator<CharacterInterval> i = set.iterator();
    CharacterInterval toNotAdd = i.next();
    if (toNotAdd.getBegin() != encoding.getMinValue())
      list.add(
        new CharacterInterval(
         encoding.getMinValue(),
         toNotAdd.getBegin() - 1));
    for (; i.hasNext();) {
      CharacterInterval interval = i.next();
      if (interval.joinable(toNotAdd)) {
        toNotAdd = toNotAdd.join(interval);
      } else {
        list.add(
          new CharacterInterval(
            toNotAdd.getEnd() + 1,
            interval.getBegin() - 1));
        toNotAdd = interval;
      }
    }
    if (toNotAdd.getEnd() != encoding.getMaxValue())
      list.add(
        new CharacterInterval(
          toNotAdd.getEnd() + 1,
          encoding.getMaxValue()));
    return list;
  }
  
  /**
   * @param list a list of sorted intervals which must not overlap
   */
  public CharacterSet(List<CharacterInterval> list) {
    this.list = list;
  }

  public CharacterSet union(CharacterSet secondSet) {
    if (secondSet == null) {
      return this;
    }

    ArrayList<CharacterInterval> rep = new ArrayList<CharacterInterval>();

    Iterator<CharacterInterval> a = list.iterator();
    Iterator<CharacterInterval> b = secondSet.list.iterator();

    CharacterInterval currentA = null, currentB = null;

    while ((a.hasNext() || currentA != null)
      && (b.hasNext() || currentB != null)) {
      if (currentA == null)
        currentA = a.next();

      if (currentB == null)
        currentB = b.next();

      if (currentA.getBegin() > currentB.getEnd() + 1) {
        rep.add(currentB);
        currentB = null;
        continue;
      }

      if (currentB.getBegin() > currentA.getEnd() + 1) {
        rep.add(currentA);
        currentA = null;
        continue;
      }

      if (currentA.getEnd() == currentB.getEnd()) {
        rep.add(currentA.join(currentB));
        currentA = null;
        currentB = null;
        continue;
      }

      if (currentA.getEnd() > currentB.getEnd()) {
        currentA = currentA.join(currentB);
        currentB = null;
        continue;
      }

      currentB = currentB.join(currentA);
      currentA = null;
    }

    if (currentA != null)
      rep.add(currentA);

    if (currentB != null)
      rep.add(currentB);

    while (a.hasNext()) {
      rep.add(a.next());
    }

    while (b.hasNext()) {
      rep.add(b.next());
    }

    return new CharacterSet(rep);
  }

  /** 
   *  split(A,B) returns (A\B,A inter B,B\A) each is null if empty
   */
  public Triplet split(CharacterSet secondSet) {

    ArrayList<CharacterInterval> aMinusB =
      new ArrayList<CharacterInterval>();
    ArrayList<CharacterInterval> aInterB =
      new ArrayList<CharacterInterval>();
    ArrayList<CharacterInterval> bMinusA =
      new ArrayList<CharacterInterval>();

     Iterator<CharacterInterval> a = list.iterator();
     Iterator<CharacterInterval> b = secondSet.list.iterator();

    CharacterInterval currentA = null, currentB = null;

    while ((a.hasNext() || currentA != null)
      && (b.hasNext() || currentB != null)) {
      /*System.out.println("currentA : "+currentA);
      System.out.println("currentB : "+currentB);
      System.out.println("A\\B : "+aMinusB);
      System.out.println("A inter B : "+aInterB);
      System.out.println("B\\A : "+bMinusA);*/
      if (currentA == null)
        currentA = a.next();

      if (currentB == null)
        currentB = b.next();

      if (currentA.getBegin() > currentB.getEnd()) {
        bMinusA.add(currentB);
        currentB = null;
        continue;
      }

      if (currentB.getBegin() > currentA.getEnd()) {
        aMinusB.add(currentA);
        currentA = null;
        continue;
      }

      if (currentA.getBegin() > currentB.getBegin()) {
        bMinusA.add(
          new CharacterInterval(
            currentB.getBegin(),
            currentA.getBegin() - 1));
      } else if (currentA.getBegin() < currentB.getBegin()) {
        aMinusB.add(
          new CharacterInterval(
            currentA.getBegin(),
            currentB.getBegin() - 1));
      }

      aInterB.add(currentA.inter(currentB));

      if (currentA.getEnd() > currentB.getEnd()) {
        currentA =
          new CharacterInterval(
            currentB.getEnd() + 1,
            currentA.getEnd());
        currentB = null;
      } else if (currentA.getEnd() < currentB.getEnd()) {
        currentB =
          new CharacterInterval(
              currentA.getEnd() + 1,
              currentB.getEnd());
        currentA = null;
      } else {
        currentA = null;
        currentB = null;
      }
    }

    if (currentA != null)
      aMinusB.add(currentA);

    if (currentB != null)
      bMinusA.add(currentB);

    while (a.hasNext()) {
      aMinusB.add(a.next());
    }

    while (b.hasNext()) {
      bMinusA.add(b.next());
    }

    /*System.out.println("currentA : "+currentA);
      System.out.println("currentB : "+currentB);
      System.out.println("A\\B : "+aMinusB);
      System.out.println("A inter B : "+aInterB);
      System.out.println("B\\A : "+bMinusA);*/

    CharacterSet aMinusBSet;
    CharacterSet aInterBSet;
    CharacterSet bMinusASet;

    if (!aMinusB.isEmpty())
      aMinusBSet = new CharacterSet(aMinusB);
    else
      aMinusBSet = null;

    if (!aInterB.isEmpty())
      aInterBSet = new CharacterSet(aInterB);
    else
      aInterBSet = null;

    if (!bMinusA.isEmpty())
      bMinusASet = new CharacterSet(bMinusA);
    else
      bMinusASet = null;

    return new Triplet(aMinusBSet, aInterBSet, bMinusASet);
  }

//  // Gilles why a char and not an int ?
//  public boolean in(char c) {
//    //    System.out.println(this.toString() + " " +c);
//    int low = 0;
//    int high = list.size() - 1;
//
//    while (low <= high) {
//      int mid = (low + high) >>> 1;
//      CharacterInterval interval = list.get(mid);
//
//      if (c > interval.getEnd())
//        low = mid + 1;
//      else if (c < interval.getBegin())
//        high = mid - 1;
//      else
//        return true;
//    }
//    return false;
//  }

  
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof CharacterSet)) {
      return false;
    }
    CharacterSet set = (CharacterSet) obj;
    return list.equals(set.list);
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }

  public List<CharacterInterval> getList() {
    return list;
  }
  
  @Override
  public String toString() {
    return list.toString();
  }
  
  /**
   * returns {@code null} is this interval does not contain one single character
   * or this character.
   * @return {@code null} is this interval does not contain one single character
   * or this character.
   */
  public Integer getSingleCharacter() {
    if (list.size()!=1)
      return null;
    CharacterInterval set = list.get(0);
    if (set.getBegin()!=set.getEnd())
      return null;
    return set.getBegin();
  }
  
  private final List<CharacterInterval> list;
}
