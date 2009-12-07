/*
Copyright © 2009 William D. Back
This file is part of gentsim.

    gentsim is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    gentsim is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with gentsim.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gentsim.framework.distributed

/**
 * Interface to define common constants for working with JMS.
 * @author Bill Back
 */
public interface JMSConstants {

  // JMS constants for configuration
  static final String JMSDistributed        = "gentsim.jms.distributed"
  static final String JMSIP                 = "gentsim.jms.IP"
  static final String JMSPort               = "gentsim.jms.port"

  // JMS Topics
  static final String JMSSystemControlTopic = "gentsim.system.control"
  static final String JMSSystemStatusTopic  = "gentsim.system.status"
  static final String JMSSystemTraceTopic   = "gentsim.system.trace"
  static final String JMSEventTopic         = "gentsim.events"

}