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
                           @RequestParam(name="quantity1", required=false, defaultValue="100") String quantity1) {

        //Lookup the current price of the 3 symbols in the api
        float currentPrice1 = getStockPrice(symbol1);
        model.addAttribute("currentPrice1", currentPrice1);

        private float calculateProfitLoss(String symbol, float buyPrice, int quantity) {
            float currentPrice = getStockPrice(symbol);
            float profitLoss = (currentPrice - buyPrice) * quantity;
            return profitLoss;
        }


        // float currentPrice = stockService.getCurrentPrice(symbol1);

        //Calculate profit/loss based on buyPrice, current price, and quantity
      //  float profitLoss = (currentPrice - buyPrice1) * quantity1;

        //Save the profit/loss amounts to the model, so it goes to the html
       // model.addAttribute("profitLoss", profitLoss);

        //Calculate profit/loss based on buyPrice, current price, and quantity

        //Save the profit/loss amounts to the model, so it goes to the html
        // e.g. model.addAttribute("profit1", profit1);

        return "stock/simulate";
    }

    @GetMapping("/simulate")
    public String simulateProcess() {
        return "stock/simulate";
    }

    private float getStockPrice(String symbol){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://investing4.p.rapidapi.com/stock/overview"))
                .header("content-type", "application/json")
                .header("X-RapidAPI-Key", "39c4bf8c2emsh30b02ab6dc01dd9p13f427jsn690a650cf2ec")
                .header("X-RapidAPI-Host", "investing4.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"country\": \"united states\",\n    \"symbol\": \"" +symbol + "\"\n}"))
                .build();
        HttpResponse<String> response = null;
        float price = -1;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject body = (JSONObject) new JSONParser().parse(response.body());
            String priceStr = (String) ((JSONObject)body.get("data")).get("Price");
            price = Float.parseFloat(priceStr);

        } catch (IOException | InterruptedException |ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
        return price;

    }



}