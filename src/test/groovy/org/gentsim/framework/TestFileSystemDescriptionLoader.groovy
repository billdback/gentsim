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
package org.gentsim.framework

import spock.lang.*

import org.gentsim.util.Trace

class TestFileSystemDescriptionLoader extends Specification {

  def "Test loading a description from a named file" () {
    setup:
      FileSystemDescriptionLoader fsdl = new FileSystemDescriptionLoader()
      // Assumes running test from the root location.
      SimulationContainer sc = new SimulationContainer()
      fsdl.loadDescriptionsFromLocation "src/test/resources/entities/Animals.groovy", sc
    expect:
      sc.getEntityDescription("car") == null
      sc.getEntityDescription("cat")
  }

  def "Test loading descriptions from a directory" () {
    setup:
      FileSystemDescriptionLoader fsdl = new FileSystemDescriptionLoader()
      SimulationContainer sc = new SimulationContainer()
      fsdl.loadDescriptionsFromLocation "src/test/resources/entities", sc

    expect:
      sc.getEntityDescription("car")
      sc.getEntityDescription("cat")
  }

  def "Test loading descriptions from multiple locations" () {
    setup:
      FileSystemDescriptionLoader fsdl = new FileSystemDescriptionLoader()
      SimulationContainer sc = new SimulationContainer()
      fsdl.loadDescriptionsFromLocations (["src/test/resources/entities",
                                           "src/test/resources/events",
                                           "src/test/resources/services"
                                          ], sc)
    expect:
      sc.getEntityDescription("car")
      sc.getEntityDescription("cat")
      sc.getEventDescription("some-event")
      sc.getServiceDescription("date")
  }

  def "Test loading a description from a named file as a resource" () {
    setup:
      FileSystemDescriptionLoader fsdl = new FileSystemDescriptionLoader()
      SimulationContainer sc = new SimulationContainer()
      fsdl.loadDescriptionsFromLocation "/entities/Animals.groovy", sc

    expect:
      sc.getEntityDescription("car") == null
      sc.getEntityDescription("cat")
  }

  def "Test loading descriptions from a directory as a resource" () {
    setup:
      FileSystemDescriptionLoader fsdl = new FileSystemDescriptionLoader()
      SimulationContainer sc = new SimulationContainer()
      fsdl.loadDescriptionsFromLocation "/entities", sc

    expect:
      sc.getEntityDescription("car")
      sc.getEntityDescription("cat")
  }

  def "Test loading descriptions from multiple locations as a resource" () {
    setup:
      FileSystemDescriptionLoader fsdl = new FileSystemDescriptionLoader()
      SimulationContainer sc = new SimulationContainer()
      fsdl.loadDescriptionsFromLocations (["/entities",
                                           "/events",
                                           "/services"
                                          ], sc)
    expect:
      sc.getEntityDescription("car")
      sc.getEntityDescription("cat")
      sc.getEventDescription("some-event")
      sc.getServiceDescription("date")
  }


}

