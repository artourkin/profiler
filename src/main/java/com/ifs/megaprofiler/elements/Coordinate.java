package com.ifs.megaprofiler.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 4/23/14.
 */
public class Coordinate extends ArrayList<String> implements Comparable<Coordinate> {

    public Coordinate(List<String> sectorCoordinates) {
        //ArrayList<Integer> integers = ToNumericCoordinates(sectorCoordinates);
        //  this.addAll(integers);
        this.addAll(sectorCoordinates);

    }

       /* public ArrayList<Integer> ToNumericCoordinates(List<String> coordinates) {
            ArrayList<Integer> result = new ArrayList<Integer>();

            for (int i = 0; i < propertyNames.size(); i++) {
                propertyValuesByName.get(i).add(coordinates.get(i));
            }

            for (int i = 0; i < coordinates.size(); i++) {
                List<String> strings = new ArrayList<String>();
                strings.addAll(propertyValuesByName.get(i));
                int indexOf = strings.indexOf(coordinates.get(i));
                result.add(indexOf);
            }
            return result;
        }
*/

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
