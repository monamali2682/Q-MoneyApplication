
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

public class TiingoService implements StockQuotesService {
  //public final static String token = "40ca5a4cfa8620713885f6cce25b02abf6c24004";  saket
  //public final static String token = "41d27a76d414a09be33e5b96e326df9f406e0876"; monika
  public final static String token = "3fb5ead0bff913defd942151957ca85688fd3067"; 
  RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    //  String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
    //       + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
          
          String start = startDate.toString();
          String end = endDate.toString();
          String url = "https://api.tiingo.com/tiingo/daily/" + symbol+ "/prices?startDate=" + start + "&endDate="
              + "&endDate=" + end + "&token=" + token;
          return url;
  } 
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException, RuntimeException {
        if(from.compareTo(to)>=0){
          throw new RuntimeException("End date should be more than purchase date");
        }
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        List<Candle> candles = new ArrayList<>();
        String url =  buildUri(symbol, from, to);
        // RestTemplate rt = new RestTemplate();
        try {
          String candlestr = restTemplate.getForObject(url,String.class);
          Candle[] candlesArr = objMapper.readValue(candlestr,TiingoCandle[].class);
          candles = Arrays.asList(candlesArr);
        } 
        catch (NullPointerException e) {
          throw new StockQuoteServiceException("TiingoCandle service returned invalid response",e.getCause());
        }
        return candles;
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.






  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
