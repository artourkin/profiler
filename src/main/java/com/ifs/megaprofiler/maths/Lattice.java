package com.ifs.megaprofiler.maths;

/**
 * Copyright 2013-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express
 * or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */


import com.ifs.megaprofiler.elements.Record;

import java.util.*;
import java.util.Map.Entry;


// extended by: Artur


/**
 * A lattice for describing the end points associated with a service and how
 * they are compartmentalized.
 *
 * The basic idea behind a lattice is to describe the availability and
 * fault-isolating compartments in which service end points are located. Lattice
 * can be N-dimensional, where each dimension is a type of dependency that may
 * cause a fault.
 *
 * An example of a one dimensional lattice is a service that is spread across
 * multiple Amazon Web Services availability zones. For example some end points
 * may be in availability zone us-east-1a, some in us-east-1b and some in
 * us-east-1c. With a Lattice we can associate each endpoint with its
 * availability zone and also simulate failure of any zone and generate a new
 * lattice with the failed endpoints removed.
 *
 * <pre>
 *    us-east-1a     us-east-1b     us-east-1c
 * +--------------+--------------+--------------+
 * |              |              |              |
 * | A B C D E F  | G H I J K L  |  M N O P Q R |
 * |              |              |              |
 * +--------------+--------------+--------------+
 * </pre>
 *
 * With a Lattice we can associate each endpoint with its availability zone and
 * also simulate failure of any zone and generate a new lattice with the failed
 * endpoints removed.
 *
 * <pre>
 * simulateFailure("AvailabilityZone", "us-east-1a") =
 *
 *    us-east-1b     us-east-1c
 * +--------------+--------------+
 * |              |              |
 * |  F G H I J   |  K L M N O   |
 * |              |              |
 * +--------------+--------------+
 * </pre>
 *
 * A more complex example is a two dimensional lattice. For example a service
 * may consist of endpoints in multiple availability zones by may also use run
 * two different software implementations in each zone. Since a bug in one
 * implementation could impact many end points, this is considered an orthagonal
 * axis in the lattice.
 *
 * <pre>
 *           us-east-1a     us-east-1b     us-east-1c
 *        +--------------+--------------+--------------+
 *        |              |              |              |
 * Python |     A B C    |     G H I    |    M N O     |
 *        |              |              |              |
 *        +--------------+--------------+--------------+
 *        |              |              |              |
 *  Ruby  |     D E F    |     J K L    |    P Q R     |
 *        |              |              |              |
 *        +--------------+--------------+--------------+
 * </pre>
 *
 * Again, failures may be simulated, now in two different dimensions. For
 * example;
 *
 * <pre>
 * simulateFailure("AvailabilityZone", "us-east-1b") =
 *
 *           us-east-1a     us-east-1c
 *        +--------------+--------------+
 *        |              |              |
 * Python |     A B C    |    M N O     |
 *        |              |              |
 *        +--------------+--------------+
 *        |              |              |
 *  Ruby  |     D E F    |    P Q R     |
 *        |              |              |
 *        +--------------+--------------+
 *
 * simulateFailure("SoftwareImplementation", "Python") =
 *           us-east-1a     us-east-1b     us-east-1c
 *        +--------------+--------------+--------------+
 *        |              |              |              |
 *  Ruby  |     D E F    |     J K L    |    P Q R     |
 *        |              |              |              |
 *        +--------------+--------------+--------------+
 * </pre>
 *
 * Higher dimensional lattices are also permitted.
 *
 * @param <T>
 *            The type for the endpoints in the lattice.
 */
public class Lattice<T> {

    /**
     * Coordinate is a private internal class that represents the coordinates of
     * cells in n-dimensional lattices. Each coordinate is an array list of
     * strings, but this class also implements Comparable so that these
     * coordinates can be used as ordered keys in a map.
     */
    @SuppressWarnings("serial")
    protected class Coordinate extends ArrayList<String> implements Comparable<Coordinate> {

        public Coordinate(List<String> sectorCoordinates) {
            super(sectorCoordinates);



        }
        public Coordinate(List<String> sectorCoordinates,Map<String, Set<String>> valuesByDimension) {
            sectorCoordinates= ToInt(sectorCoordinates,valuesByDimension);
            this.addAll(sectorCoordinates);
        }

