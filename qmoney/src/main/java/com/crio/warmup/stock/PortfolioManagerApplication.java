package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;

import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.NestedRuntimeException;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.
  //public final static String token = "40ca5a4cfa8620713885f6cce25b02abf6c24004";  saket
  //public final static String token = "41d27a76d414a09be33e5b96e326df9f406e0876"; monika
  public final static String token = "3fb5ead0bff913defd942151957ca85688fd3067"; 
  
  

  static class StockComparator implements Comparator<TotalReturnsDto>{
    @Override
    public int compare(TotalReturnsDto dto1, TotalReturnsDto dto2) {
      if(dto1.getClosingPrice()<dto2.getClosingPrice()){
        return -1;
      }
      else if(dto1.getClosingPrice()>dto2.getClosingPrice()){
        return 1;
      }
      return 0;
    }
  }

  static class ReturnComparator implements Comparator<AnnualizedReturn>{
    @Override
    public int compare(AnnualizedReturn ret1, AnnualizedReturn ret2) {
      if(ret1.getAnnualizedReturn()<ret2.getAnnualizedReturn()){
        return 1;
      }
      else if(ret1.getAnnualizedReturn()>ret2.getAnnualizedReturn()){
        return -1;
      }
      return 0;
    }
  }

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    List<String>ans = new ArrayList<>();
    ObjectMapper objectMapper =  getObjectMapper();
    File inputFile = resolveFileFromResources(args[0]);
    PortfolioTrade[] portfolioTrade = objectMapper.readValue(inputFile, PortfolioTrade[].class);
    for(PortfolioTrade trade: portfolioTrade){
      ans.add(trade.getSymbol());
    }
    return ans;
  }
  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException ,IOException, RuntimeException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues
  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";


    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/shivanshs977-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5542c4ed";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "29";
// 20fc3ace9581a5beb1594642fee6a83d67644b07  api token

   return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
       toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
       lineNumberFromTestFileInStackTrace});
  }


//            MODULE 2 
//   Now that you are all setup with Tiingo, use it to retrieve the closing price of stocks on a given (historical) date. Create URLs using your API token to collect this data.
// The end date is passed as the second argument in the input parameters.
// Modify mainReadQuotes(String[] args) by reading the TODOs.
// Sort the stocks in ascending order of their closing price.
// Return the list of sorted stocks.
// Test your code by running the following command in the terminal:

  // public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
  //   String url = prepareUrl(trade, endDate, token);
  //   RestTemplate restTemplate = new RestTemplate();
  //   Candle[] candles = restTemplate.getForObject(url, TiingoCandle[].class);
  //   return Arrays.asList(candles); 
  // }
  
  // String  apiToken = "41d27a76d414a09be33e5b96e326df9f406e0876";
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  
  public static List<String> mainReadQuotes(String[] args) throws Exception,NestedRuntimeException, RuntimeException,IOException, URISyntaxException {    
       List<PortfolioTrade> trades = readTradesFromJson(args[0]);  
       //String token = "40ca5a4cfa8620713885f6cce25b02abf6c24004";
       LocalDate enddate = LocalDate.parse(args[1]);
       ArrayList<TotalReturnsDto> TotalReturnsDtoList = new ArrayList<>();
       for(PortfolioTrade trade : trades){
        List<Candle> candles = fetchCandles(trade, enddate, token);
        double closingPrice = getClosingPriceOnEndDate(candles);
        TotalReturnsDto dto = new TotalReturnsDto(trade.getSymbol(), closingPrice);
        TotalReturnsDtoList.add(dto);
       }
       
       Collections.sort(TotalReturnsDtoList,new StockComparator());
       ArrayList<String> ans = new ArrayList<>(); 
       for(TotalReturnsDto dto : TotalReturnsDtoList){
        ans.add(dto.getSymbol());
       }
       return ans;     
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    List<PortfolioTrade>ans= new ArrayList<>();
    ObjectMapper objectMapper = getObjectMapper();
    File file = resolveFileFromResources(filename);
    PortfolioTrade[] trade = objectMapper.readValue(file, PortfolioTrade[].class);
    for(PortfolioTrade trades:trade){
      ans.add(trades);
    }
    return ans;
  }

  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token){
    //LocalDate startDate = endDate.minusDays(1);
    LocalDate startDate = trade.getPurchaseDate();
    String start = startDate.toString();
    String end = endDate.toString();
    String url = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol()+ "/prices?startDate=" + start + "&endDate="
        + end + "&token=" + token;
    return url;
  }

  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }

  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() - 1).getClose();
  }

  public static String getToken(){
    return token;
  }

  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
        List<Candle> candles = new ArrayList<>();
        String url =  prepareUrl(trade, endDate, token);
        RestTemplate rt = new RestTemplate();
        Candle[] candlesArr = rt.getForObject(url,TiingoCandle[].class);
        candles = Arrays.asList(candlesArr);
        return candles;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {

        String filename = args[0];
        LocalDate enddate = LocalDate.parse(args[1]);
        List<PortfolioTrade> trades = readTradesFromJson(filename);
        List<AnnualizedReturn> ans = new ArrayList<>();

        for(PortfolioTrade trade: trades){
          List<Candle> candles = fetchCandles(trade, enddate, token);
          Double buyPrice = getOpeningPriceOnStartDate(candles);
          Double sellPrice = getClosingPriceOnEndDate(candles);
          AnnualizedReturn e = calculateAnnualizedReturns(enddate, trade, buyPrice, sellPrice);
          ans.add(e);
        } 
        Collections.sort(ans, new ReturnComparator());
        return ans;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
    
        LocalDate startDate = trade.getPurchaseDate();
        Double totalReturns = (sellPrice-buyPrice)/buyPrice;
        long days =  startDate.until(endDate,ChronoUnit.DAYS);
        double years = (double)days/(double)365;
        Double x= 1.0+totalReturns;
        Double y= (1.0/years);
        
        Double annualizedReturn = Math.pow(x, y)-1.0;
        return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturns);
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainCalculateSingleReturn(args));

  }
}

