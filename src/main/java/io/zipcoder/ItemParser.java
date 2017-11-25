package io.zipcoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ItemParser {


    public ArrayList<String> parseRawDataIntoStringArray(String rawData) {
        String stringPattern = "##";
        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawData);
        return response;
    }

    private ArrayList<String> splitStringWithRegexPattern(String stringPattern, String inputString) {
        return new ArrayList<String>(Arrays.asList(inputString.split(stringPattern)));
    }


    public Item parseStringIntoItem(String rawItem) throws ItemParseException {

        //apply various regex patterns to match the jerkSON syntax of each item per its 4 properties, then apply logic defined below
        //in findValueForPattern; wrap in try-catch for exception handling
        try {
            String namePattern = "(^[n][a][m][e]:[a-z].+?;)";
            String name = findValueForPattern(namePattern, rawItem);

            String pricePattern = "([p][r][i][c][e]:[0-9]+\\.[0-9].+?;)";
            String price = findValueForPattern(pricePattern, rawItem);

            String typePattern = "([t][y][p][e]:[a-z].+?[;|^|%|\\\\*|!|@])";
            String type = findValueForPattern(typePattern, rawItem);

            String expirationDatePattern = "([e][x][p][i][r][a][t][i][o][n]:\\d{1,2}[\\/-]\\d{1,2}[\\/-]\\d{4})";
            String expirationDate = findValueForPattern(expirationDatePattern, rawItem);

            //once regex patterns are applied, method returns a new Item with appropriate properties to be used for outputting;
            //capitalizeString() and replace() for "name" output formatting purpose (more explained more in capitalizeString method body
            //and parseDouble required to parse String price to a double for price outputting purposes
            return new Item(capitalizeString(name).replace("0", "o"), Double.parseDouble(price), type, expirationDate);

            //otherwise we throw a parse exception up the chain to be stored and printed with output in the Main > main method
        } catch (ItemParseException e) {
            throw e;

        }
    }

    public String findValueForPattern(String stringPattern, String inputString) throws ItemParseException {

        //setup pattern/matcher for regex application, use case_insensitive to alleviate unnecessary verbosity in above regex patterns
        Pattern p = Pattern.compile(stringPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(inputString);
        String returnValue = "";

        //if matcher finds what it is looking for then we want to split that finding at the ":" as all 4 individual item properties are
        //separated via ":", then we want to look at the second part of that split (index 1)
        if (m.find()) {
            String[] splitValue = m.group(0).split(":");
            returnValue = splitValue[1];

            //once we have identified the property (or value) which follows the ":" we determine that it is whatever
            //exists between that ":" and the next ";" by searching for ";" and reducing the string down to whatever leads up to it
            if (returnValue.contains(";"))
                returnValue = returnValue.substring(0, returnValue.length() - 1);

            //otherwise we know the item is missing that property/value so we throw a parse exception to be handled (4 total)
        } else {
            throw new ItemParseException("Cannot parse this item: " + inputString);

        }

        return returnValue;
    }

    private String capitalizeString(String inputString) {

        //This method was created to accomplish proper formatting in an effort to match output of this program with the supplied
        // output.txt... food items had random capital and lower-case letters but need each to be displayed
        //with first letter capitalized and the rest lower-case to match desired output
        String returnString = "";
        returnString = inputString.substring(0, 1).toUpperCase();
        returnString += inputString.substring(1, inputString.length()).toLowerCase();

        return returnString;
    }


    // Below previously supplied method is not used, see comment for ItemParserTest for explanation

//    public ArrayList<String> findKeyValuePairsInRawItemData(String rawItem) {
//        String stringPattern = "[;|^]";
//        ArrayList<String> response = splitStringWithRegexPattern(stringPattern, rawItem);
//        return response;
//    }

}
