package edu.stanford.nlp.sempre;

import fig.basic.LispTree;

/**
 * For timeouts, server exceptions, etc.
 *
 * @author Percy Liang
 */
public class ErrorValue extends Value {
  // Logical form is invalid and can't be converted into SQL query for some reason.
  // Example: (and x y), where x and y are two predicates
  // Example: unbound variables
  public static final ErrorValue badFormula(BadFormulaException e) {
    return new ErrorValue("BADFORMULA: " + e);
  }

  // Request is taking too long (caused by client-side timeouts).
  public static final ErrorValue timeout = new ErrorValue("TIMEOUT");

  // Server dropped the connection (sometimes because the request is taking too long).
  public static final ErrorValue server408 = new ErrorValue("SERVER408");

  // Internal server error (happens when Virtuoso thinks its going to take too long).
  // Example: Virtuoso 42000 Error The estimated execution time 541 (sec) exceeds the limit of 400 (sec).
  public static final ErrorValue server500 = new ErrorValue("SERVER500");

  // Server returned back an empty response.
  public static final ErrorValue empty = new ErrorValue("EMPTY");

  // Server returned something back but it had a bad format (e.g., HTML instead of XML).
  public static final ErrorValue badFormat = new ErrorValue("BADFORMAT");

  public final String type;

  public ErrorValue(LispTree tree) { this.type = tree.child(1).value; }
  public ErrorValue(String type) { this.type = type; }

  public LispTree toLispTree() {
    LispTree tree = LispTree.proto.newList();
    tree.addChild("error");
    tree.addChild(type != null ? type : "");
    return tree;
  }

  @Override
  public String toString() { return type; }
  public static ErrorValue fromString(String s) {
    if (s.equals(timeout.type)) return timeout;
    if (s.equals(server408.type)) return server408;
    if (s.equals(server500.type)) return server500;
    if (s.equals(empty.type)) return empty;
    if (s.equals(badFormat.type)) return badFormat;
    return null;
  }

  public double getCompatibility(Value thatValue) {
    return 0;  // Never give points for error.
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ErrorValue that = (ErrorValue) o;
    if (!type.equals(that.type)) return false;
    return true;
  }

  @Override public int hashCode() { return type.hashCode(); }
}
