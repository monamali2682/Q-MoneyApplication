
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  
  private RestTemplate restTemplate;
  public final static String token = "3fb5ead0bff913defd942151957ca85688fd3067"; 
  private StockQuotesService StockQuoteService;
  


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.StockQuoteService = stockQuotesService;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
  }

  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size() - 1).getClose();
  }

  // public static String getToken(){
  //   return token;
  // }

  // public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
  //     throws JsonProcessingException {
  //       List<Candle> candles = new ArrayList<>();
  //       String url =  buildUri(symbol, from, to);
  //       // RestTemplate rt = new RestTemplate();
  //       Candle[] candlesArr = restTemplate.getForObject(url,TiingoCandle[].class);
  //       candles = Arrays.asList(candlesArr);
  //       return candles;
  // }
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        List<Candle> candles = StockQuoteService.getStockQuote(symbol, from, to);
        // String url =  buildUri(symbol, from, to);
        // // RestTemplate rt = new RestTemplate();
        // Candle[] candlesArr = 
        // candles = Arrays.asList(candlesArr);
        return candles;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
      //       + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
            
            String start = startDate.toString();
            String end = endDate.toString();
            String url = "https://api.tiingo.com/tiingo/daily/$" + symbol+ "/prices?startDate=$" + start + "&endDate=$"
                + end + "&token=$" + token;
            return url;
  }

  public static AnnualizedReturn calculateAnnualizedReturnOfStock(LocalDate endDate,
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

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> trades,
      LocalDate enddate) throws JsonProcessingException {
    
        // String filename = args[0];
        // LocalDate enddate = LocalDate.parse(args[1]);
        // List<PortfolioTrade> trades = readTradesFromJson(filename);
        List<AnnualizedReturn> ans = new ArrayList<>();

        for(PortfolioTrade trade: trades){
          String symbol = trade.getSymbol();
          LocalDate startDate = trade.getPurchaseDate();
          List<Candle> candles = getStockQuote(symbol,startDate, enddate);
          Double buyPrice = getOpeningPriceOnStartDate(candles);
          Double sellPrice = getClosingPriceOnEndDate(candles);
          AnnualizedReturn e = calculateAnnualizedReturnOfStock(enddate, trade, buyPrice, sellPrice);
          ans.add(e);
        } 
        Collections.sort(ans, getComparator());
        return ans;
  }



  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
