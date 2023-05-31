package com.nighthawk.spring_portfolio.mvc.stock;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

// Built using article: https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html
// or similar: https://asbnotebook.com/2020/04/11/spring-boot-thymeleaf-form-validation-example/
@Controller
@RequestMapping("/mvc/stock")
public class StockViewController {
    // Autowired enables Control to connect HTML and POJO Object to database easily for CRUD
    @Autowired
    private StockJpaRepository repository;

    @GetMapping("/read")
    public String stock(Model model) {
        List<Stock> list = repository.findAll();
        model.addAttribute("list", list);
        return "stock/read";
    }

    @GetMapping("/delete/{id}")
    public String StockDelete(@PathVariable("id") long id) {
        repository.deleteById(id);
        return "redirect:/mvc/stock/read";
    }

    //make a controller method for an update to refresh the prices on the table



    @PostMapping("/simulate")
    public String simulate(Model model, @RequestParam(name="symbol1", required=false, defaultValue="AMZN") String symbol1,
                           @RequestParam(name="buyPrice1", required=false, defaultValue="34") float buyPrice1,
                           @RequestParam(name="quantity1", required=false, defaultValue="100") int quantity1,
                           @RequestParam(name="symbol2", required=false, defaultValue="AMZN") String symbol2,
                           @RequestParam(name="buyPrice2", required=false, defaultValue="34") float buyPrice2,
                           @RequestParam(name="quantity2", required=false, defaultValue="100") int quantity2,
                           @RequestParam(name="symbol3", required=false, defaultValue="AMZN") String symbol3,
                           @RequestParam(name="buyPrice3", required=false, defaultValue="34") float buyPrice3,
                           @RequestParam(name="quantity3", required=false, defaultValue="100") int quantity3) {

        //Lookup the current price of the 3 symbols in the api
        model.addAttribute("symbol1", symbol1);
        model.addAttribute("buyPrice1", buyPrice1);
        model.addAttribute("quantity1", quantity1);
        Stock stock1 = getStockPrice(symbol1);
        float currentPrice1 = stock1.getCompanyPrice();
        model.addAttribute("currentPrice1", currentPrice1);
        model.addAttribute("name1", stock1.getCompanyName());

        float profitLoss1 = calculateProfitLoss(symbol1, buyPrice1, quantity1, currentPrice1);
        model.addAttribute("profitLoss1", profitLoss1);

        repository.save(stock1);

        model.addAttribute("symbol2", symbol2);
        model.addAttribute("buyPrice2", buyPrice2);
        model.addAttribute("quantity2", quantity2);
        Stock stock2 = getStockPrice(symbol2);
        float currentPrice2 = stock2.getCompanyPrice();
        model.addAttribute("currentPrice2", currentPrice2);
        model.addAttribute("name2", stock2.getCompanyName());

        float profitLoss2 = calculateProfitLoss(symbol2, buyPrice2, quantity2, currentPrice2);
        model.addAttribute("profitLoss2", profitLoss2);

        repository.save(stock2);

        model.addAttribute("symbol3", symbol3);
        model.addAttribute("buyPrice3", buyPrice3);
        model.addAttribute("quantity3", quantity3);
        Stock stock3 = getStockPrice(symbol3);
        float currentPrice3 = stock3.getCompanyPrice();
        model.addAttribute("currentPrice3", currentPrice3);
        model.addAttribute("name3", stock3.getCompanyName());

        float profitLoss3 = calculateProfitLoss(symbol3, buyPrice3, quantity3, currentPrice3);
        model.addAttribute("profitLoss3", profitLoss3);

        repository.save(stock3);

        return "stock/simulate";
    }


    private float calculateProfitLoss(String symbol, float buyPrice, int quantity, float currentPrice) {
        float profitLoss = (currentPrice - buyPrice) * quantity;
        return profitLoss;
    }

    @GetMapping("/simulate")
    public String displaySimulate() {
        return "stock/simulate";
    }

    private Stock getStockPrice(String symbol){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://investing4.p.rapidapi.com/stock/overview"))
                .header("content-type", "application/json")
                .header("X-RapidAPI-Key", "39c4bf8c2emsh30b02ab6dc01dd9p13f427jsn690a650cf2ec")
                .header("X-RapidAPI-Host", "investing4.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"country\": \"united states\",\n    \"symbol\": \"" +symbol + "\"\n}"))
                .build();
        HttpResponse<String> response = null;
        Stock stock = new Stock();
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject body = (JSONObject) new JSONParser().parse(response.body());
            String priceStr = (String) ((JSONObject)body.get("data")).get("Price");
            float price = Float.parseFloat(priceStr);
            String name = (String) ((JSONObject)body.get("data")).get("Name");
            stock.setCompanyPrice(price);
            stock.setSymbol(symbol);
            stock.setCompanyName(name);


        } catch (IOException | InterruptedException |ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
        return stock;

    }



}