
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.security.KeyStore.Entry;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

// import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
// import com.crio.warmup.stock.dto.Candle;
// import com.crio.warmup.stock.exception.StockQuoteServiceException;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import java.time.LocalDate;
// import java.util.Comparator;
// import java.util.List;
// import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  static class CandleComparator implements Comparator<Candle>{
    @Override
    public int compare(Candle candle1, Candle candle2) {
      if(candle1.getDate().isBefore(candle2.getDate())){
        return -1;
      }
      else if(candle1.getDate().isAfter(candle2.getDate())){
        return 1;
      }
      return 0;
    }
  }

  public final static String token = "3MZ3EVYUTLO4UO5S"; 
  RestTemplate restTemplate;

  protected AlphavantageService (RestTemplate restTemplate){
    this.restTemplate=restTemplate;
  }
  
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    //  String uriTemplate = https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED
    // &symbol=IBM&outputsize=full&apikey=3MZ3EVYUTLO4UO5S;
          
          // String start = startDate.toString();
          // String end = endDate.toString();
          String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol
            + "&outputsize=full&apikey=" + token;
          return url;
  } 

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,StockQuoteServiceException,RuntimeException {
        List<Candle> candles = new ArrayList<>();
        if(from.compareTo(to)>=0){
          throw new RuntimeException("End date should be more than purchase date");
        }
        String url =  buildUri(symbol, from, to);
        try {
          String jsonString = restTemplate.getForObject(url,String.class);
          //System.out.println(jsonString);
          candles = getAllcandles(jsonString,from,to);
          Collections.sort(candles,new CandleComparator());
        } 
        catch (NullPointerException e) {
          throw new StockQuoteServiceException("Alphavantage returned invalid response", e.getCause());
        }
        return candles;
  }

  public List<Candle> getAllcandles(String jsonString,LocalDate from, LocalDate to) throws JsonMappingException, JsonProcessingException,NullPointerException{
        //try{
          ObjectMapper objMapper = new ObjectMapper();
          objMapper.registerModule(new JavaTimeModule());
          AlphavantageDailyResponse alphavantageDailyResponse = objMapper.readValue(jsonString, AlphavantageDailyResponse.class);
          Map<LocalDate, AlphavantageCandle> candlemap = alphavantageDailyResponse.getCandles();
          List<Candle> candles= new ArrayList<>();
          for(Map.Entry<LocalDate, AlphavantageCandle> entry :candlemap.entrySet()){
            LocalDate curr = entry.getKey();
            if( ( ! curr.isBefore(from) ) && ( ! curr.isAfter( to) )){
              Candle c =  entry.getValue();
              c.setDate(entry.getKey());
              candles.add(c);
            }
          }
        //}
        // catch(NullPointerException e){
        //   throw new NullPointerException();
        // }
        return candles;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //   1. Update the method signature to match the signature change in the interface.
  //   2. Start throwing new StockQuoteServiceException when you get some invalid response from
  //      Alphavantage, or you encounter a runtime exception during Json parsing.
  //   3. Make sure that the exception propagates all the way from PortfolioManager, so that the
  //      external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}