        public ArrayList<String> ToInt(List<String> coordinates,Map<String, Set<String>> valuesByDimension){
            ArrayList<String> result=new ArrayList<String>();
            for (int i=0; i<coordinates.size();i++)
            {
                List<String> strings =  new ArrayList<String>();
                 strings.addAll(valuesByDimension.get(dimensionNames.get(i))) ;
                //for(int j=0; j< strings.size(); j++) {
              //      if (strings.)
              //  }
                int indexOf = strings.indexOf(coordinates.get(i));
                result.add(Integer.toString(indexOf));
            }
            return result;
        }

        @Override
        public int compareTo(Coordinate other) {

            for (int i = 0; i < this.size(); i++) {
                if (other.size() < i) {
                    return -1;
                }
                if (!other.get(i).equals(this.get(i))) {
                    return this.get(i).compareTo(other.get(i));
                }
            }

            return 0;
        }

    }

    /* We store a copy of the dimension names */
    protected final List<String> dimensionNames;

    /*
     * Each dimension also has a valid set of values, e.g. the
     * "AvailabilityZone" dimension may have the values "us-east-1a",
     * "us-east-1b", "us-east-1c". These values represent positions along the
     * dimension and form part of the sector coordinates for an end-point.
     */
    protected final Map<String, Set<String>> valuesByDimension;

    /*
     * We keep a map of the end-points by sector coordinates. For example if our
     * dimensions are "AvailabilityZone" and "SoftwareVersion" then we might
     * have something like;
     *
     * [ "us-east-1a", "v2.1" ] -> [
     * set-of-end-points-in-us-east-1a-running-v2.1 ]
     */
    protected final Map<Coordinate, Collection<T>> endpointsByCoordinate = new TreeMap<Coordinate, Collection<T>>();

    /**
     * Create an n-dimensional Lattice where each dimension represents a
     * meaningful availability axis. For example a List containing the strings
     * "AvailabilityZone", "SoftwareVersion" creates a two dimensional lattice.
     *
     * @param dimensionNames
     *            List of human-meaningful names for the dimensions.
     */
    public Lattice(List<String> dimensionNames) {
        if (dimensionNames.size() == 0) {
            throw new IllegalArgumentException("At least one dimension is required");
        }
        this.dimensionNames = dimensionNames;
        this.valuesByDimension = new HashMap<String, Set<String>>(dimensionNames.size());

        for (String dimensionName : dimensionNames) {
            this.valuesByDimension.put(dimensionName, new HashSet<String>());
        }
    }

    /**
     * Add all of the end-points associated with a particular sector.
     *
     * @param sectorCoordinates
     *            The coordinates of the sector
     * @param endpoints
     *            The end-points to be added
     */
    public void addEndpointsForSector(List<String> sectorCoordinates, Collection<T> endpoints) {
        if (sectorCoordinates.size() != dimensionNames.size()) {
            throw new IllegalArgumentException("Mismatch between dimensions of lattice and sector");
        }

         /* Add the coordinate values to our list of values by dimension */
        for (int i = 0; i < dimensionNames.size(); i++) {
            this.valuesByDimension.get(dimensionNames.get(i)).add(sectorCoordinates.get(i));
        }

        ArrayList<T> toBeAdded = new ArrayList<T>(endpoints);
        Collection<T> existing = endpointsByCoordinate.get(new Coordinate(sectorCoordinates, valuesByDimension));
        if (existing != null) {
            toBeAdded.addAll(existing);
        }

        endpointsByCoordinate.put(new Coordinate(sectorCoordinates, valuesByDimension), toBeAdded);


    }




    /**
     * Get the endpoints in a particular sector
     *
     * @param sectorCoordinates
     *            The coordintes of the sector
     * @return All end-points associated with a particular sector
     */
    public Collection<T> getEndpointsForSector(List<String> sectorCoordinates) {
        if (sectorCoordinates.size() != dimensionNames.size()) {
            throw new IllegalArgumentException("Mismatch between dimensions of lattice and sector");
        }

        return endpointsByCoordinate.get(new Coordinate(sectorCoordinates));
    }

