package org.gentsim.framework

/**
 * This class allows complex conditions to be formed.  This is used to express interest in entities and events.
 * Anything that has a map called "attributes" can have conditions set against it.
 */
class Condition {

  private final conditions = [] // list of closures to test for conditions.

  /**
   * Defines a simple condition with no values.
   */
  Condition () {
  }

  /**
   * Defines a simple condition with just a single name and value.
   * @param name A name to match on.
   * @param value A value to match on.
   */
  Condition (name, value) {
    this.match(name, value)
  }

  /**
   * Defines a condition with multiple names and value.
   * @param m A set of name/value pairs.
   */
  Condition (Map m) {
    conditions.matchAll(m)
  }

  /** Prints out a more friendly version. */
  String toString() {
    "Conditions:  ${conditions}"
  }

  /**
   * True if the condition matches against the thing.
   * Default mapping is an and condition.
   * Returns true if the thing matches the current condition.
   * @return true if the thing matches this condition.
   */
  def matches (thing) {
    def attributes = thing.attributes
    for (condition in this.conditions) {
      if (!condition(thing)) return false
    }
    return true // matched all conditions
  }

  /**
   * Basic match closure to see if the the value matches.  Handles both 
   * regular values and closures.
   */
  private valueMatch (thing, name, value) {
    if (value instanceof Closure) {
      value(thing.attributes[name])
    }
    else if (thing.attributes[name] instanceof String) {
//println "----string match ${thing.attributes[name]}.matches(${value}) == ${thing.attributes[name].matches(value)}"
      thing.attributes[name].matches(value) // support pattern matching on all string attributes.
    }
    else {
      thing.attributes[name] == value 
    }
  }

  /**
   * Adds a match condition to match when the key has the given value.
   * @param name The name of the attribute to match.
   * @param value The value to match.
   */
  def match (name, value) {
    conditions << { thing -> 
      valueMatch(thing, name, value)
    }
    this
  }

  /**
   * Adds a match condition if the closure evaluates to true.
   * @param c The closure that accepts the thing to match against.
   */
  def match (Closure c) {
    conditions << { thing -> c(thing) }
    this
  }

  /**
   * Adds a match on a range of values.  This will be true if the named attribute matches any of the values.
   * @param name The name of the attribute.
   * @param values The list (or range) of values to compare to.
   */
  def matchAny (name, List values) {
    conditions << { thing -> 
      for (entry in values) {
        if (valueMatch(thing, name, entry)) return true
      }
    }
    this
  }

  /**
   * Adds a match for any of the specific name / value pairs.
   * @param map A map of name/value pairs that must match.
   */
  def matchAny (Map map) {
    conditions << { thing ->
      for (entry in map) {
        if (valueMatch(thing, entry.key, entry.value)) return true
      }
      return false
    }
    this
  }

  /**
   * Adds a match for all of the specific name / value pairs.
   * @param m A map of name/value pairs that must match.
   */
  def matchAll (Map m) {
    conditions << { thing ->
      for (entry in m) {
        if (!valueMatch(thing, entry.key, entry.value)) return false
      }
      return true
    }
    this
  }

  /**
   * Adds a match on the range of values.
   * TODO improve the performance of this since a list evaluates each member individually.
   * @param name The name of the attribute to test.
   * @param r The range of valid values.
   */
  def matchRange (name, Range r) {
    matchAny (name, r) // since a range is just a list.
    this
  }

  /**
   * Adds a match against list intersection.  If any of the entries in the thing match any of the entries in the 
   * condition, then this is a match.
   * @param name The name of the attribute to test.
   * @param list The list of possible values.
   */
  def matchIntersects (name, List list) {
    conditions << { thing ->
      thing.attributes[name] && thing.attributes[name].intersect(list) != []
    }
    this
  }
}

