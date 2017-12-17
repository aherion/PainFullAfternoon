package io.zipcoder;

import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;


// This is an exercise in which the goal was to accomplish parsing input from a custom (randomized) text file type and outputting
// in a certain format, as well as providing unit tests for functionality.


public class Main {

    public String readRawDataToString() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        return result;
    }

    public static void main(String[] args) throws Exception {
        String rawData = (new Main()).readRawDataToString();
        System.out.println(rawData);
        // TODO: parse the data in output into items, and display to console.

        ItemParser itemParser = new ItemParser();
        //Create array list for strings of raw data, to be parsed, which are passed by the already supplied raw data parser in ItemParser
        ArrayList<String> rawDataArrayList = itemParser.parseRawDataIntoStringArray(rawData);
        //Create array list to store data after it has been parsed into an Item via supplied, then built out, string parser in ItemParser
        ArrayList<Item> parsedItemsArrayList = new ArrayList<Item>();
        //create array list to keep track of any exceptions encountered during parsing process
        ArrayList<ItemParseException> itemParseExceptions = new ArrayList<ItemParseException>();


        //loop which attempts to apply the parseStringIntoItem method logic in ItemParser to supplied raw data; if the logic in that
        //called method completes successfully then parsed item is added to the parsedItemArrayList and, if not, it will catch the
        //exception defined in the aforementioned method and then be stored in the itemParseExceptions array list for later use
        for (String itemString : rawDataArrayList) {

            try {

                parsedItemsArrayList.add(itemParser.parseStringIntoItem(itemString));

            } catch (ItemParseException e) {

                itemParseExceptions.add(e);
            }

        }

        //for each unique item in parseItems array list apply formatting and logic defined within aggregateName/Price/ExceptionInfo methods
        //in order to print desired output information... Note: formatting is not a 100% match to output.txt; all information is
        //present and in the right place but lines are not padded to be as neat as output.txt (will hopefully have time to revisit this
        //in the future)
        String items = "";
        for (Item item : parsedItemsArrayList) {

            if (!items.contains(item.getName())) {
                items += aggregateNameInfo(item, parsedItemsArrayList);

                items += aggregatePriceInfo(item, parsedItemsArrayList);
            }
        }

        items += aggregateExceptionInfo(itemParseExceptions);

        System.out.println(items);
    }

    static private String aggregateNameInfo(Item currentItem, ArrayList<Item> items) {

        String returnValue = "name:    {0}       seen: {1} times\n" +
                "=============       =============\n";

        //initialize counter at 0 and provide item loop logic to support above main method logic; if that item is the same as the
        //current item then increment the counter which will be used below to denote the number of times that item was seen
        int counter = 0;
        for (Item item : items) {

            if (item.getName().compareToIgnoreCase(currentItem.getName()) == 0) {
                counter++;
            }
        }

        //replace the placeholders in the returnValue string supplied above with the name of the current item and the counters
        //value (denoting number of times item was seen).
        returnValue = returnValue.replace("{0}", currentItem.getName());
        returnValue = returnValue.replace("{1}", Integer.toString(counter));

        return returnValue;
    }

    static private String aggregatePriceInfo(Item currentItem, ArrayList<Item> items) {

        HashMap<Double, Integer> myMap = new HashMap<Double, Integer>();

        String returnFormat = "Price:    {0}       seen: {1} times\n" +
                "-------------       -------------\n";

        //similar logic as described above for aggregateNameInfo, however, in this case the there are multiple prices to keep track
        //of per item so a HashMap with Double (for price) and Integer (for the "seen" statistic) is ideal for storing this
        for (Item item : items) {

            if (item.getName().compareToIgnoreCase(currentItem.getName()) == 0) {

                //when the map contains a double value for price we want to update the integer for the "seen" value to reflect
                //each time the price was seen per item, else we want to insert it along with integer of 1 since each additional
                //price is seen only once
                if (myMap.containsKey(item.getPrice())) {
                    myMap.put(item.getPrice(), myMap.get(item.getPrice()) + 1);
                } else {
                    myMap.put(item.getPrice(), 1);
                }
            }
        }

        //returnValue initialized to the returnFormat above and replace the placeholders with Double and Integer
        //values stored in the above hash map, then utilize a string builder to append alterations to returnValue and return
        //this string builder toString for output in main
        StringBuilder sb = new StringBuilder();

        for (Double key : myMap.keySet()) {
            String returnValue = returnFormat;
            returnValue = returnValue.replace("{0}", Double.toString(key));
            returnValue = returnValue.replace("{1}", Integer.toString(myMap.get(key)));

            sb.append(returnValue);
        }

        return sb.toString();
    }

    static private String aggregateExceptionInfo(ArrayList<ItemParseException> exceptions) {

        //format created with placeholder to replace with the number of thrown exceptions recorded in previously created
        //ItemParseExceptions array list (4 total)
        String returnValue = "\nErrors             seen: {0} times\n";

        returnValue = returnValue.replace("{0}", Integer.toString(exceptions.size()));

        return returnValue;
    }
}