    /**
     * Get all of the end-points in the lattice
     *
     * @return all end-points in the lattice
     */
    public List<T> getAllEndpoints() {
        List<T> allEndpoints = new ArrayList<T>();
        for (Collection<T> endpoints : endpointsByCoordinate.values()) {
            allEndpoints.addAll(endpoints);
        }
        return allEndpoints;
    }

    /**
     * Get a list of all cells in the lattice
     *
     * @return A list of the coordinates for all cells in the lattice
     */
    public Set<List<String>> getAllCoordinates() {
        Set<List<String>> allCoordinates = new HashSet<List<String>>();

        for (Coordinate coordinate : endpointsByCoordinate.keySet()) {
            allCoordinates.add(coordinate);
        }

        return allCoordinates;
    }

    /**
     * How many dimensions does this lattice have?
     *
     * @return The dimensionality of this lattice
     */
    public Map<String, Integer> getDimensionality() {
        Map<String, Integer> dimensionality = new HashMap<String, Integer>(dimensionNames.size());

        for (String dimension : dimensionNames) {
            dimensionality.put(dimension, getDimensionValues(dimension).size());
        }

        return dimensionality;
    }

    /**
     * Get the list of dimension names
     *
     * @return the list of dimension names
     */
    public List<String> getDimensionNames() {
        return dimensionNames;
    }

    /**
     * Get the dimension name for a given numbered dimension
     *
     * @param dimension
     *            the numbered dimension
     * @return the dimension name
     */
    public String getDimensionName(int dimension) {
        return dimensionNames.get(dimension);
    }

    /**
     * Get the set of values for a given dimension, e.g. ["us-east-1a",
     * "us-east-1b"] may be valid values for the "AvailabilityZone" dimension.
     *
     * @param dimensionName
     *            The name of the dimension
     * @return The set of valid values for this dimension
     */
    public Set<String> getDimensionValues(String dimensionName) {
        return valuesByDimension.get(dimensionName);
    }

    /**
     * How many discrete coordinates are there in a given dimension
     *
     * @param dimensionName
     *            The dimension
     * @return the number of coordinate values this dimension contains
     */
    public int getDimensionSize(String dimensionName) {
        return valuesByDimension.get(dimensionName).size();
    }

    /**
     * Simulate failure of a particular slice of cells in the lattice. E.g.
     * remove all endpoints that have "AvailabilityZone" => "us-east-1a".
     *
     * @param dimensionName
     *            The dimension on which the failure should occur
     * @param dimensionValue
     *            The value within the dimension to fail
     * @return A sublattice of the remaining endpoints after a simulated failure
     */
    public Lattice<T> simulateFailure(String dimensionName, String dimensionValue) {
        Lattice<T> sublattice = new Lattice<T>(dimensionNames);

        int dimensionNumber = dimensionNames.indexOf(dimensionName);
        if (dimensionNumber == -1) {
            throw new IllegalArgumentException("Unknown dimension name");
        }

        for (List<String> coordinate : endpointsByCoordinate.keySet()) {
            if (!coordinate.get(dimensionNumber).equals(dimensionValue)) {
                sublattice.addEndpointsForSector(coordinate, endpointsByCoordinate.get(coordinate));
            }
        }

        return sublattice;
    }

    /**
     * @return the lattice in string form
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < dimensionNames.size(); i++) {
            if (i > 0) {
                sb.append(" , ");
            }
            sb.append(dimensionNames.get(i));
        }
        sb.append("]");
        sb.append(System.getProperty("line.separator"));

        for (Entry<Coordinate, Collection<T>> entry : endpointsByCoordinate.entrySet()) {
            sb.append("[");
            for (int i = 0; i < dimensionNames.size(); i++) {
                if (i > 0) {
                    sb.append(" , ");
                }
                sb.append(entry.getKey().get(i));
            }
            sb.append("] -> [");

            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i > 0) {
                    sb.append(" , ");
                }
                sb.append(entry.getValue().toArray()[i].toString());
            }

            sb.append("]");
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }
}