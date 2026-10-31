package com.example.trade;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapImpl;

import com.oanda.v20.Context;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.trade.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oanda.v20.instrument.CandlestickGranularity.*;

@SpringBootApplication
@Controller
public class TradeApplication {
    private Context ctx() {
        if (Config.TOKEN.isBlank() || Config.ACCOUNT_ID.isBlank()) {
            throw new IllegalStateException("OANDA practice credentials are missing. Configure OANDA_ACCOUNT_ID and OANDA_TOKEN or password.txt.");
        }
        return new Context(Config.URL, Config.TOKEN);
    }

    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/account_info")
    public String accountInfo(Model model) {
        try {
            model.addAttribute("account", ctx().account.summary(Config.ACCOUNTID).getAccount());
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to retrieve OANDA practice account information: " + ex.getMessage());
        }
        return "account_info";
    }


    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }



    @GetMapping("/actual_prices")
    public String actual_prices(Model model) {
        model.addAttribute("par", new MessageActPrice());
        return "form_actual_prices";
    }

    @PostMapping("/actual_prices")
    public String actual_prices2(
            @ModelAttribute MessageActPrice messageActPrice,
            Model model) {

        model.addAttribute("instr", messageActPrice.getInstrument());
        try {
            List<String> instruments = List.of(messageActPrice.getInstrument());
            PricingGetResponse response = ctx().pricing.get(
                    new PricingGetRequest(Config.ACCOUNTID, instruments));
            List<Map<String, String>> quotes = new ArrayList<>();
            for (ClientPrice price : response.getPrices()) {
                String bid = price.getBids() == null || price.getBids().isEmpty()
                        ? "Unavailable" : String.valueOf(price.getBids().get(0).getPrice());
                String ask = price.getAsks() == null || price.getAsks().isEmpty()
                        ? "Unavailable" : String.valueOf(price.getAsks().get(0).getPrice());
                quotes.add(Map.of("time", String.valueOf(price.getTime()),
                        "bid", bid, "ask", ask,
                        "instrument", String.valueOf(price.getInstrument())));
            }
            model.addAttribute("quotes", quotes);
            if (quotes.isEmpty()) model.addAttribute("error", "No quotes returned for this instrument.");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to retrieve actual prices: " + e.getMessage());
        }
        return "result_actual_prices";
    }

    @GetMapping("/hist_prices")
    public String hist_prices(Model model) {
        model.addAttribute("param", new MessageHistPrice());
        return "form_hist_prices";
    }

    @PostMapping("/hist_prices")
    public String hist_prices2(
            @ModelAttribute MessageHistPrice messageHistPrice,
            Model model) {

        model.addAttribute("instr", messageHistPrice.getInstrument());
        model.addAttribute("granularity", messageHistPrice.getGranularity());
        try {
            InstrumentCandlesRequest request = new InstrumentCandlesRequest(
                    new InstrumentName(messageHistPrice.getInstrument()));
            switch (messageHistPrice.getGranularity()) {
                case "M1": request.setGranularity(M1); break;
                case "H1": request.setGranularity(H1); break;
                case "D": request.setGranularity(D); break;
                case "W": request.setGranularity(W); break;
                case "M": request.setGranularity(M); break;
                default: throw new IllegalArgumentException("Invalid granularity");
            }
            request.setCount(10L);
            InstrumentCandlesResponse response = ctx().instrument.candles(request);
            List<Map<String, String>> candles = new ArrayList<>();
            for (Candlestick candle : response.getCandles()) {
                if (candle.getMid() != null) {
                    candles.add(Map.of("time", String.valueOf(candle.getTime()),
                            "close", String.valueOf(candle.getMid().getC())));
                }
            }
            model.addAttribute("candles", candles);
            if (candles.isEmpty()) model.addAttribute("error", "No historical prices returned.");
        } catch (Exception e) {
            model.addAttribute("error", "Unable to retrieve historical prices: " + e.getMessage());
        }
        return "result_hist_prices";
    }

    @GetMapping("/open_position")
    public String open_position(Model model) {
        model.addAttribute("param", new MessageOpenPosition());
        return "form_open_position";
    }

    @PostMapping("/open_position")
    public String open_position2(
            @ModelAttribute MessageOpenPosition messageOpenPosition,
            Model model) {

        model.addAttribute("instr", messageOpenPosition.getInstrument());
        model.addAttribute("units", messageOpenPosition.getUnits());
        if (messageOpenPosition.getUnits() == 0) {
            model.addAttribute("error", "Units must not be zero.");
            return "result_open_position";
        }
        try {
            InstrumentName instrument = new InstrumentName(messageOpenPosition.getInstrument());
            OrderCreateRequest request = new OrderCreateRequest(Config.ACCOUNTID);
            MarketOrderRequest marketOrderRequest = new MarketOrderRequest();
            marketOrderRequest.setInstrument(instrument);
            marketOrderRequest.setUnits(messageOpenPosition.getUnits());
            request.setOrder(marketOrderRequest);
            OrderCreateResponse response = ctx().order.create(request);
            model.addAttribute("id", "Order fill transaction ID: "
                    + response.getOrderFillTransaction().getId());
        } catch (Exception e) {
            model.addAttribute("error", "Could not open practice trade: " + e.getMessage());
        }
        return "result_open_position";
    }

    @GetMapping("/positions")
    public String positions(Model model) {
        try {
            model.addAttribute("trades", ctx().trade.listOpen(Config.ACCOUNTID).getTrades());
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to retrieve open positions: " + ex.getMessage());
        }
        return "positions";
    }

    @GetMapping("/close_position")
    public String close_position(Model model) {
        model.addAttribute("param", new MessageClosePosition());
        return "form_close_position";
    }

    @PostMapping("/close_position")
    public String close_position2(
            @ModelAttribute MessageClosePosition messageClosePosition,
            Model model) {

        String tradeId = String.valueOf(messageClosePosition.getTradeId());
        if (messageClosePosition.getTradeId() <= 0) {
            model.addAttribute("error", "Enter a positive Trade ID from Open Positions; 0 is not valid.");
            return "result_close_position";
        }
        try {
            ctx().trade.close(new TradeCloseRequest(
                    Config.ACCOUNTID, new TradeSpecifier(tradeId)));
            model.addAttribute("tradeId", "Closed trade ID: " + tradeId);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to close trade " + tradeId
                    + ". Check Open Positions and market availability. Details: " + e.getMessage());
        }
        return "result_close_position";
    }

    @GetMapping("/exercise")
    public String exercise(Model model) {
        model.addAttribute("param", new MessagePrice());
        return "form";
    }

    @PostMapping("/exercise")
    public String exercise2(
            @ModelAttribute MessagePrice messagePrice,
            Model model) {
        try {
            MNBArfolyamServiceSoapImpl serviceImpl =
                    new MNBArfolyamServiceSoapImpl();

            MNBArfolyamServiceSoap service =
                    serviceImpl.getCustomBindingMNBArfolyamServiceSoap();

            String sendOut = service.getExchangeRates(
                    messagePrice.getStartDate(),
                    messagePrice.getEndDate(),
                    messagePrice.getCurrency());

            model.addAttribute("currency", messagePrice.getCurrency());
            model.addAttribute("sendOut", sendOut);

            return "result";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}