/*
Copyright © 2009 William D. Back (bback@gentsim.org)
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
import org.gentsim.framework.*

car = new EntityDescription("car")
car.parameter "number-wheels", 4
car.attribute "speed", 0

airplane = new EntityDescription("airplane")
airplane.attribute "max-altitude", 50000
airplane.attribute "altitude"
airplane.attribute "number-passegers"
airplane.attribute "speed", 0
